package com.sirmeows.fluffyinventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Product image stored as bytes. Kept in its own table (not on Item/ItemDetails) so item
 * queries never load image blobs.
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class ItemImage extends AbstractIdentifiable {

    @Column(nullable = false, unique = true)
    private UUID itemId;

    @Column(nullable = false)
    private String contentType;

    @Lob
    @Column(nullable = false)
    @ToString.Exclude
    private byte[] data;
}