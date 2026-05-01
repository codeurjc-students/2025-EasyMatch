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

    private static final String BASE_URL = "http://localhost";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String MESSAGES_ENDPOINT = "/api/v1/messages";
    private static final String USER_EMAIL = "pedro@emeal.com";
    private static final String USER_PASSWORD = "pedroga4";
    private static final String ADMIN_EMAIL = "admin@emeal.com";
    private static final String ADMIN_PASSWORD = "admin";   

    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void getChatMessagesAsRegularUserShouldReturnForbidden(){

        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(MESSAGES_ENDPOINT)
        .then()
            .statusCode(403)
            .body("message", equalTo("Solo el admin puede acceder a todos los mensajes")); 
    }

    @Test
    public void getChatMessagesAsAdminShouldReturnMessages(){

        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(MESSAGES_ENDPOINT)
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0)); 
    }

    @Test
    public void getChatMessageByIdShouldReturnMessage(){
        
        long id = 1L;

        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(MESSAGES_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) id))
            .body("content", notNullValue())
            .body("timestamp", notNullValue())
            .body("senderUsername", notNullValue())
            .body("matchId", notNullValue());

    }

    @Test
    public void getChatMessageByIdShouldReturnNotFoundForNonExistingMessage(){
        
        long id = 999L;

        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(MESSAGES_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(404);
    }

    @Test
    public void getChatMessageByIdShouldReturnForbiddenForUnauthorizedUser(){
        
        long id = 5L;

        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        assertNotNull(cookie);

        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(MESSAGES_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(403);
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
            .post(LOGIN_ENDPOINT)
        .then()
            .extract()
            .response();

        String cookie = loginResponse.getCookie("AuthToken");
        return cookie;
    }
}
