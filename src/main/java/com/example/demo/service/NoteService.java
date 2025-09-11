package com.example.demo.service;

import com.example.demo.database.NoteRepository;
import com.example.demo.dto.NoteDto;
import com.example.demo.mapper.NoteMapper;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Observed
@Validated
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final UserService userService;
    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;

    public void add(@Valid NoteDto dto, UUID userId) {
        var user = userService.getUser(userId);

        var newNote = noteMapper.toNewEntity(new NoteMapper.NewNoteWrapper(dto, user));
        user.getNotes().add(newNote);
    }

    public int count(UUID userId) {
        var user = userService.getUser(userId);

        return user.getNotes().size();
    }

    public List<NoteDto> getAll(UUID userId) {
        var user = userService.getUser(userId);

        return user.getNotes().stream()
            .map(noteMapper::toDto)
            .toList();
    }

    public void delete(UUID id, UUID currentUserId) {
        noteRepository.deleteByIdAndUserId(id, currentUserId);
    }

    public void update(UUID id, boolean checked, UUID userId) {
        var note = noteRepository.findByIdAndUserId(id, userId).orElseThrow();

        note.setChecked(checked);
    }
}
