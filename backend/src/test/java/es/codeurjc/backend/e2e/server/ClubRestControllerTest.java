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
        given().when().get("/api/v1/clubs/")
                .then().statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].name", equalTo("Tennis Club Elite"))
                .body("content[1].name", equalTo("Padel Pro Center"))
                .body("content[2].name", equalTo("Tennis & Padel Hub")) 
                .body("content[3].name", equalTo("Football Arena"))
                .body("totalElements", equalTo(4));  
    }

    @Test
    public void testGetClubById() {
        long id = 3L;
        given().when().get("/api/v1/clubs/{id}", id)
                .then().statusCode(200)
                .body("id", equalTo((int) id))
                .body("email", equalTo("tennis&padelhub@emeal.com"));
    }

    @Test
    public void testGetClubImage(){
        long id = 2L;
        given().when().get("/api/v1/clubs/{id}/image", id)
                .then().statusCode(200)
                .header("Content-Type", equalTo("image/jpeg"));
    }

    @Test
    public void testGetFilteredCLubs(){
        given().when().get("/api/v1/clubs?search=pro&city=Valencia&sport=padel")
            .then().statusCode(200)
            .body("content.size()",greaterThan(0))
            .body("content[0].name",equalTo("Padel Pro Center"))
            .body("content[0].city",equalTo("Valencia"))
            .body("content[0].sports.name", hasItem("Padel"))
            .body("content.size()", equalTo(1));
    }
    
}
