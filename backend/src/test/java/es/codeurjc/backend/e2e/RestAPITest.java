package es.codeurjc.backend.e2e;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
public class RestAPITest {

    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetMatches() {
        given().when().get("/api/matches/")
                .then().statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].organizer", equalTo("Pedro"))
                .body("content[1].organizer", equalTo("Maria"))
                .body("content[2].organizer", equalTo("Juan")) 
                .body("content[3].organizer", equalTo("Luis"))
                .body("totalElements", equalTo(4));
        
        
    }
}
    

