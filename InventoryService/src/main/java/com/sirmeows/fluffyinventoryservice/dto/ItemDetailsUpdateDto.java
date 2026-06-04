package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Partial-update payload for an item's details (PATCH). Every field is optional: an
 * omitted/null field leaves the stored value unchanged. Value constraints apply only when
 * a field is present (Jakarta constraints treat null as valid).
 *
 * Carries NO id. The details row is never selected by the client — the service merges
 * these fields onto the item's existing managed ItemDetails instance. This prevents
 * mass-assignment/IDOR (binding to a foreign details row) and avoids orphaning the
 * current row by replacing it with a new instance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemDetailsUpdateDto.ItemDetailsUpdateDtoBuilderImpl.class)
public class ItemDetailsUpdateDto {

    @Size(min = 1, max = 255)
    private String description;

    @Size(min = 1, max = 255)
    private String ageGroup;

    @Size(min = 1, max = 255)
    private String itemType;

    @Size(min = 1, max = 255)
    private String material;

    @URL(protocol = "https")
    @Size(max = 2048)
    private String imgUrl;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemDetailsUpdateDtoBuilderImpl extends ItemDetailsUpdateDto.ItemDetailsUpdateDtoBuilder<ItemDetailsUpdateDto, ItemDetailsUpdateDtoBuilderImpl> {
    }
}
