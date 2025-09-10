package com.example.demo.mapper;

import com.github.f4b6a3.uuid.UuidCreator;
import org.mapstruct.Named;

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
}
