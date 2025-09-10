package com.example.demo.controller;

import com.example.api.AuthenticationV1Api;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.model.Login200Response;
import com.example.model.LoginRequest;
import com.example.model.RegisterRequest;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthenticationV1Api {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @Observed
    public ResponseEntity<Login200Response> login(LoginRequest loginRequest) {
        var token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        var response = Login200Response.builder().token(token).build();
        return ResponseEntity.ok(response);
    }

    @Override
    @Observed
    public ResponseEntity<Void> register(RegisterRequest registerRequest) {
        userService.register(userMapper.toDTO(registerRequest));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

