package com.dlshomies.fluffyplushies.repository;

import com.dlshomies.fluffyplushies.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {
}
