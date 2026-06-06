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

    // Length forces MEDIUMBLOB (16MB); Hibernate otherwise creates a 64KB BLOB, far too
    // small for a re-encoded PNG.
    @Lob
    @Column(nullable = false, length = 16_777_215)
    @ToString.Exclude
    private byte[] data;
}