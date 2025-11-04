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
import io.restassured.http.ContentType;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@ActiveProfiles("test")
public class LoginRestControllerTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test 
    public void testUserLoginValidCredentials(){
        String loginJson = """
            {
                "username": "pedro@emeal.com",
                "password": "pedroga4"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(loginJson)
        .when()
            .post("/api/v1/auth/login")
        .then()
            .statusCode(200) 
            .body("status", equalTo("SUCCESS"))
            .body("message", equalTo("Auth successful. Tokens are created in cookie."));
    }

    
    @Test 
    public void testUserLogout(){
        given().when().post("/api/v1/auth/logout")
                .then()
                .body("status", equalTo("SUCCESS"))
                .body("message", equalTo("logout successfully"));
    }

}
