package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemResponseDto.ItemResponseDtoBuilderImpl.class)
public class ItemResponseDto {
    private UUID id;
    private String name;
    private URI imgLink;
    private BigDecimal price;
    private int stock;
    private ItemDetailsResponseDto details;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemResponseDtoBuilderImpl extends ItemResponseDto.ItemResponseDtoBuilder<ItemResponseDto,  ItemResponseDtoBuilderImpl> {
    }
}
