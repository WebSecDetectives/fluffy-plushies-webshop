package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Partial-update payload for a review (PATCH). Fields are optional: an omitted/null field
 * leaves the stored value unchanged (value constraints apply only when present). Carries no
 * reviewerId/id — the author is fixed at creation and ownership is checked server-side.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ReviewUpdateDto.ReviewUpdateDtoBuilderImpl.class)
public class ReviewUpdateDto {

    @Size(min = 1, max = 2000)
    private String reviewText;

    @Min(1)
    @Max(5)
    private Integer rating; // boxed: omitted (null) means "unchanged"

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReviewUpdateDtoBuilderImpl extends ReviewUpdateDto.ReviewUpdateDtoBuilder<ReviewUpdateDto, ReviewUpdateDtoBuilderImpl> {
    }
}
