package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

    public Review creteReview(Review review) {
        return reviewRepository.save(review);
    }
}
