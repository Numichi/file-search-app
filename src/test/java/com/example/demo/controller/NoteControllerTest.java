package com.example.demo.controller;

import com.example.api.NoteV1Api;
import com.example.demo.base.Common;
import com.example.demo.base.UserEnum;
import com.example.demo.base.container.TestContainerConfig;
import com.example.demo.service.JwtService;
import com.example.demo.service.NoteService;
import com.example.model.CreateNoteRequest;
import com.example.model.UpdateNoteCheckedRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static com.example.demo.base.Common.getAuthenticationBearerHeader;
import static com.example.demo.base.Common.getAuthenticationJwtToken;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        RestAssured.port = port;
    }

    @Test
    void shouldCreateNoteForUserAAndNotVisibleToUserB() {
        var jwt = getAuthenticationJwtToken(UserEnum.USER_1);
        var header = getAuthenticationBearerHeader(jwt);

        //@formatter:off
        RestAssured.given()
            .header(header)
            .contentType(ContentType.JSON)
            .body(createNoteRequestBody)
        .when()
            .post(NoteV1Api.PATH_CREATE_NOTE)
        .then()
            .statusCode(201);
        //@formatter:on

        var userAId = UUID.fromString(jwtService.extractUserId(jwt));
        assertEquals(3, noteService.count(userAId)); // user A had 2 notes from seed data + 1 created now

        // backdoor check to ensure user B cannot see user A's notes
        var userBToken = Common.getAuthenticationJwtToken(UserEnum.USER_2);
        var userBId = UUID.fromString(jwtService.extractUserId(userBToken));
        assertEquals(0, noteService.count(userBId));
    }

    @Test
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

    @Test
    void shouldReturnNotesForAuthenticatedUser() {
        //@formatter:off
        RestAssured.given()
            .header(getAuthenticationBearerHeader(UserEnum.USER_1))
            .accept(ContentType.JSON)
        .when()
            .get(NoteV1Api.PATH_GET_NOTES)
        .then()
            .statusCode(200)
            .log().body(true)
            .body("notes.size()", equalTo(2))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff811' }.checked", equalTo(false))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff812' }.checked", equalTo(true));
        //@formatter:on
    }

    @Test
    void shouldUpdateNoteCheckedStatusForAuthenticatedUser() {
        var header = getAuthenticationBearerHeader(UserEnum.USER_1);
        var body = UpdateNoteCheckedRequest.builder()
            .checked(true)
            .id(UUID.fromString("01992abc-3c1c-7305-bf26-c5fdb14ff811"))
            .build();

        //@formatter:off
        // Initial state verification
        RestAssured.given()
            .header(header)
            .header(getAuthenticationBearerHeader(UserEnum.USER_1))
            .accept(ContentType.JSON)
        .when()
            .get(NoteV1Api.PATH_GET_NOTES)
        .then()
            .statusCode(200)
            .log().body(true)
            .body("notes.size()", equalTo(2))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff811' }.checked", equalTo(false))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff812' }.checked", equalTo(true));

        // Do change
        RestAssured.given()
            .header(header)
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .patch(NoteV1Api.PATH_UPDATE_NOTE_CHECKED)
        .then()
            .statusCode(200);

        // Change verification
        RestAssured.given()
            .header(header)
            .accept(ContentType.JSON)
        .when()
            .get(NoteV1Api.PATH_GET_NOTES)
        .then()
            .statusCode(200)
            .log().body(true)
            .body("notes.size()", equalTo(2))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff811' }.checked", equalTo(true))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff812' }.checked", equalTo(true));
        //@formatter:on
    }

    @Test
    void shouldRemoveNoteForAuthenticatedUser() {
        var header = getAuthenticationBearerHeader(UserEnum.USER_1);

        //@formatter:off
        // Initial state verification
        RestAssured.given()
            .header(header)
            .accept(ContentType.JSON)
        .when()
            .get(NoteV1Api.PATH_GET_NOTES)
        .then()
            .statusCode(200)
            .log().body(true)
            .body("notes.size()", equalTo(2))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff811' }.checked", equalTo(false))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff812' }.checked", equalTo(true));

        // Do change
        RestAssured.given()
            .header(header)
            .queryParam("id", "01992abc-3c1c-7305-bf26-c5fdb14ff811")
        .when()
            .delete(NoteV1Api.PATH_DELETE_NOTE_BY_ID)
        .then()
            .statusCode(200);

        // Change verification
        RestAssured.given()
            .header(header)
            .accept(ContentType.JSON)
        .when()
            .get(NoteV1Api.PATH_GET_NOTES)
        .then()
            .statusCode(200)
            .log().body(true)
            .body("notes.size()", equalTo(1))
            .body("notes.find { it.id == '01992abc-3c1c-7305-bf26-c5fdb14ff812' }.checked", equalTo(true));
        //@formatter:on
    }
}
