package com.example.demo.mapper;

import com.example.demo.database.Note;
import com.example.demo.database.User;
import com.example.demo.dto.NoteDto;
import com.example.model.CreateNoteRequest;
import com.example.model.GetNotes200ResponseNotesInner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface NoteMapper extends CommonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checked", constant = "false")
    @Mapping(target = "modified", ignore = true)
    NoteDto toDto(CreateNoteRequest request);

    @Mapping(target = "id", source = "dto.id", qualifiedByName = "makeUUIDv7IfNecessary")
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "checked", source = "dto.checked")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Note toNewEntity(NewNoteWrapper wrapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Note entity, NoteDto dto);

    @Mapping(target = "modified", source = "updatedAt")
    NoteDto toDto(Note entity);

    List<GetNotes200ResponseNotesInner> toResponseNotesInner(List<NoteDto> dto);

    record NewNoteWrapper(NoteDto dto, User user) {
    }
}
