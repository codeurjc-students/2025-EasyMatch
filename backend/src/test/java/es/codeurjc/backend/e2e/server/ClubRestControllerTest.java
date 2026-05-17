package es.codeurjc.backend.e2e.server;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
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
public class ClubRestControllerTest {

    @LocalServerPort
    private int port;

    private static final String BASE_URL = "http://localhost";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String CLUBS_ENDPOINT = "/api/v1/clubs";
    private static final String ADMIN_EMAIL = "admin@emeal.com";
    private static final String ADMIN_PASSWORD = "admin";

    @BeforeEach
        public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @Order(1)
    public void getClubsShouldReturnClubs() {
        given()
        .when()
            .get(CLUBS_ENDPOINT + "/")
        .then().statusCode(200)
            .body("content", not(empty()))
            .body("totalElements", greaterThan(0))
            .body("content[0].id", notNullValue());  
    }

    @Test
    @Order(2)
    public void getFilteredClubsShouldReturnFilteredClubs(){
        given()
        .when()
            .get(CLUBS_ENDPOINT + "?search=pro&city=Valencia&sport=padel")
        .then()
            .statusCode(200)
            .body("content.size()",greaterThan(0))
            .body("content.name", everyItem(matchesRegex("(?i).*pro.*")))
            .body("content.city",everyItem(equalTo("Valencia")))
            .body("content.sports.name", everyItem(hasItem("Padel")))
            .body("content.size()", equalTo(1));
    }

    @Test
    @Order(3)
    public void getClubByIdShouldReturnClub() {
        long id = 3L;
        given()
        .when()
            .get(CLUBS_ENDPOINT + "/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(Math.toIntExact(id)))
            .body("name", equalTo("Club Tenis y Pádel Bético"));
    }

    @Test
    @Order(4)
    public void getClubImageShouldReturnImage(){
        long id = 2L;
        given()
        .when()
            .get(CLUBS_ENDPOINT + "/{id}/image", id)
        .then()
            .statusCode(200)
            .header("Content-Type", equalTo("image/jpeg"));
    }

    @Test
    @Order(5)
    public void createClubShouldSucceed() {

        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);

        String newClubJson = """
            {
                "name": "Club Selenium Test",
                "phone": "612345678",
                "email": "selenium@club.com",
                "web": "www.seleniumclub.com",
                "address": "Calle Test 123",
                "city": "Madrid",
                "schedule":{
                    "openingTime": "08:00",
                    "closingTime": "23:00"
                },
                "priceRange":{
                    "minPrice": 5,
                    "maxPrice": 15,
                    "unit": "€/hora"
                }
            }
        """;

        given()
            .cookie("AuthToken", cookie)
            .contentType(ContentType.JSON)
            .body(newClubJson)
        .when()
            .post(CLUBS_ENDPOINT + "/")
        .then()
            .statusCode(201)
            .body("name", equalTo("Club Selenium Test"))
            .body("city", equalTo("Madrid"))
            .body("priceRange.minPrice", equalTo(5f))
            .body("priceRange.maxPrice", equalTo(15f));
    }

    @Test
    @Order(6)
    public void replaceClubShouldSucceed() {

        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);

        long clubIdToEdit = 1L;

        String updatedClubJson = """
            {
                "name": "Club Editado Test",
                "phone": "699999999",
                "email": "editado@club.com",
                "web": "www.editado.com",
                "address": "Nueva dirección 456",
                "city": "Barcelona",
                "schedule":{
                    "openingTime": "09:00",
                    "closingTime": "22:00"
                },
                "priceRange":{
                    "minPrice": 6,
                    "maxPrice": 20,
                    "unit": "€/hora"
                }   
            }
        """;

        given()
            .cookie("AuthToken", cookie)
            .contentType(ContentType.JSON)
            .body(updatedClubJson)
        .when()
            .put(CLUBS_ENDPOINT + "/{id}", clubIdToEdit)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) clubIdToEdit))
            .body("name", equalTo("Club Editado Test"))
            .body("city", equalTo("Barcelona"))
            .body("priceRange.maxPrice", equalTo(20f));
    }

    @Test
    @Order(7)
    public void deleteClubShouldSucceed() {

        String cookie = loginAndGetCookie(ADMIN_EMAIL, ADMIN_PASSWORD);

        long clubIdToDelete = 5L;

        given()
            .cookie("AuthToken", cookie)
        .when()
            .delete(CLUBS_ENDPOINT + "/{id}", clubIdToDelete)
        .then()
            .statusCode(200)
            .body("id", equalTo((int) clubIdToDelete));

        given()
        .when()
            .get(CLUBS_ENDPOINT + "/{id}", clubIdToDelete)
        .then()
            .statusCode(404);
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
