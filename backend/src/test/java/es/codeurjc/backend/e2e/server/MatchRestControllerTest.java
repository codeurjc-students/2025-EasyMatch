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
        String cookie = loginAndGetCookie("pedro@emeal.com","pedroga4");

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
        String cookie = loginAndGetCookie("pedro@emeal.com","pedroga4");

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
        String cookie = loginAndGetCookie("pedro@emeal.com","pedroga4");

        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .delete("/api/v1/matches/{id}/users/me", id)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(7)
    public void testReplaceMatch() {
        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        long matchId = 1L; 

        String updatedMatchJson = """
            {
                "date": "2025-12-01T20:00:00Z",
                "type": false,
                "isPrivate": true,
                "price": 15.0,
                "sport":{
                    "id": 1,
                    "name": "Tenis"
                },
                "club":{
                    "id": 2,
                    "name": "Padel Center Pro",
                    "city": "Barcelona"
                }
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
            .body(updatedMatchJson)
        .when()
            .put("/api/v1/matches/{id}", matchId)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) matchId))
            .body("price", equalTo(15.0f))
            .body("isPrivate", equalTo(true))
            .body("type", equalTo(false))
            .body("club.city", equalTo("Barcelona"))
            .body("date", equalTo("2025-12-01T20:00:00"));
    }

    @Test
    @Order(8)
    public void testDeleteMatchAsAdmin() {
        String cookie = loginAndGetCookie("admin@emeal.com","admin");
        long matchIdToDelete = 3L; 

        given()
            .cookie("AuthToken", cookie)
        .when()
            .delete("/api/v1/matches/{id}", matchIdToDelete)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) matchIdToDelete));

        given()
        .when()
            .get("/api/v1/matches/{id}", matchIdToDelete)
        .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    public void testAddOrUpdateIncompleteMatchResult(){
        long matchId = 1L;
        String cookie = loginAndGetCookie("pedro@emeal.com","pedroga4");
        String resultJson = """
            {
                "team1Name": "A",
                "team2Name": "B",
                "team1GamesPerSet": [6, 6],
                "team2GamesPerSet": [4, 3]
            }
        """;
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
            .body(resultJson)
        .when()
            .put("/api/v1/matches/{id}/result", matchId)
        .then()
            .statusCode(409)
            .body("message", equalTo("No se puede añadir el resultado a un partido incompleto"));
    }

    @Test
    @Order(10)
    public void testAddOrUpdateFullMatchResult(){
        long matchId = 1L;
        String cookie = loginAndGetCookie("silvia@emeal.com","silvia5");
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
            .put("/api/v1/matches/{id}/users/me", matchId)
        .then()
            .statusCode(200)
            .body("status",equalTo("SUCCESS"))
            .body("message", equalTo("Player added to team B"));
        
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .post("/api/v1/auth/logout")
                .then()
                .body("status", equalTo("SUCCESS"))
                .body("message", equalTo("logout successfully"));   

        cookie = loginAndGetCookie("pedro@emeal.com","pedroga4");
        
        String resultJson = """
            {
                "team1Name": "A",
                "team2Name": "B",
                "team1GamesPerSet": [6, 3, 7],
                "team2GamesPerSet": [4, 6, 5]
            }
        """;
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
            .body(resultJson)
        .when()
            .put("/api/v1/matches/{id}/result", matchId)
        .then()
            .statusCode(200);
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
