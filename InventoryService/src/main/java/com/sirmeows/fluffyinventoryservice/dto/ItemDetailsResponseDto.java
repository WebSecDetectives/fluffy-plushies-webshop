package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemDetailsResponseDto.ItemDetailsResponseDtoBuilderImpl.class)
public class ItemDetailsResponseDto {
    private String description;
    private String ageGroup;
    private String itemType;
    private String material;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemDetailsResponseDtoBuilderImpl extends ItemDetailsResponseDto.ItemDetailsResponseDtoBuilder<ItemDetailsResponseDto, ItemDetailsResponseDto.ItemDetailsResponseDtoBuilderImpl> {
    }
}
