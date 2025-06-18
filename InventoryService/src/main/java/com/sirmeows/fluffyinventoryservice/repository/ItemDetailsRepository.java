package com.sirmeows.fluffyinventoryservice.repository;

import com.sirmeows.fluffyinventoryservice.entity.ItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemDetailsRepository extends JpaRepository<ItemDetails, UUID> {
}
