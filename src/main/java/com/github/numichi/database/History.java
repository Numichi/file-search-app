package com.github.numichi.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a search history record.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "history")
public class History {

    @Id
    private UUID id;

    @Column(name = "linux_user", nullable = false)
    private String linuxUser;

    @Column(nullable = false)
    private String ext;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String result;

    // @CreatedDate //TODO: check why doesn't work
    @Column(name = "created_at")
    private Instant createdAt;
}
