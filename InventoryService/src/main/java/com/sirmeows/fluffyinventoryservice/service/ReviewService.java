package com.sirmeows.fluffyinventoryservice.service;

import com.sirmeows.fluffyinventoryservice.dto.ReviewUpdateDto;
import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.exception.ReviewAccessDeniedException;
import com.sirmeows.fluffyinventoryservice.exception.ReviewNotFoundException;
import com.sirmeows.fluffyinventoryservice.repository.ReviewRepository;
import com.sirmeows.fluffyinventoryservice.security.AuthUser;
import com.sirmeows.fluffyinventoryservice.security.Role;
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
     * the item is absent or hidden, so a review can't be attached to — or used to probe — a
     * PRIVATE item the caller can't access. The reviewer is always the authenticated caller,
     * never taken from the request body; the endpoint requires an authenticated USER/ADMIN,
     * so {@code caller} is never null here.
     */
    public Review createReview(UUID itemId, Review review, AuthUser caller) {
        var item = itemService.getVisibleItem(itemId, caller);
        review.setItem(item);
        review.setReviewerId(caller.id());
        return reviewRepository.save(review);
    }

    /**
     * Updates a review the caller authored, or — for an ADMIN — any review (moderation).
     * Only non-null patch fields are applied.
     */
    public Review updateReview(UUID id, ReviewUpdateDto patch, AuthUser caller) {
        var review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        assertCanModify(review, caller);

        if (patch.getReviewText() != null) review.setReviewText(patch.getReviewText());
        if (patch.getRating() != null) review.setRating(patch.getRating());

        return reviewRepository.save(review);
    }

    public void deleteReview(UUID id, AuthUser caller) {
        var review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        assertCanModify(review, caller);
        reviewRepository.delete(review);
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

    /**
     * A review may be modified by its author or by an ADMIN (moderation); anyone else is
     * forbidden (403). The caller is non-null here — the mutating endpoints require USER or ADMIN.
     */
    private void assertCanModify(Review review, AuthUser caller) {
        boolean isAuthor = caller != null && caller.id().equals(review.getReviewerId());
        boolean isAdmin = caller != null && caller.role() == Role.ADMIN;
        if (!isAuthor && !isAdmin) {
            throw new ReviewAccessDeniedException(review.getId());
        }
    }
}
