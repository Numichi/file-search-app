package com.example.demo.database;

import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @NonNull
    @NewSpan
    Optional<User> findById(@NonNull UUID uuid);

    @NewSpan
    boolean existsByUsername(String username);

    @NewSpan
    boolean existsByEmail(String email);

    @NewSpan
    Optional<User> findByUsername(String username);

    @NewSpan
    Optional<User> findByEmail(String email);
}

