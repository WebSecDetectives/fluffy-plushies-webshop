export interface Review {
  id: string;
  reviewerId: string;
  reviewText: string;
  rating: number;
}

export interface ReviewRequest {
  reviewText: string;
  rating: number;
}
