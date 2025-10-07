package com.github.numichi.mapper;

import com.github.numichi.database.History;
import com.github.numichi.generated.openapi.model.HistoryItem;
import com.github.numichi.model.SearchLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    /**
     * Maps a SearchLog and ObjectMapper to a History entity.
     *
     * @param log          The SearchLog containing search details.
     * @param objectMapper The ObjectMapper for JSON processing.
     * @return A History entity populated with data from the SearchLog.
     */
    @Mapping(target = "id", expression = "java(com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch())")
    @Mapping(target = "result", source = "result", qualifiedByName = "ObjToJson")
    @Mapping(target = "linuxUser", source = "user")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    History toEntity(SearchLog log, @Context ObjectMapper objectMapper);

    /**
     * Maps a History entity and ObjectMapper to a HistoryItem DTO.
     *
     * @param history      The History entity containing search history details.
     * @param objectMapper The ObjectMapper for JSON processing.
     * @return A HistoryItem DTO populated with data from the History entity.
     */
    @Mapping(target = "results", source = "result", qualifiedByName = "JsonToObj")
    @Mapping(target = "user", source = "linuxUser")
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
