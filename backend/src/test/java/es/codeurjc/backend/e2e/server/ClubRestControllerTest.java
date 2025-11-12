package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;


@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@ActiveProfiles("test")
public class ClubRestControllerTest {

    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetClubs() {
        given()
        .when()
            .get("/api/v1/clubs/")
        .then().statusCode(200)
            .body("content", not(empty()))
            .body("totalElements", greaterThan(0))
            .body("content[0].id", notNullValue());  
    }

    @Test
    public void testGetFilteredCLubs(){
        given()
        .when()
            .get("/api/v1/clubs?search=pro&city=Valencia&sport=padel")
        .then()
            .statusCode(200)
            .body("content.size()",greaterThan(0))
            .body("content.name", everyItem(matchesRegex("(?i).*pro.*")))
            .body("content.city",everyItem(equalTo("Valencia")))
            .body("content.sports.name", everyItem(hasItem("Padel")))
            .body("content.size()", equalTo(1));
    }

    @Test
    public void testGetClubById() {
        long id = 3L;
        given()
        .when()
            .get("/api/v1/clubs/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("name", equalTo("Tennis & Padel Hub"));
    }

    @Test
    public void testGetClubImage(){
        long id = 2L;
        given()
        .when()
            .get("/api/v1/clubs/{id}/image", id)
        .then()
            .statusCode(200)
            .header("Content-Type", equalTo("image/jpeg"));
    }

    
    
}
