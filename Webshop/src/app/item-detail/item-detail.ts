import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ItemService } from '../items/item.service';
import { Item } from '../items/item.model';
import { ReviewService } from '../reviews/review.service';
import { Review } from '../reviews/review.model';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-item-detail',
  imports: [ReactiveFormsModule],
  templateUrl: './item-detail.html',
  styleUrls: ['./item-detail.css']
})
export class ItemDetail {
  private route = inject(ActivatedRoute);
  private itemService = inject(ItemService);
  private reviewService = inject(ReviewService);
  private fb = inject(FormBuilder);
  protected authService = inject(AuthService);

  readonly item = signal<Item | null>(null);
  readonly notFound = signal(false);
  readonly reviews = signal<Review[]>([]);
  readonly reviewError = signal<string | null>(null);
  readonly submittingReview = signal(false);

  /** Reviews can be written by users and admins, not merchants */
  readonly canReview = computed(() => {
    const role = this.authService.currentUser()?.role;
    return role === 'USER' || role === 'ADMIN';
  });

  reviewForm: FormGroup = this.fb.group({
    reviewText: ['', Validators.required],
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]]
  });

  private readonly itemId = this.route.snapshot.paramMap.get('id');

  constructor() {
    if (!this.itemId) {
      this.notFound.set(true);
      return;
    }
    this.itemService.getItem(this.itemId).subscribe({
      next: item => this.item.set(item),
      // Generic message on purpose: the backend answers 404 both for missing
      // items and for private items the caller may not see
      error: () => this.notFound.set(true)
    });
    this.loadReviews();
  }

  canDelete(review: Review): boolean {
    const user = this.authService.currentUser();
    return !!user && (user.role === 'ADMIN' || user.userId === review.reviewerId);
  }

  stars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  submitReview(event: Event): void {
    event.preventDefault();
    if (this.reviewForm.invalid || !this.itemId) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    this.submittingReview.set(true);
    this.reviewError.set(null);
    const v = this.reviewForm.value;
    // Native <select> values are strings; the backend expects a number
    const request = { reviewText: v.reviewText, rating: Number(v.rating) };
    this.reviewService.createReview(this.itemId, request).subscribe({
      next: () => {
        this.reviewForm.reset({ reviewText: '', rating: 5 });
        this.submittingReview.set(false);
        this.loadReviews();
      },
      error: () => {
        this.reviewError.set('Could not submit the review. Please try again.');
        this.submittingReview.set(false);
      }
    });
  }

  deleteReview(review: Review): void {
    this.reviewService.deleteReview(review.id).subscribe({
      next: () => this.loadReviews(),
      error: () => this.reviewError.set('Could not delete the review.')
    });
  }

  useFallbackImage(event: Event): void {
    (event.target as HTMLImageElement).src = '/toy-805814_1920.jpg';
  }

  private loadReviews(): void {
    if (!this.itemId) return;
    this.reviewService.getReviewsByItem(this.itemId).subscribe({
      next: reviews => this.reviews.set(reviews),
      // The item view already handles not-found; an empty list is fine here
      error: () => this.reviews.set([])
    });
  }
}
