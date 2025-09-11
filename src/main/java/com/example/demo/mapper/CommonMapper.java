package com.example.demo.mapper;

import com.github.f4b6a3.uuid.UuidCreator;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public interface CommonMapper {

    default UUID makeUUIDv7() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    @Named("makeUUIDv7IfNecessary")
    default UUID makeUUIDv7IfNecessary(UUID uuid) {
        if (uuid == null) {
            return makeUUIDv7();
        }

        return uuid;
    }

    @SuppressWarnings("unused")
    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    @SuppressWarnings("unused")
    default Instant offsetDateTimeToInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
}
