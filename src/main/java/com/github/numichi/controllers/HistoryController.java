package com.github.numichi.controllers;

import com.github.numichi.generated.openapi.api.HistoryV1Api;
import com.github.numichi.generated.openapi.model.GetHistory200Response;
import com.github.numichi.services.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HistoryController implements HistoryV1Api {

    private final HistoryService historyService;

    @Override
    public ResponseEntity<GetHistory200Response> getHistory() {
        var history = historyService.getAll();
        return ResponseEntity.ok(GetHistory200Response.builder().histories(history).build());
    }
}
