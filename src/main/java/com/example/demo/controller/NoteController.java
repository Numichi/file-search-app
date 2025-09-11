package com.example.demo.controller;

import com.example.api.NoteV1Api;
import com.example.demo.mapper.NoteMapper;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.NoteService;
import com.example.model.CreateNoteRequest;
import com.example.model.GetNotes200Response;
import com.example.model.UpdateNoteCheckedRequest;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
public class NoteController implements NoteV1Api {

    private final NoteService noteService;
    private final NoteMapper noteMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Observed(name = "notes.operations", contextualName = "create-note")
    public ResponseEntity<Void> createNote(CreateNoteRequest createNoteRequest) {
        var userId = authenticationService.getId();
        log.info("Attempting to create note for user: {}", userId);

        noteService.add(noteMapper.toDto(createNoteRequest), userId);
        log.info("Successfully created note for user: {}", userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Observed(name = "notes.operations", contextualName = "delete-note")
    public ResponseEntity<Void> deleteNoteById(UUID id) {
        var userId = authenticationService.getId();
        log.info("Attempting to delete note with id: {} for user: {}", id, userId);

        noteService.delete(id, userId);
        log.info("Successfully deleted note with id: {} for user: {}", id, userId);

        return ResponseEntity.ok().build();
    }

    @Override
    @Observed(name = "notes.operations", contextualName = "get-all-notes")
    public ResponseEntity<GetNotes200Response> getNotes() {
        var userId = authenticationService.getId();
        log.info("Attempting to get all notes for user: {}", userId);

        var notes = noteService.getAll(userId);

        var response = new GetNotes200Response().notes(
            noteMapper.toResponseNotesInner(notes)
        );

        log.info("Successfully retrieved {} notes for user: {}", notes.size(), userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @Observed(name = "notes.operations", contextualName = "update-note-checked")
    public ResponseEntity<Void> updateNoteChecked(UpdateNoteCheckedRequest request) {
        var userId = authenticationService.getId();
        log.info("Attempting to update note with id: {} for user: {}", request.getId(), userId);

        noteService.update(request.getId(), request.isChecked(), userId);
        log.info("Successfully updated note with id: {} for user: {}", request.getId(), userId);

        return ResponseEntity.ok().build();
    }
}
