package com.example.demo.database;

import io.micrometer.tracing.annotation.NewSpan;
import org.apache.logging.log4j.core.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    @NonNull
    @NewSpan
    Optional<Note> findById(@NonNull UUID uuid);

    @NewSpan
    List<Note> findAllByUserId(@NonNull UUID userId);

    void deleteByIdAndUserId(UUID id, UUID currentUserId);

    Optional<Note> findByIdAndUserId(UUID id, UUID userId);
}

