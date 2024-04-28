package org.mattpayne.demo.chatting.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mattpayne.demo.chatting.config.BaseIT;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;


public class PersonResourceTest extends BaseIT {

    @Test
    @Sql("/data/personData.sql")
    void getAllPeople_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/people")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1000));
    }

    @Test
    @Sql("/data/personData.sql")
    void getAllPeople_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/people?filter=1001")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1001));
    }

    @Test
    @Sql("/data/personData.sql")
    void getPerson_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/people/1000")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("email", Matchers.equalTo("Sed ut perspiciatis."));
    }

    @Test
    void getPerson_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/people/1666")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createPerson_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/personDTORequest.json"))
                .when()
                    .post("/api/people")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, personRepository.count());
    }

    @Test
    void createPerson_missingField() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/personDTORequest_missingField.json"))
                .when()
                    .post("/api/people")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("email"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/personData.sql")
    void updatePerson_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/personDTORequest.json"))
                .when()
                    .put("/api/people/1000")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Duis autem vel.", personRepository.findById(((long)1000)).orElseThrow().getEmail());
        assertEquals(2, personRepository.count());
    }

    @Test
    @Sql("/data/personData.sql")
    void deletePerson_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/people/1000")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, personRepository.count());
    }

}
