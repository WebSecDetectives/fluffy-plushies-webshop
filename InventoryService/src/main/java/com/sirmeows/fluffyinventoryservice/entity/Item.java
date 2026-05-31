package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class Item extends AbstractIdentifiable {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(0)
    private int stock;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default                       // needed because @SuperBuilder ignores plain field initializers
    private Visibility visibility = Visibility.PUBLIC;

    @OneToOne(cascade = CascadeType.ALL)
    private ItemDetails details;
}
