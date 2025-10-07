package com.github.numichi.mapper;

import com.github.numichi.database.History;
import com.github.numichi.generated.openapi.model.HistoryItem;
import com.github.numichi.model.SearchLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mapping(target = "id", expression = "java(com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch())")
    @Mapping(target = "result", source = "result", qualifiedByName = "ObjToJson")
    @Mapping(target = "createdAt", ignore = true)
    History toEntity(SearchLog log, @Context ObjectMapper objectMapper);

    @Mapping(target = "results", source = "result", qualifiedByName = "JsonToObj")
    @Mapping(target = "timestamp", source = "createdAt")
    HistoryItem toDto(History history, @Context ObjectMapper objectMapper);

    default OffsetDateTime map(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
    }

    @Named("ObjToJson")
    default String toBase64List(List<String> list, @Context ObjectMapper objectMapper) {
        if (list == null) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Named("JsonToObj")
    default List<String> fromBase64List(String json, @Context ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
