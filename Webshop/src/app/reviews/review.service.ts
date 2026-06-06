import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Review, ReviewRequest } from './review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrlInventory}/reviews`;

  getReviewsByItem(itemId: string): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/item/${itemId}`);
  }

  createReview(itemId: string, request: ReviewRequest): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/item/${itemId}`, request);
  }

  deleteReview(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
