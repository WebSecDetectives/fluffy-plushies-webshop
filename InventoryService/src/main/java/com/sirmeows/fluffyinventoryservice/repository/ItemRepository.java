package com.sirmeows.fluffyinventoryservice.repository;

import com.sirmeows.fluffyinventoryservice.entity.Item;
import com.sirmeows.fluffyinventoryservice.entity.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    // Public catalogue — visible to everyone, including anonymous callers.
    List<Item> findByVisibility(Visibility visibility);

    // A merchant's view: all PUBLIC items plus their own (PUBLIC and PRIVATE).
    List<Item> findByVisibilityOrMerchantId(Visibility visibility, UUID merchantId);

    // A merchant's own items (for GET /items/mine).
    List<Item> findByMerchantId(UUID merchantId);
}
