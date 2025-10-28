package es.codeurjc.backend.e2e;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.ActiveProfiles;

@Tag("e2e")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
classes = es.codeurjc.easymatch.EasyMatchApplication.class
)
@ActiveProfiles("test")

public class AngularUITest {
    @LocalServerPort
    int port;

    private WebDriver driver;

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
    }

    @AfterEach
    public void tearDown(){
        driver.quit();
    }

    @Test
    public void testLoginPage(){
        driver.get("http://localhost:" + port+"/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form")));

        WebElement welcomeMessage = driver.findElement(By.id("welcome-message"));
        assertThat(welcomeMessage.getText(), equalTo("Bienvenido de vuelta"));

    }

    @Test 
    public void testHomePage(){
         driver.get("http://localhost:" + port+"/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));

        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        emailInput.sendKeys("pedro@emeal.com");
        passwordInput.sendKeys("pedroga4");

        WebElement loginButton = driver.findElement(By.cssSelector("button.btn-login"));
        loginButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));

        WebElement match1 = driver.findElement(By.id("match-card1")).findElement(By.className("organizer-name"));
        assertThat(match1.getText(), equalTo("Pedro Garcia"));
        WebElement match2 = driver.findElement(By.id("match-card2")).findElement(By.className("organizer-name"));
        assertThat(match2.getText(), equalTo("Maria Lopez"));
        WebElement match3 = driver.findElement(By.id("match-card3")).findElement(By.className("organizer-name"));
        assertThat(match3.getText(), equalTo("Juan Martinez"));
        WebElement match4 = driver.findElement(By.id("match-card4")).findElement(By.className("organizer-name"));
        assertThat(match4.getText(), equalTo("Luis Sanchez"));
    }


}
