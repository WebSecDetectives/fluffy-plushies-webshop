package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ReviewRequestDto.ReviewRequestDtoBuilderImpl.class)
public class ReviewRequestDto {

    @NotBlank
    private String reviewText;

    @Min(1) @Max(5)
    private int rating;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReviewRequestDtoBuilderImpl extends ReviewRequestDto.ReviewRequestDtoBuilder<ReviewRequestDto, ReviewRequestDto.ReviewRequestDtoBuilderImpl> {
    }
}
