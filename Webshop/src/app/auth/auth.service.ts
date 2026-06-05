import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../enviroments/enviroment';

export interface AuthResponse {
  token: string;
}

export interface CurrentUser {
  userId: string;
  username: string;
  role: 'USER' | 'MERCHANT' | 'ADMIN';
}

const TOKEN_KEY = 'jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  private readonly currentUserSignal = signal<CurrentUser | null>(this.readUserFromStoredToken());

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isLoggedIn = computed(() => this.currentUserSignal() !== null);
  readonly isMerchant = computed(() => this.currentUserSignal()?.role === 'MERCHANT');
  readonly isAdmin = computed(() => this.currentUserSignal()?.role === 'ADMIN');

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.baseUrlIdentity}/auth/login`, { username, password })
      .pipe(
        tap(response => {
          localStorage.setItem(TOKEN_KEY, response.token);
          this.currentUserSignal.set(this.decodeUser(response.token));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.currentUserSignal.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  /**
   * Decodes the stored token for display/UX state only. Authorization is always
   * enforced server-side; a tampered token just breaks the UI for the tamperer.
   */
  private readUserFromStoredToken(): CurrentUser | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) return null;

    const user = this.decodeUser(token);
    if (!user) {
      localStorage.removeItem(TOKEN_KEY);
    }
    return user;
  }

  private decodeUser(token: string): CurrentUser | null {
    try {
      // JWT payloads are base64url-encoded; atob expects standard base64
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(atob(base64));
      if (this.isExpired(payload.exp)) {
        return null;
      }
      return { userId: payload.sub, username: payload.username, role: payload.role };
    } catch {
      return null;
    }
  }

  private isExpired(exp: number | undefined): boolean {
    return exp !== undefined && exp * 1000 <= Date.now();
  }
}