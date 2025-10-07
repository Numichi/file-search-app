package com.github.numichi.controllers;

import com.github.numichi.base.TestContainerConfig;
import com.github.numichi.generated.openapi.api.HistoryV1Api;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.contains;

@Sql(
    scripts = {
        "/seed/cleanup.sql",
        "/seed/seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HistoryControllerTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void shouldReceiveHistroy() {
        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
        .when()
            .get(HistoryV1Api.PATH_GET_HISTORY)
        .then()
            .statusCode(200)
            .body("histories[0].user", equalTo("testuser1"))
            .body("histories[0].timestamp", equalTo("2025-09-08T19:10:03.480678Z"))
            .body("histories[0].ext", equalTo("txt"))
            .body("histories[0].results", contains("file1.txt", "file2.txt", "file3.txt"))
            .body("histories[1].user", equalTo("testuser2"))
            .body("histories[1].timestamp", equalTo("2025-09-08T19:10:04.480678Z"))
            .body("histories[1].ext", equalTo("txt"))
            .body("histories[1].results", contains("file4.txt", "file5.txt", "file6.txt"));
        //@formatter:on
    }
}
