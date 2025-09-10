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
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql(
    scripts = {
        "/seed/cleanup.sql",
        "/seed/seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
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
        RestAssured.baseURI = "http://localhost";
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
        var asd = RestAssured.given()
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
    void registrationProcessWrongData() {
        var body = RegisterRequest.builder()
            .username(Generator.username())
            .email("invalid-email")
            .password("weakpass")
            .build();

        //@formatter:off
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(AuthenticationV1Api.PATH_REGISTER)
        .then()
            .statusCode(400)
            .body("errors", containsInAnyOrder(
                "email: must be a well-formed email address",
                "password: password must be at least 8 characters long and include uppercase, lowercase, digit, and special character"
            ))
            .body("errors.size()", equalTo(2));
        //@formatter:on
    }

    @Test
    void loginProcessHappyPath() {
        var token = Common.getTestUserToken(UserEnum.USER_1);
        assertEquals(UserEnum.USER_1.getUsername(), jwtService.extractClaim(token, Claims::getSubject));
        assertEquals(List.of("ROLE_USER"), jwtService.extractClaim(token, (Claims c) -> c.get("authorities")));

        token = Common.getTestUserToken(UserEnum.ADMIN);
        assertEquals(UserEnum.ADMIN.getUsername(), jwtService.extractClaim(token, Claims::getSubject));
        assertEquals(List.of("ROLE_ADMIN"), jwtService.extractClaim(token, (Claims c) -> c.get("authorities")));
    }
}
