package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import com.sirmeows.fluffyinventoryservice.entity.Visibility;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(builder = ItemRequestDto.ItemRequestDtoBuilderImpl.class)
public class ItemRequestDto {

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @Min(0)
    private int stock;

    // Default defined in Item
    private Visibility visibility;

    @NotNull
    @Valid
    private ItemDetailsRequestDto details;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemRequestDtoBuilderImpl extends ItemRequestDto.ItemRequestDtoBuilder<ItemRequestDto,  ItemRequestDtoBuilderImpl> {
    }
}
