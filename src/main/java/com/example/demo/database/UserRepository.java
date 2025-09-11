package com.example.demo.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @NonNull
    Optional<User> findById(@NonNull UUID uuid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
