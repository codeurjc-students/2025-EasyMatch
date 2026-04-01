package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class ChatRestControllerTest {
    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetChatMessages(){
        given()
        .when()
            .get("/api/v1/messages/")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0)); 
    }

    @Test
    public void testGetChatMessageById(){
        long id = 1L;
        given().
        when()
            .get("/api/v1/messages/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) id))
            .body("content", notNullValue())
            .body("timestamp", notNullValue())
            .body("senderUsername", notNullValue())
            .body("matchId", notNullValue());

    }
}
