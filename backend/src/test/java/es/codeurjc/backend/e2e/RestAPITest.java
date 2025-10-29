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
                .body("content[0].sport", equalTo("Tenis"))
                .body("content[1].sport", equalTo("Padel"))
                .body("content[2].sport", equalTo("Tenis")) 
                .body("content[3].sport", equalTo("Futbol"))
                .body("totalElements", equalTo(4));  
    }

    @Test
    public void testGetMatchById() {
        long id = 1;
        given().when().get("/api/v1/matches/{id}", id)
                .then().statusCode(200)
                .body("id", equalTo((int) id))
                .body("organizer.realname", equalTo("Pedro Garcia"));
    }

    @Test
    public void testGetUsers(){
        given().when().get("/api/v1/users/")
                .then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].realname", equalTo("Pedro Garcia"))
                .body("[1].realname", equalTo("Maria Lopez"))
                .body("[2].realname", equalTo("Juan Martinez"))
                .body("[3].realname", equalTo("Luis Sanchez"));
    }

    @Test
    public void testGetUserById(){
        long id = 2;
        given().when().get("/api/v1/users/{id}", id)
                .then().statusCode(200)
                .body("id", equalTo((int) id))
                .body("realname", equalTo("Maria Lopez"));
    }

    @Test
    public void testGetUserImage(){
        long id = 1;
        given().when().get("/api/v1/users/{id}/image", id)
                .then().statusCode(200)
                .header("Content-Type", equalTo("image/jpeg"));
    }

    
}
    

