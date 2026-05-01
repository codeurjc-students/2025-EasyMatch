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

    private static final String BASE_URL = "http://localhost";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/v1/auth/logout";
    private static final String MATCHES_ENDPOINT = "/api/v1/matches";
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
    public void getMatchesShouldReturnMatches() {
        given()
        .when()
            .get(MATCHES_ENDPOINT + "/")
        .then()
            .statusCode(200)
            .body("content", not(empty()))
            .body("totalElements", greaterThan(0))
            .body("content[0].id", notNullValue());  
    }

    @Test
    @Order(2)
    public void getFilteredMatchesShouldReturnFilteredMatches(){
        given()
        .when()
            .get(MATCHES_ENDPOINT + "?search=tennis&sport=tenis&timeRange=morning&includeFriendlies=true")
        .then()
            .statusCode(200)
            .body("content.sport.name", everyItem(equalTo("Tenis")))
            .body("content.club.name", everyItem(matchesRegex("(?i).*tennis.*")))
            .body("content.state",everyItem(equalTo(true)))
            .body("content.size()", equalTo(1));
    }

    @Test
    @Order(3)
    public void getMatchByIdShouldReturnMatch() {
        long id = 1L;
        given()
        .when()
            .get(MATCHES_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("organizer.realname", equalTo("Pedro Garcia"));
    }

    @Test
    @Order(4)
    public void createMatchShouldSucceed(){
        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);

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
            .post(MATCHES_ENDPOINT)
        .then()
            .statusCode(201)
            .body("state", equalTo(true))
            .body("team1Players",not(empty()))
            .body("date", equalTo("2025-11-25T19:30:00"))
            .body("organizer.username", equalTo("pedro123"));
    }

    @Test
    @Order(5)
    public void joinMatchShouldSucceed(){
        long id = 4L;
        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);

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
            .put(MATCHES_ENDPOINT + "/{id}/users/me", id)
        .then()
            .statusCode(200)
            .body("status",equalTo("SUCCESS"))
            .body("message", equalTo("Player added to team B"));
    }

    @Test
    @Order(6)
    public void leaveMatchShouldSucceed(){
        long id = 4L;
        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);

        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .delete(MATCHES_ENDPOINT + "/{id}/users/me", id)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(7)
    public void replaceMatchShouldSucceed() {
        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
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
            .put(MATCHES_ENDPOINT + "/{id}", matchId)
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
    public void deleteMatchAsAdminShouldSucceed() {
        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
        long matchIdToDelete = 3L; 

        given()
            .cookie("AuthToken", cookie)
        .when()
            .delete(MATCHES_ENDPOINT + "/{id}", matchIdToDelete)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) matchIdToDelete));

        given()
        .when()
            .get(MATCHES_ENDPOINT + "/{id}", matchIdToDelete)
        .then()
            .statusCode(404);
    }

    @Test
    @Order(9)
    public void updateMatchResultShouldFail(){
        long matchId = 1L;
        String cookie = loginAndGetCookie(USER_EMAIL, USER_PASSWORD);
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
            .put(MATCHES_ENDPOINT + "/{id}/result", matchId)
        .then()
            .statusCode(409)
            .body("message", equalTo("No se puede añadir el resultado a un partido incompleto"));
    }

    @Test
    @Order(10)
    public void updateFullMatchResultShouldSucceed(){
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
            .put(MATCHES_ENDPOINT + "/{id}/users/me", matchId)
        .then()
            .statusCode(200)
            .body("status",equalTo("SUCCESS"))
            .body("message", equalTo("Player added to team B"));
        
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .post(LOGOUT_ENDPOINT)
                .then()
                .body("status", equalTo("SUCCESS"))
                .body("message", equalTo("logout successfully"));   

        cookie = loginAndGetCookie(USER_EMAIL,USER_PASSWORD);
        
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
            .post(MATCHES_ENDPOINT + "/{id}/result", matchId)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(11)
    public void addPlayerToMatchAsAdminShouldSucceed(){
        long matchId = 2L;
        long playerId = 6L;
        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .post(MATCHES_ENDPOINT + "/{matchId}/team1Players/{playerId}", matchId,playerId)
        .then()
            .statusCode(200)
            .body("status", equalTo("SUCCESS"))
            .body("message", equalTo("Player added to team 1"));
    }

    @Test
    @Order(12)
    public void removePlayerFromMatchAsAdminShouldSucceed(){
        long matchId = 2L;
        long playerId = 6L;
        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .delete(MATCHES_ENDPOINT + "/{matchId}/team1Players/{playerId}", matchId,playerId)
        .then()
            .statusCode(200)
            .body("status", equalTo("SUCCESS"))
            .body("message", equalTo("Player removed from team 1"));
    }

    @Test
    @Order(13)
    public void getMatchMessagesShouldSucceed(){
        long matchId = 1L;
        String cookie = loginAndGetCookie(USER_EMAIL,USER_PASSWORD);
        given()
            .contentType(ContentType.JSON)
            .cookie("AuthToken", cookie)
        .when()
            .get(MATCHES_ENDPOINT + "/{id}/messages", matchId)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("id", everyItem(notNullValue()))
            .body("content", everyItem(notNullValue()))
            .body("senderUsername", everyItem(notNullValue()))
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
