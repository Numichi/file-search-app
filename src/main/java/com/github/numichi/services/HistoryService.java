package com.github.numichi.services;

import com.github.numichi.database.HistoryRepository;
import com.github.numichi.generated.openapi.model.HistoryItem;
import com.github.numichi.mapper.HistoryMapper;
import com.github.numichi.model.SearchLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service for managing search history logs.
 */
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ObjectMapper objectMapper;
    private final String currentLinuxUser;
    private final HistoryMapper historyMapper;
    private final HistoryRepository historyRepository;

    /**
     * Save a search log asynchronously.
     *
     * @param startPath The absolute path of the searched start directory.
     * @param fileExtension The file extension that was searched for.
     * @param result The list of found file names.
     */
    @Async
    @Transactional
    public void save(String startPath, String fileExtension, List<String> result) {
        var log = new SearchLog(currentLinuxUser, startPath, fileExtension, result, null);
        var history = historyMapper.toEntity(log, objectMapper);
        historyRepository.save(history);
    }

    /**
     * Retrieve all search history logs as a list.
     *
     * @return A list of HistoryItem DTOs representing the search history.
     */
    public List<HistoryItem> getAll() {
        return historyRepository.findAll()
            .stream()
            .map(item -> historyMapper.toDto(item, objectMapper))
            .toList();
    }
}
