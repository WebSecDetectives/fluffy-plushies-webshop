package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemDetailsRequestDto.ItemDetailsRequestDtoBuilderImpl.class)
public class ItemDetailsRequestDto {
    @NotBlank
    private String description;

    @NotBlank
    private String ageGroup;

    @NotBlank
    private String itemType;

    @NotBlank
    private String material;

    @NotBlank
    @URL(protocol = "https")
    @Size(max = 2048)
    private String imgLink;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemDetailsRequestDtoBuilderImpl extends ItemDetailsRequestDto.ItemDetailsRequestDtoBuilder<ItemDetailsRequestDto, ItemDetailsRequestDto.ItemDetailsRequestDtoBuilderImpl> {
    }
}
