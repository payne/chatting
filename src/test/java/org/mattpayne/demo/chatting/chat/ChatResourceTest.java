package org.mattpayne.demo.chatting.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mattpayne.demo.chatting.config.BaseIT;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;


public class ChatResourceTest extends BaseIT {

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql", "/data/chatData.sql"})
    void getAllChats_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chats")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1100));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql", "/data/chatData.sql"})
    void getAllChats_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chats?filter=1101")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1101));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql", "/data/chatData.sql"})
    void getChat_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chats/1100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("message", Matchers.equalTo("Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat."));
    }

    @Test
    void getChat_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chats/1766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql"})
    void createChat_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatDTORequest.json"))
                .when()
                    .post("/api/chats")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, chatRepository.count());
    }

    @Test
    void createChat_missingField() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatDTORequest_missingField.json"))
                .when()
                    .post("/api/chats")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("message"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql", "/data/chatData.sql"})
    void updateChat_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatDTORequest.json"))
                .when()
                    .put("/api/chats/1100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.", chatRepository.findById(((long)1100)).orElseThrow().getMessage());
        assertEquals(2, chatRepository.count());
    }

    @Test
    @Sql({"/data/personData.sql", "/data/sessionData.sql", "/data/chatData.sql"})
    void deleteChat_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/chats/1100")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, chatRepository.count());
    }

}
