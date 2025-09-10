package com.example.demo.service;

import com.example.demo.database.NoteRepository;
import com.example.demo.dto.NoteDto;
import com.example.demo.mapper.NoteMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated

@RequiredArgsConstructor
public class NoteService {

    private final UserService userService;
    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;

    @Transactional
    public void add(@Valid NoteDto dto, UUID userId) {
        var user = userService.getUser(userId);

        var newNote = noteMapper.toNewEntity(new NoteMapper.NewNoteWrapper(dto, user));
        user.getNotes().add(newNote);
    }

    @Transactional
    public int count(UUID userId) {
        var user = userService.getUser(userId);

        return user.getNotes().size();
    }

    @Transactional
    public List<NoteDto> getAll(UUID userId) {
        var user = userService.getUser(userId);

        return user.getNotes().stream()
            .map(noteMapper::toDto)
            .toList();
    }

    public void delete(UUID id, UUID currentUserId) {
        noteRepository.deleteByIdAndUserId(id, currentUserId);
    }

    @Transactional
    public void update(UUID id, boolean checked, UUID userId) {
        var note = noteRepository.findByIdAndUserId(id, userId)
            .filter(n -> n.getUser().getId().equals(userId))
            .orElseThrow();

        note.setChecked(checked);
    }
}
