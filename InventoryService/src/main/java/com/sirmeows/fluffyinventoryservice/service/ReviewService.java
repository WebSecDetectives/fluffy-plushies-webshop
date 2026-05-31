package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.exception.ReviewNotFoundException;
import com.sirmeows.fluffyinventoryservice.repository.ReviewRepository;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private ItemService itemService;

    /**
     * Creates a review for an item the caller may see. {@code getVisibleItem} throws 404 if
     * the item is absent or hidden from the caller, so a review can't be attached to — or
     * used to probe — a PRIVATE item the caller can't access.
     */
    public Review createReview(UUID itemId, Review review, AuthUser caller) {
        var item = itemService.getVisibleItem(itemId, caller);
        review.setItem(item);
        return reviewRepository.save(review);
    }

    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    /**
     * Lists an item's reviews only if the caller may see the item; otherwise 404, so the
     * reviews don't leak the existence or contents of a PRIVATE item.
     */
    public List<Review> getReviewsByItemId(UUID itemId, AuthUser caller) {
        itemService.getVisibleItem(itemId, caller);
        return reviewRepository.findByItem_Id(itemId);
    }

    public Review getReview(UUID id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }
}
