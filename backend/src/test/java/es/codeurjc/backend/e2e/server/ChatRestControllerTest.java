package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

        String cookie = loginAndGetCookie("pedro@emeal.com", "pedroga4");
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get("/api/v1/messages/")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0)); 
    }

    @Test
    public void testGetChatMessageById(){
        
        long id = 1L;

        String cookie = loginAndGetCookie("pedro@emeal.com", "pedroga4");
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get("/api/v1/messages/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) id))
            .body("content", notNullValue())
            .body("timestamp", notNullValue())
            .body("senderUsername", notNullValue())
            .body("matchId", notNullValue());

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
