package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

public class UserRestControllerTest {
    
    @LocalServerPort
    private int port;
    
    private static final String BASE_URL = "http://localhost";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String USERS_ENDPOINT = "/api/v1/users";
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
    @Order(1)
    public void getUsersShouldReturnUsers(){
        given()
        .when()
            .get(USERS_ENDPOINT + "/")
        .then()
            .statusCode(200)
            .body("content", not(empty()))
            .body("size()", greaterThan(0))
            .body("content[0].id", notNullValue()); 
    }

    @Test
    @Order(2)
    public void getUserByIdShouldReturnUser(){
        long id = 3L;
        given().
        when()
            .get(USERS_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("realname", equalTo("Maria Lopez"));
    }

    @Test
    @Order(3)
    public void getUserImageShouldReturnImage(){
        long id = 1L;
        given()
        .when()
            .get(USERS_ENDPOINT + "/{id}/image", id)
        .then()
            .statusCode(200)
            .header("Content-Type", equalTo("image/jpeg"));
    }

    @Test
    @Order(4)
    public void deleteUserShouldSucceed(){
        long id = 4L;
        String cookie = loginAndGetCookie("juan@emeal.com", "juanma1");

        given()
            .cookie("AuthToken", cookie)
        .when()
            .delete(USERS_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("realname", equalTo("Juan Martinez"));
    }

    @Test
    @Order(5)
    public void createUserShouldSucceed(){
        String newUserJson = """
            {
                "realname": "Daniel Perez",
                "username": "nelmar",
                "email": "daniel@emeal.com",
                "password": "daniel6",
                "birthDate": "2003-06-05T00:00:00Z",
                "gender": true,
                "description": "El tenis es mi pasion"
            }
        """;
        given()
            .contentType(ContentType.JSON)
            .body(newUserJson)
        .when()
            .post(USERS_ENDPOINT + "/")
        .then()
            .statusCode(201)
            .body("realname",equalTo("Daniel Perez"))
            .body("roles", hasItem("USER"));

    }

    @Test
    @Order(6)
    public void replaceUserAsAdminShouldSucceed(){

        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
        assertThat(cookie, notNullValue());

        String editedUserJson = """
            {
                "realname": "Temporal User",
                "username": "temporalUser",
                "email": "temporal@emeal.com",
                "password": "tempPass",
                "birthDate": "1990-01-01T00:00:00Z",
                "gender": true,
                "description": "Temporal test user"
            }
        """;

        Response editResponse = given()
                .contentType(ContentType.JSON)
                .cookie("AuthToken", cookie)
                .body(editedUserJson)
        .when()
                .post(USERS_ENDPOINT + "/")
        .then()
                .statusCode(201)
                .extract()
                .response();

        long editedId = Integer.toUnsignedLong(editResponse.path("id"));
        assertThat(editedId, greaterThan(0L));

        String replaceJson = """
            {
                "realname": "Usuario Reemplazado",
                "username": "userReplaced",
                "email": "replaced@emeal.com",
                "password": "newPass123",
                "birthDate": "1999-09-09T00:00:00Z",
                "gender": false,
                "description": "Usuario modificado con PUT"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .cookie("AuthToken", cookie)
                .body(replaceJson)
        .when()
                .put(USERS_ENDPOINT + "/{id}", editedId)
        .then()
                .statusCode(200)
                .body("id", equalTo((int) editedId))
                .body("realname", equalTo("Usuario Reemplazado"))
                .body("username", equalTo("userReplaced"))
                .body("email", equalTo("replaced@emeal.com"));

    }

    @Test
    @Order(7)
    public void replaceUserAsRegularUserShouldSucceed(){

        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        assertThat(cookie, notNullValue());
        long userId = 2L; 
        String replaceJson = """
            {
                "realname": "Usuario Reemplazado",
                "username": "userReplaced",
                "email": "replaced@emeal.com",
                "password": "newPass123",
                "birthDate": "1999-09-09T00:00:00Z",
                "gender": false,
                "description": "Usuario modificado con PUT"
            }
        """;
        given()
                .contentType(ContentType.JSON)
                .cookie("AuthToken", cookie)
                .body(replaceJson)
        .when()
                .put(USERS_ENDPOINT + "/{id}", userId)
        .then()
                .statusCode(200)
                .body("id", equalTo((int) userId))
                .body("realname", equalTo("Usuario Reemplazado"))
                .body("username", equalTo("userReplaced"))
                .body("email", equalTo("replaced@emeal.com"));
    }

    @Test
    public void getUserMessagesShouldReturnMessages(){
        long userId = 2L; 
        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
        given()
            .cookie("AuthToken", cookie)
        .when()
            .get(USERS_ENDPOINT + "/{id}/messages/", userId)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("id", everyItem(notNullValue()))
            .body("content", everyItem(notNullValue()))
            .body("sender.id", everyItem(notNullValue()))
            .body("timestamp", everyItem(notNullValue()));
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
