package es.codeurjc.backend.e2e;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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


@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
classes = es.codeurjc.easymatch.EasyMatchApplication.class
)

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
    public void testHomePage() {
        
        driver.get("http://localhost:" + port+"/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
        String match_header1 = driver.findElement(By.id("match-1")).findElement(By.id("organizer-sport")).getText();
        assertThat(match_header1, equalTo("Pedro - Tenis"));
        String match_header2 = driver.findElement(By.id("match-2")).findElement(By.id("organizer-sport")).getText();
        assertThat(match_header2, equalTo("Maria - Padel"));
        String match_header3 = driver.findElement(By.id("match-3")).findElement(By.id("organizer-sport")).getText();
        assertThat(match_header3, equalTo("Juan - Tenis"));
        String match_header4 = driver.findElement(By.id("match-4")).findElement(By.id("organizer-sport")).getText();
        assertThat(match_header4, equalTo("Luis - Futbol"));
    }


}
