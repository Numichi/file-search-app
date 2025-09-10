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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class NoteController implements NoteV1Api {

    private final NoteService noteService;
    private final NoteMapper noteMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Observed
    public ResponseEntity<Void> createNote(CreateNoteRequest createNoteRequest) {
        noteService.add(noteMapper.toDto(createNoteRequest), authenticationService.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @Observed
    public ResponseEntity<Void> deleteNoteById(UUID id) {
        noteService.delete(id, authenticationService.getId());

        return ResponseEntity.ok().build();
    }

    @Override
    @Observed
    public ResponseEntity<GetNotes200Response> getNotes() {
        var notes = noteService.getAll(authenticationService.getId());

        var response = new GetNotes200Response().notes(
            noteMapper.toResponseNotesInner(notes)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> updateNoteChecked(UpdateNoteCheckedRequest request) {
        noteService.update(request.getId(), request.isChecked(), authenticationService.getId());

        return ResponseEntity.ok().build();
    }
}
