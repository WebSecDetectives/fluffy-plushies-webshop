package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Check;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Check(constraints = "rating >= 1 and rating <= 5")
public class Review extends AbstractIdentifiable {

    @Column(nullable = false, length = 2000)
    private String reviewText;

    @Min(value = 1, message = "Rating must be ≥ 1")
    @Max(value = 5, message = "Rating must be ≤ 5")
    @Column(nullable = false)
    private int rating;
}
