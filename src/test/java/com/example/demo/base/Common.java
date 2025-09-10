package com.example.demo.base;

import com.example.api.AuthenticationV1Api;
import com.example.model.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.notNullValue;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Common {

    public static Header getTestUserHeaderToken(UserEnum userEnum) {
        return getTestUserHeaderToken(Common.getTestUserToken(userEnum));
    }

    public static Header getTestUserHeaderToken(String token) {
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    public static String getTestUserToken(UserEnum userEnum) {
        return getTokenForUser(userEnum);
    }

    private static String getTokenForUser(UserEnum userEnum) {
        var loginRequest = LoginRequest.builder()
            .username(userEnum.getUsername())
            .password(userEnum.getPassword())
            .build();

        return performLogin(loginRequest);
    }

    private static String performLogin(LoginRequest loginRequest) {
        //@formatter:off
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post(AuthenticationV1Api.PATH_LOGIN)
        .then()
            .statusCode(200)
            .body("token", notNullValue())
        .extract()
            .body()
            .jsonPath()
            .getString("token");
        //@formatter:on
    }
}
