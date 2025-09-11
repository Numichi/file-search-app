package com.example.demo.controller;

import com.example.api.AuthenticationV1Api;
import com.example.demo.base.Common;
import com.example.demo.base.Generator;
import com.example.demo.base.UserEnum;
import com.example.demo.base.container.TestContainerConfig;
import com.example.demo.database.UserRepository;
import com.example.demo.service.JwtService;
import com.example.model.RegisterRequest;
import io.jsonwebtoken.Claims;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest extends TestContainerConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void registrationProcessHappyPath() {
        var body = RegisterRequest.builder()
            .username(Generator.username())
            .email(Generator.email())
            .password(Generator.password())
            .build();

        //@formatter:off
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(AuthenticationV1Api.PATH_REGISTER)
        .then()
            .statusCode(201);
        //@formatter:on

        var user = userRepository.findByUsername(body.getUsername()).orElseThrow();
        assertNotNull(user);
        assertEquals(body.getEmail(), user.getEmail());
        assertEquals("USER", user.getRoles());
    }

    @Test
    void registrationProcessWithInvalidEmail_shouldReturnBadRequest() {
        var body = RegisterRequest.builder()
            .username(Generator.username())
            .email("invalid-email")
            .password(Generator.password())
            .build();

        //@formatter:off
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(AuthenticationV1Api.PATH_REGISTER)
        .then()
            .statusCode(400)
            .log().body()
            .body("errors", contains("email: must be a well-formed email address"))
            .body("errors.size()", equalTo(1));
        //@formatter:on
    }

    @Test
    void registrationProcessWithInvalidPassword_shouldReturnBadRequest() {
        var body = RegisterRequest.builder()
            .username(Generator.username())
            .email(Generator.email())
            .password("123456")
            .build();

        //@formatter:off
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(AuthenticationV1Api.PATH_REGISTER)
        .then()
            .statusCode(400)
            .log().body()
            .body("errors", contains("password: the password must be at least 8 characters long, contain uppercase and lowercase letters, numbers, special characters"))
            .body("errors.size()", equalTo(1));
        //@formatter:on
    }

    @Test
    void loginProcessHappyPath() {
        var token = Common.getAuthenticationJwtToken(UserEnum.USER_1);
        assertEquals(UserEnum.USER_1.getId().toString(), jwtService.extractClaim(token, Claims::getSubject));
        assertEquals(List.of("ROLE_USER"), jwtService.extractClaim(token, (Claims c) -> c.get("authorities")));

        token = Common.getAuthenticationJwtToken(UserEnum.ADMIN);
        assertEquals(UserEnum.ADMIN.getId().toString(), jwtService.extractClaim(token, Claims::getSubject));
        assertEquals(List.of("ROLE_ADMIN"), jwtService.extractClaim(token, (Claims c) -> c.get("authorities")));
    }
}
