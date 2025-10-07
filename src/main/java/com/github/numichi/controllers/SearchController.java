package com.github.numichi.controllers;

import com.github.numichi.exceptions.DirectoryAccessException;
import com.github.numichi.generated.openapi.api.SearchV1Api;
import com.github.numichi.generated.openapi.model.ErrorMessageResponse;
import com.github.numichi.generated.openapi.model.GetUnique200Response;
import com.github.numichi.processes.DeepScanContext;
import com.github.numichi.services.HistoryService;
import com.github.numichi.services.DeepScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.InvalidPathException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class SearchController implements SearchV1Api {

    private final HistoryService historyService;
    private final DeepScanService deepScanService;

    @Override
    public ResponseEntity<GetUnique200Response> getUnique(String folder, String ext, Boolean warn) {
        var context = new DeepScanContext(folder, ext, warn);

        var result = deepScanService.search(context);
        var fileNames = result.orderedFileNames();
        historyService.save(folder, ext, fileNames);

        var body = GetUnique200Response.builder().results(fileNames).errors(result.permissionProblems()).build();
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidPathException(InvalidPathException e) {
        var body = ErrorMessageResponse.builder()
            .errors(List.of("The provided folder path is invalid: " + e.getInput()))
            .status(HttpStatus.BAD_REQUEST.value())
            .build();

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        var body = ErrorMessageResponse.builder()
            .errors(List.of(e.getMessage()))
            .status(HttpStatus.BAD_REQUEST.value())
            .build();

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler(DirectoryAccessException.class)
    public ResponseEntity<ErrorMessageResponse> handleDirectoryAccessException(DirectoryAccessException e) {
        var body = ErrorMessageResponse.builder()
            .errors(List.of(e.getMessage()))
            .status(HttpStatus.FORBIDDEN.value())
            .build();

        return ResponseEntity.status(body.getStatus()).body(body);
    }
}
