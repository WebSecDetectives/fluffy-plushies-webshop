package com.sirmeows.fluffyinventoryservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private URI imgLink;

    @NotNull
    private BigDecimal price;

    @Min(0)
    private int stock;

    @NotNull
    private ItemDetailsRequestDto details;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ItemRequestDtoBuilderImpl extends ItemRequestDto.ItemRequestDtoBuilder<ItemRequestDto,  ItemRequestDtoBuilderImpl> {
    }
}
