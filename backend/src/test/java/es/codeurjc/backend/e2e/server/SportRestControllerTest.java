package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")

public class SportRestControllerTest {

    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
    
    @Test
    @Order(1)
    public void testGetSports() {
        given()
        .when()
            .get("/api/v1/sports/")
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body("size()", greaterThan(0))
            .body("[0].id", notNullValue());
    }

    @Test
    @Order(2)
    public void testGetSportById() {
        long id = 1L;
        given()
        .when()
            .get("/api/v1/sports/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue());
    }

    @Test
    @Order(3)
    public void testCreateSport() {

        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        assertThat(cookie, notNullValue());

        String newSportJson = """
            {
                "name": "Speedball",
                "modes": [],
                "scoringType": "SCORE"
            }
        """;

        given()
            .cookie("AuthToken",cookie)
            .contentType(ContentType.JSON)
            .body(newSportJson)
        .when()
            .post("/api/v1/sports/")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Speedball"));
    }

    @Test
    @Order(4)
    public void testReplaceSport() {

        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        assertThat(cookie, notNullValue());

        long idToEdit = 1L;

        String updatedSportJson = """
            {
                "id": 1,
                "name": "Tenis Renovado",
                "modes": [],
                "scoringType": "SETS"
            }
        """;

        given()
            .cookie("AuthToken",cookie)
            .contentType(ContentType.JSON)
            .body(updatedSportJson)
        .when()
            .put("/api/v1/sports/{id}", idToEdit)
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", equalTo("Tenis Renovado"));
    }

    @Test
    @Order(5)
    public void testDeleteSport() {

        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        assertThat(cookie, notNullValue());

        String newSportJson = """
            {
                "name": "TemporalSport",
                "modes": [],
                "scoringType": "SCORE"
            }
        """;

        Response resp = given()
            .cookie("AuthToken",cookie)
            .contentType(ContentType.JSON)
            .body(newSportJson)
        .when()
            .post("/api/v1/sports/")
        .then()
            .statusCode(201)
            .extract().response();

        long createdId = resp.jsonPath().getLong("id");

        given()
            .cookie("AuthToken",cookie)
        .when()
            .delete("/api/v1/sports/{createdId}", createdId)
        .then()
            .statusCode(200)
            .body("name", equalTo("TemporalSport"));
    }

    @Test
    @Order(6)
    public void testDeleteNonExistingSport() {
        long nonExistingId = 9999L;

        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        assertThat(cookie, notNullValue());

        given()
            .cookie("AuthToken",cookie)
        .when()
            .delete("/api/v1/sports/{id}", nonExistingId)
        .then()
            .statusCode(404) 
            .body("message", equalTo("Deporte no encontrado"));
    }

    private String loginAndGetCookie(String email, String password) {
        String loginJson = 
            String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, email, password);

        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginJson)
        .when()
            .post("/api/v1/auth/login")
        .then()
            .extract()
            .response();

        String cookie = loginResponse.getCookie("AuthToken");
        return cookie;
    }
}
