package es.codeurjc.backend.e2e.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


@Tag("e2e")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
classes = es.codeurjc.easymatch.EasyMatchApplication.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")

public class AngularUITest {
    @LocalServerPort
    int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        String browser = System.getenv("BROWSER");
        port = 4200;
        if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            driver = new FirefoxDriver(options);
        } else if ("edge".equalsIgnoreCase(browser)) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless");
            driver = new EdgeDriver(options);
        } else { // default: Chrome
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            driver = new ChromeDriver(options);
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown(){
        driver.quit();
    }

    @Test
    @Order(1)
    public void verifyLoginPageLoads(){
        driver.get("http://localhost:" + port+"/");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form")));

        WebElement welcomeMessage = driver.findElement(By.id("welcome-message"));
        assertThat(welcomeMessage.getText(), equalTo("Bienvenido de vuelta"));

    }

    @Test
    @Order(2) 
    public void verifyRegisterPageLoadsAndWorks(){
        driver.get("http://localhost:" + port+"/");
        String email = "daniel@emeal.com";
        String password ="dani13";
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));

        WebElement registerLink = driver.findElement(By.id("register-link"));
        registerLink.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-register")));

        WebElement realnameInput = driver.findElement(By.cssSelector("input[formcontrolname='realname']"));
        realnameInput.sendKeys("Daniel Perez");

        WebElement usernameInput = driver.findElement(By.cssSelector("input[formcontrolname='username']"));
        usernameInput.sendKeys("nelmar");

        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        emailInput.sendKeys(email);

        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys(password);

        WebElement dateInput = driver.findElement(By.cssSelector("input[formcontrolname='birthDate']"));
        dateInput.sendKeys("06/05/2002");

        driver.findElement(By.cssSelector("mat-radio-group[formcontrolname='gender']"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-radio-button")));
        driver.findElements(By.cssSelector("mat-radio-button")).get(0).click();

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl(), containsString("/login"));

        loginUser(email,password);

        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        profileBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-user")));

        WebElement currentUser = driver.findElement(By.id("user-card"));
        assertThat(currentUser.findElement(By.cssSelector(".birthdate")).getText(), containsString("junio 2002"));

    }

    @Test
    @Order(3) 
    public void verifyHomePageLoads(){
        loginUser("pedro@emeal.com","pedroga4");

        List<WebElement> matches = driver.findElements(By.cssSelector(".match-card"));
        assertThat(matches.size(), greaterThan(0));
        WebElement firstMatch = matches.get(0);

        assertThat(firstMatch.findElement(By.cssSelector(".sport-title")).getText(), not(emptyString()));
        assertThat(firstMatch.findElement(By.cssSelector(".match-date")).getText(), matchesPattern("\\d{2}/\\d{2}/\\d{4}, \\d{2}:\\d{2}"));
        assertThat(firstMatch.findElement(By.cssSelector(".match-city")).getText(), containsString(","));
        assertThat(firstMatch.findElement(By.cssSelector(".organizer-name")).getText(), not(emptyString()));
        assertThat(firstMatch.findElement(By.cssSelector(".organizer-level")).getText(), containsString("Nivel"));
    }

    @Test
    @Order(4) 
    public void verifyLogoutWorks(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement optionsMenu = driver.findElement(By.className("user-info"));
        optionsMenu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logout-btn")));

        WebElement logoutBtn = driver.findElement(By.className("logout-btn"));
        logoutBtn.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl(), containsString("/login"));
        

    }

    @Test
    @Order(5) 
    public void verifyClubPageLoads(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement clubsBtn = driver.findElement(By.id("clubs-btn"));
        clubsBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-club")));

        List<WebElement> clubs = driver.findElements(By.cssSelector(".club-card"));
        assertThat(clubs.size(), greaterThan(0));
        WebElement firstClub = clubs.get(0);

        assertThat(firstClub.findElement(By.id("club-name")).getText(), not(emptyString()));
        assertThat(firstClub.findElement(By.cssSelector(".club-meta")).getText(), containsString("-"));
        List<WebElement> sports = firstClub.findElements(By.cssSelector(".sport"));
        assertThat(sports.size(), greaterThan(0));
        assertThat(firstClub.findElement(By.cssSelector(".price")).getText(), containsString("-"));
    }

    @Test 
    @Order(6)
    public void verifyJoinMatchAndMyMatchesPage(){
        loginUser("pedro@emeal.com","pedroga4");
        WebElement matchToJoin = driver.findElement(By.id("match-card4"));
        WebElement joinBtn = matchToJoin.findElement(By.className("join-button"));
        joinBtn.click();

        WebElement dialog = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-join-match-dialog")));
        
        WebElement title = dialog.findElement(By.cssSelector("h2.dialog-title"));
        assertThat(title.getText(), containsString("Unirse al partido"));

        WebElement teamA = dialog.findElement(By.xpath("//h3[text()='Equipo A']/parent::div"));
        List<WebElement> playersA = teamA.findElements(By.tagName("li"));
        assertThat(playersA.size(), greaterThanOrEqualTo(0));

        WebElement teamSelector = dialog.findElement(By.cssSelector("mat-select"));
        teamSelector.click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(1).click();

        WebElement confirmBtn = dialog.findElement(By.className("confirm-btn"));
        assertThat(confirmBtn.isEnabled(), equalTo(true));
        confirmBtn.click();

        WebElement optionsMenu = wait.until(
            ExpectedConditions.elementToBeClickable(By.className("user-info"))
        );
        optionsMenu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my-matches-btn")));

        WebElement myMatchesBtn = driver.findElement(By.className("my-matches-btn"));
        myMatchesBtn.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-my-matches")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));

        WebElement joinedMatch = driver.findElement(By.id("match-card4"));
        assertThat(joinedMatch.findElement(By.id("number-players")).getText(), containsString("2/14"));

        
    }

    @Test 
    @Order(7)
    public void verifyLeaveMatchAndMyMatchesPage(){
        loginUser("pedro@emeal.com","pedroga4");
        
        WebElement optionsMenu = wait.until(
            ExpectedConditions.elementToBeClickable(By.className("user-info"))
        );
        optionsMenu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my-matches-btn")));

        WebElement myMatchesBtn = driver.findElement(By.className("my-matches-btn"));
        myMatchesBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-my-matches")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
        WebElement joinedMatch = driver.findElement(By.id("match-card4"));

        WebElement leaveBtn = joinedMatch.findElement(By.className("leave-button"));
        leaveBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-confirm-dialog")));

        WebElement confirmBtn = driver.findElement(By.id("confirm-btn"));
        confirmBtn.click();

        WebElement matchesBtn = driver.findElement(By.id("matches-btn"));
        matchesBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
        WebElement matchLeft = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("match-card4"))
        );
        WebElement numberPlayers = matchLeft.findElement(By.id("number-players"));
        assertThat(numberPlayers.getText(), containsString("1/14"));
    }

    @Test
    @Order(9) 
    public void verifyProfilePageLoadsAndAccountDeletionWorks(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        profileBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-user")));

        WebElement currentUser = driver.findElement(By.id("user-card"));
        assertThat(currentUser.findElement(By.cssSelector(".username")).getText(), equalTo("@pedro123"));
        assertThat(currentUser.findElement(By.cssSelector(".birthdate")).getText(), containsString("mayo 1990"));
        assertThat(currentUser.findElement(By.cssSelector(".description")).getText(), equalTo("Apasionado del tenis"));
        assertThat(currentUser.findElement(By.id("totalMatches")).getText(), containsString("3"));
        assertThat(currentUser.findElement(By.id("wins")).getText(), containsString("1"));
        assertThat(currentUser.findElement(By.id("winRate")).getText(), containsString("33,33%"));
        assertThat(currentUser.findElement(By.id("maxLevel")).getText(), containsString("5,12"));
        
        WebElement deleteBtn = driver.findElement(By.id("delete_account_btn"));
        deleteBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-confirm-dialog")));

        WebElement confirmBtn = driver.findElement(By.id("confirm-btn"));
        confirmBtn.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertThat(alert.getText(), equalTo("Tu cuenta ha sido eliminada correctamente."));
        alert.accept();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl(), containsString("/login"));

    }

    @Test 
    @Order(8)
    public void verifyCreateMatchPageAndSubmitForm(){
        loginUser("pedro@emeal.com","pedroga4");
        WebElement createMatchBtn = driver.findElement(By.id("create-match-btn"));
        createMatchBtn.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match-create")));

        WebElement clubSelector = driver.findElement(By.cssSelector("mat-select[formcontrolname='club']"));
        clubSelector.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(1).click();

        WebElement sportSelector = driver.findElement(By.cssSelector("mat-select[formcontrolname='sport']"));
        sportSelector.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click();

        WebElement modeSelector = driver.findElement(By.cssSelector("mat-select[formcontrolname='mode']"));
        modeSelector.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click();

        WebElement priceInput = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        priceInput.sendKeys("20");

        WebElement dateInput = driver.findElement(By.cssSelector("input[formcontrolname='date']"));
        dateInput.sendKeys("11/15/2025");

        WebElement timeInput = driver.findElement(By.cssSelector("input[formcontrolname='time']"));
        timeInput.sendKeys("18:30");

        WebElement typeSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='type']"));
        typeSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click();

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/matches"));
        assertThat(driver.getCurrentUrl(), containsString("/matches"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));

        
        
        WebElement match1 = driver.findElement(By.id("match-card9")).findElement(By.className("organizer-name"));
        assertThat(match1.getText(), equalTo("Pedro Garcia"));

    }

    private void loginUser(String email, String password) {
        driver.get("http://localhost:" + port+"/");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));

        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);

        WebElement loginButton = driver.findElement(By.cssSelector("button.btn-login"));
        loginButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
    }



}
