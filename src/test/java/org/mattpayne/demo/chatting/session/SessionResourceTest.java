package org.mattpayne.demo.chatting.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mattpayne.demo.chatting.config.BaseIT;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;


public class SessionResourceTest extends BaseIT {

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void getAllSessions_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/sessions")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1200));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void getAllSessions_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/sessions?filter=1201")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1201));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void getSession_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/sessions/1200")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

    @Test
    void getSession_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/sessions/1866")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql("/data/personData.sql")
    void createSession_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/sessionDTORequest.json"))
                .when()
                    .post("/api/sessions")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, sessionRepository.count());
    }

    @Test
    void createSession_missingField() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/sessionDTORequest_missingField.json"))
                .when()
                    .post("/api/sessions")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("startedAt"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void updateSession_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/sessionDTORequest.json"))
                .when()
                    .put("/api/sessions/1200")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(2, sessionRepository.count());
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void deleteSession_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/sessions/1200")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, sessionRepository.count());
    }

}
