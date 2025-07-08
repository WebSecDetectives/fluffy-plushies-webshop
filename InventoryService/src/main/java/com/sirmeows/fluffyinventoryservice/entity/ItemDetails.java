package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class ItemDetails extends AbstractIdentifiable {
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
}
