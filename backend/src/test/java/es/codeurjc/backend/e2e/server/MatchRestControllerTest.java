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
        given().when().get("/api/v1/matches/")
                .then().statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].sport.name", equalTo("Tenis"))
                .body("content[1].sport.name", equalTo("Padel"))
                .body("content[2].sport.name", equalTo("Tenis")) 
                .body("content[3].sport.name", equalTo("Futbol"))
                .body("totalElements", equalTo(4));  
    }

    @Test
    @Order(2)
    public void testGetFilteredMatches(){
        given().when().get("/api/v1/matches?search=tennis&sport=tenis&timeRange=morning&includeFriendlies=true")
            .then().statusCode(200)
            .body("content.size()",greaterThan(0))
            .body("content[0].club.name",equalTo("Tennis Club Elite"))
            .body("content[0].date",equalTo("2025-09-30T12:30:00"))
            .body("content[1].club.name",equalTo("Tennis & Padel Hub"))
            .body("content[1].date",equalTo("2025-10-03T10:30:00"))
            .body("content.size()", equalTo(2));
    }

    @Test
    @Order(3)
    public void testGetMatchById() {
        long id = 1L;
        given().when().get("/api/v1/matches/{id}", id)
                .then().statusCode(200)
                .body("id", equalTo((int) id))
                .body("organizer.realname", equalTo("Pedro Garcia"));
    }

    @Test
    @Order(4)
    public void testCreateMatch(){
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
            .body("date", equalTo("2025-11-25T19:30:00"))
            .body("organizer.username", equalTo("pedro123"))
            .body("sport.name",equalTo("Tenis"));

    }

}
