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
public class MatchRestControllerTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetMatches() {
        given().when().get("/api/v1/matches/")
                .then().statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].sport.name", equalTo("Tenis"))
                .body("content[1].sport.name", equalTo("Padel"))
                .body("content[2].sport.name", equalTo("Tenis")) 
                .body("content[3].sport.name", equalTo("Futbol"))
                .body("totalElements", equalTo(4));  
    }

    @Test
    public void testGetFilteredMatches(){
        given().when().get("/api/v1/matches?search=tennis&sport=tenis&timeRange=morning&includeFriendlies=true")
            .then().statusCode(200)
            .body("content.size()",greaterThan(0))
            .body("content[0].club.name",equalTo("Tennis Club Elite"))
            .body("content[0].date",equalTo("2025-09-30T12:30:00"))
            .body("content[1].club.name",equalTo("Tennis & Padel Hub"))
            .body("content[1].date",equalTo("2025-10-03T10:30:00"))
            .body("content.size()", equalTo(2));
    }

    @Test
    public void testGetMatchById() {
        long id = 1L;
        given().when().get("/api/v1/matches/{id}", id)
                .then().statusCode(200)
                .body("id", equalTo((int) id))
                .body("organizer.realname", equalTo("Pedro Garcia"));
    }

}
