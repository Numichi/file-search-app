package com.github.numichi.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing History entities.
 */
public interface HistoryRepository extends JpaRepository<History, UUID> {
}
