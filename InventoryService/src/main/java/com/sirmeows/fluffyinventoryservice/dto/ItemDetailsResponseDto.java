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
@JsonDeserialize(builder = ItemDetailsResponseDto.ItemDetailsResponseDtoBuilderImpl.class)
public class ItemDetailsResponseDto {
    private String description;
    private String ageGroup;
    private String itemType;
    private String material;
    private URI imgLink;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemDetailsResponseDtoBuilderImpl extends ItemDetailsResponseDto.ItemDetailsResponseDtoBuilder<ItemDetailsResponseDto, ItemDetailsResponseDto.ItemDetailsResponseDtoBuilderImpl> {
    }
}
