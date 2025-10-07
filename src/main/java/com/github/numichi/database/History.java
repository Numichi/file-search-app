package com.github.numichi.database;

import com.github.numichi.config.generator.Uuid7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "history")
public class History {

    @Id
    @Uuid7
    private UUID id;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String ext;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String result;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
}
