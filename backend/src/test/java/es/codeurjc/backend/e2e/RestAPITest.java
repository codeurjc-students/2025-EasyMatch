package es.codeurjc.backend.e2e;

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
        given().when().get("/api/v1/matches/")
                .then().statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].organizer.realname", equalTo("Pedro Garcia"))
                .body("content[1].organizer.realname", equalTo("Maria Lopez"))
                .body("content[2].organizer.realname", equalTo("Juan Martinez")) 
                .body("content[3].organizer.realname", equalTo("Luis Sanchez"))
                .body("totalElements", equalTo(4));
        
        
    }
}
    

