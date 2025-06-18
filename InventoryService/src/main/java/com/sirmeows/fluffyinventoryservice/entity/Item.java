package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.net.URI;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class Item extends AbstractIdentifiable {
    @NotBlank
    private String name;

    @NotBlank
    private URI imgLink;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    private int stock;

    @NotNull
    @ManyToOne
    private ItemDetails details;
}
