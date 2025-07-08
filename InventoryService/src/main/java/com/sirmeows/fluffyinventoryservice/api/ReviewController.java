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

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/review")
public class ReviewController {

    private ReviewService reviewService;
    private final ModelMapper modelMapper;

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ReviewResponseDto createReview(@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        var review = reviewService.creteReview(modelMapper.map(reviewRequestDto, Review.class));
        return modelMapper.map(review, ReviewResponseDto.class);
    }
}
