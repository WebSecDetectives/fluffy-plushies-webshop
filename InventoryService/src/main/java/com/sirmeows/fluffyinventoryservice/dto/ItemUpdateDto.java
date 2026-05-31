package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sirmeows.fluffyinventoryservice.entity.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Partial-update payload for an item (PATCH). Every field is optional: an omitted/null
 * field leaves the stored value unchanged — the ModelMapper skip-null condition merges
 * only non-null fields onto the existing entity. Value constraints still apply when a
 * field IS present (most Jakarta constraints treat null as valid).
 *
 * Deliberately excludes merchantId and id: ownership/identity are server-controlled and
 * must never be settable from a request body (mass-assignment protection). It also omits
 * fields that aren't editable here (e.g. nested details), so clients can only change what
 * this DTO exposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemUpdateDto.ItemUpdateDtoBuilderImpl.class)
public class ItemUpdateDto {

    @Size(min = 1, max = 255)
    private String name;

    @PositiveOrZero
    private BigDecimal price;

    @Min(0)
    private Integer stock; // boxed: omitted (null) means "unchanged", not 0

    private Visibility visibility;

    @Valid
    private ItemDetailsUpdateDto details;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemUpdateDtoBuilderImpl extends ItemUpdateDto.ItemUpdateDtoBuilder<ItemUpdateDto, ItemUpdateDtoBuilderImpl> {
    }
}
