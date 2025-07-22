package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.dto.ReviewRequestDto;
import com.sirmeows.fluffyinventoryservice.dto.ReviewResponseDto;
import com.sirmeows.fluffyinventoryservice.entity.Review;
import com.sirmeows.fluffyinventoryservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.sirmeows.fluffyinventoryservice.config.ModelMapperConfig.LIST_TYPE_REVIEW_RESPONSE_DTO;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public List<ReviewResponseDto> getReviews() {
        return modelMapper.map(reviewService.getReviews(), LIST_TYPE_REVIEW_RESPONSE_DTO);
    }

    @GetMapping("/{id}")
    public ReviewResponseDto getReview(@PathVariable UUID id) {
        return modelMapper.map(reviewService.getReview(id), ReviewResponseDto.class);
    }

    @GetMapping("/item/{itemId}")
    public List<ReviewResponseDto> getReviewsByItemId(@PathVariable UUID itemId) {
        return modelMapper.map(reviewService.getReviewsByItemId(itemId), LIST_TYPE_REVIEW_RESPONSE_DTO);
    }

    @PostMapping("/item/{itemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ReviewResponseDto createReview(@PathVariable UUID itemId,
                                          @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        var review = reviewService.createReview(itemId, modelMapper.map(reviewRequestDto, Review.class));
        return modelMapper.map(review, ReviewResponseDto.class);
    }
}
