package com.sirmeows.fluffyinventoryservice.repository;

import com.sirmeows.fluffyinventoryservice.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

    Optional<ItemImage> findByItemId(UUID itemId);

    void deleteByItemId(UUID itemId);
}