package com.example.demo.controller;

import com.example.api.NoteV1Api;
import com.example.demo.base.Common;
import com.example.demo.base.UserEnum;
import com.example.demo.base.container.TestContainerConfig;
import com.example.demo.service.JwtService;
import com.example.demo.service.NoteService;
import com.example.model.CreateNoteRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Sql(
    scripts = {
        "/seed/cleanup.sql",
        "/seed/seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoteControllerTest extends TestContainerConfig {

    @LocalServerPort
    private int port;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private NoteService noteService;

    private final static CreateNoteRequest createNoteRequestBody = CreateNoteRequest.builder().title("This is a test note.").build();

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Should create note for user A and not visible to user B")
    void shouldCreateNoteForUserAAndNotVisibleToUserB() {
        var userAToken = Common.getTestUserToken(UserEnum.USER_1);

        //@formatter:off
        RestAssured.given()
            .header(Common.getTestUserHeaderToken(Common.getTestUserToken(UserEnum.USER_1)))
            .contentType(ContentType.JSON)
            .body(createNoteRequestBody)
        .when()
            .post(NoteV1Api.PATH_CREATE_NOTE)
        .then()
            .statusCode(201);
        //@formatter:on

        var userAId = UUID.fromString(jwtService.extractUserId(userAToken));
        assertEquals(1, noteService.count(userAId));

        // backdoor check to ensure user B cannot see user A's notes
        var userBToken = Common.getTestUserToken(UserEnum.USER_2);
        var userBId = UUID.fromString(jwtService.extractUserId(userBToken));
        assertEquals(0, noteService.count(userBId));
    }

    @Test
    @DisplayName("Should not create note when unauthenticated")
    void shouldNotCreateNoteWhenUnauthenticated() {
        //@formatter:off
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(createNoteRequestBody)
        .when()
            .post(NoteV1Api.PATH_CREATE_NOTE)
        .then()
            .statusCode(401);
        //@formatter:on
    }
}

