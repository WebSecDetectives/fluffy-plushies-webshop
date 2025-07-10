package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ReviewRequestDto.ReviewRequestDtoBuilderImpl.class)
public class ReviewResponseDto {
    private String reviewText;
    private int rating;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReviewResponseDtoBuilderImpl extends ReviewResponseDto.ReviewResponseDtoBuilder<ReviewResponseDto, ReviewResponseDto.ReviewResponseDtoBuilderImpl> {
    }
}
