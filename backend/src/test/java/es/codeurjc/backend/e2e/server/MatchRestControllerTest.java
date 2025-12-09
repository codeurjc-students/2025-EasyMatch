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
public class MatchRestControllerTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @Order(1)
    public void testGetMatches() {
        given()
        .when()
            .get("/api/v1/matches/")
        .then()
            .statusCode(200)
            .body("content", not(empty()))
            .body("totalElements", greaterThan(0))
            .body("content[0].id", notNullValue());  
    }

    @Test
    @Order(2)
    public void testGetFilteredMatches(){
        given()
        .when()
            .get("/api/v1/matches?search=tennis&sport=tenis&timeRange=morning&includeFriendlies=true")
        .then()
            .statusCode(200)
            .body("content.sport.name", everyItem(equalTo("Tenis")))
            .body("content.club.name", everyItem(matchesRegex("(?i).*tennis.*")))
            .body("content.state",everyItem(equalTo(true)))
            .body("content.size()", equalTo(1));
    }

    @Test
    @Order(3)
    public void testGetMatchById() {
        long id = 1L;
        given()
        .when()
            .get("/api/v1/matches/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("organizer.realname", equalTo("Pedro Garcia"));
    }

    @Test
    @Order(4)
    public void testCreateMatch(){
        String cookie = loginAndGetCookie();

        String newMatchJson = """
            {
                "date": "2025-11-25T19:30:00Z",
                "type": true,
                "isPrivate": false,
                "price": 10.5,
                "sport":{
                    "id": 1,
                    "name": "Tenis",
                    "modes": [
                        {
                            "name": "Singles",
                            "playersPerGame": 2
                        },
                        {
                            "name": "Doubles",
                            "playersPerGame": 4
                        }
                    ]
                },
                "club":{
                    "id": 1,
                    "name": "Tennis Club Elite",
                    "city": "Madrid"
                }
            }
        """;
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
            .body(newMatchJson)
        .when()
            .post("/api/v1/matches")
        .then()
            .statusCode(201)
            .body("state", equalTo(true))
            .body("team1Players",not(empty()))
            .body("date", equalTo("2025-11-25T19:30:00"))
            .body("organizer.username", equalTo("pedro123"));
    }

    @Test
    @Order(5)
    public void testJoinMatch(){
        long id = 4L;
        String cookie = loginAndGetCookie();

        String teamSelectedJson = """
            {
            "team": "B"
            }
        """;
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
            .body(teamSelectedJson)
        .when()
            .put("/api/v1/matches/{id}/users/me", id)
        .then()
            .statusCode(200)
            .body("status",equalTo("SUCCESS"))
            .body("message", equalTo("Player added to team B"));
    }

    @Test
    @Order(6)
    public void testLeaveMatch(){
        long id = 4L;
        String cookie = loginAndGetCookie();

        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .delete("/api/v1/matches/{id}/users/me", id)
        .then()
            .statusCode(200);
    }
    
    private String loginAndGetCookie() {
        String loginJson = """
            {
                "username": "pedro@emeal.com",
                "password": "pedroga4"
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
        return cookie;
    }
}
