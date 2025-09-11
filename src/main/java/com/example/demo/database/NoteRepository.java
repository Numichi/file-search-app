package com.example.demo.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    void deleteByIdAndUserId(UUID id, UUID currentUserId);

    Optional<Note> findByIdAndUserId(UUID id, UUID userId);
}
