package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
}
