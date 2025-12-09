package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")

public class UserRestControllerTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @Order(1)
    public void testGetUsers(){
        given()
        .when()
            .get("/api/v1/users/")
        .then()
            .statusCode(200)
            .body("content", not(empty()))
            .body("size()", greaterThan(0))
            .body("content[0].id", notNullValue()); 
    }

    @Test
    @Order(2)
    public void testGetUserById(){
        long id = 3L;
        given().
        when()
            .get("/api/v1/users/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("realname", equalTo("Maria Lopez"));
    }

    @Test
    @Order(3)
    public void testGetUserImage(){
        long id = 1L;
        given()
        .when()
            .get("/api/v1/users/{id}/image", id)
        .then()
            .statusCode(200)
            .header("Content-Type", equalTo("image/jpeg"));
    }

    @Test
    @Order(4)
    public void testDeleteUser(){
        long id = 4L;
        String loginJson = """
            {
                "username": "juan@emeal.com",
                "password": "juanma1"
            }
        """;
        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginJson)
        .when()
            .post("/api/v1/auth/login")
        .then()
            .extract()
            .response();

        String cookie = loginResponse.getCookie("AuthToken");

        given()
            .cookie("AuthToken", cookie)
        .when()
            .delete("/api/v1/users/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("realname", equalTo("Juan Martinez"));
    }

    @Test
    @Order(5)
    public void testCreateUser(){
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
            .post("/api/v1/users/")
        .then()
            .statusCode(201)
            .body("level",equalTo(0.0f))
            .body("realname",equalTo("Daniel Perez"))
            .body("stats.totalMatches", equalTo(0));

    }

}
