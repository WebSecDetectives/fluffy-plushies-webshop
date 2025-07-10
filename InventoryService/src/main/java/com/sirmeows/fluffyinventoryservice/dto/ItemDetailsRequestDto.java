package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;

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

    @NotNull
    private URI imgLink;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemDetailsRequestDtoBuilderImpl extends ItemDetailsRequestDto.ItemDetailsRequestDtoBuilder<ItemDetailsRequestDto, ItemDetailsRequestDto.ItemDetailsRequestDtoBuilderImpl> {
    }
}
