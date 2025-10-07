package com.github.numichi.controllers;

import com.github.numichi.base.BeanTestConfiguration;
import com.github.numichi.base.TestContainerConfig;
import com.github.numichi.database.HistoryRepository;
import com.github.numichi.exceptions.DirectoryAccessException;
import com.github.numichi.services.PermissionScanner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(BeanTestConfiguration.class)
public class SearchControllerTest  extends TestContainerConfig {

    @TempDir
    Path tempDir;

    @LocalServerPort
    private int port;

    @MockitoSpyBean
    private PermissionScanner permissionScanner;

    @Autowired
    private HistoryRepository historyRepository;

    private Path nestedDir;

    @BeforeEach
    void setUp() throws IOException {
        historyRepository.deleteAll();
        RestAssured.port = port;

        var subDir1 = Files.createDirectory(tempDir.resolve("subdir1"));
        nestedDir = Files.createDirectory(subDir1.resolve("nested"));
        var subDir2 = Files.createDirectory(tempDir.resolve("subdir2"));

        Files.createFile(tempDir.resolve("file3.txt"));
        Files.createFile(tempDir.resolve("file2.log"));
        Files.createFile(subDir1.resolve("file1.txt"));
        Files.createFile(subDir2.resolve("file2.txt"));
        Files.createFile(nestedDir.resolve("file1.txt"));
        Files.createFile(nestedDir.resolve("file4.txt"));
        Files.createFile(nestedDir.resolve("file5.csv"));
    }

    @Test
    void shouldReceiveAndSaveSearchHistory() {
        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("folder", tempDir.toAbsolutePath().toString())
            .queryParam("ext", ".txt")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(200)
            .body("results", contains("file1.txt", "file2.txt", "file3.txt", "file4.txt"))
            .body("errors", empty());
        //@formatter:on

        assertEquals(1, historyRepository.count());
    }

    @Test
    void shouldCollectErrorsWhenDirectoryAccessDenied() {
        doAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            if (path.equals(nestedDir)) {
                throw new DirectoryAccessException("Access denied: " + path.toAbsolutePath());
            }
            return null;
        }).when(permissionScanner).checkReadPermission(any(Path.class));

        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("folder", tempDir.toAbsolutePath().toString())
            .queryParam("ext", "txt")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(200)
            .body("results", contains("file1.txt", "file2.txt", "file3.txt"))
            .body("errors", contains("Access denied: " + nestedDir.toAbsolutePath()));
        //@formatter:on
    }

    @Test
    void shouldReturnForbiddenWhenWarnFalseAndDirectoryAccessDenied() {
        doAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            if (path.equals(nestedDir)) {
                throw new DirectoryAccessException("Acceds denied: " + path.toAbsolutePath());
            }
            return null;
        }).when(permissionScanner).checkReadPermission(any(Path.class));

        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("folder", tempDir.toAbsolutePath().toString())
            .queryParam("ext", "txt")
            .queryParam("warn", "false")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(403)
            .body("status", equalTo(403))
            .body("errors", contains("Acceds denied: " + nestedDir.toAbsolutePath()));
        //@formatter:on
    }

    @Test
    void shouldReturnBadRequestWhenFolderIsFile() {
        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("folder", tempDir.toAbsolutePath() + "/file3.txt")
            .queryParam("ext", "txt")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(400).log().body()
            .body("status", equalTo(400))
            .body("errors", contains("Path is not a directory: " + tempDir.toAbsolutePath() + File.separator + "file3.txt"));
        //@formatter:on
    }

    @Test
    void shouldReturnBadRequestWhenFolderParamMissing() {
        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("ext", "txt")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors", contains("Required request parameter 'folder' for method parameter type String is not present"));
        //@formatter:on
    }

    @Test
    void shouldReturnBadRequestWhenExtParamIsBlank() {
        //@formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .queryParam("folder", tempDir.toAbsolutePath().toString())
            .queryParam("ext", "")
        .when()
            .get(SearchController.PATH_GET_UNIQUE)
        .then()
            .statusCode(400)
            .log().body()
            .body("status", equalTo(400))
            .body("errors", contains("ext: must not be blank"));
        //@formatter:on
    }
}
