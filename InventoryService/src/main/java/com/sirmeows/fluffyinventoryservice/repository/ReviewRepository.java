package com.sirmeows.fluffyinventoryservice.repository;

import com.sirmeows.fluffyinventoryservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByItem_Id(UUID itemId);
}
