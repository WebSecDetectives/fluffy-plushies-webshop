import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'Fluffy Plushies';

  constructor(private router: Router, protected authService: AuthService) {
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  home() {
    this.router.navigate(['/']);
  }
}