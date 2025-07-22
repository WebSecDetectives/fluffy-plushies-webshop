package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException;
import com.sirmeows.fluffyinventoryservice.exception.ReviewNotFoundException;
import com.sirmeows.fluffyinventoryservice.repository.ItemRepository;
import com.sirmeows.fluffyinventoryservice.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private ItemRepository itemRepository;

    public Review createReview(UUID itemId, Review review) {
        var item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        review.setItem(item);
        return reviewRepository.save(review);
    }

    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByItemId(UUID itemId) {
        return reviewRepository.findByItem_Id(itemId);
    }

    public Review getReview(UUID id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }
}
