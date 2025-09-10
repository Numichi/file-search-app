package com.example.demo.mapper;

import com.example.demo.database.User;
import com.example.demo.dto.UserDto;
import com.example.model.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper extends CommonMapper {

    @Mapping(target = "id", expression = "java(makeUUIDv7())")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    @Mapping(target = "roles", constant = "USER")
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toNewEntity(UserDto dto, PasswordEncoder passwordEncoder);

    UserDto toDTO(RegisterRequest user);
}
