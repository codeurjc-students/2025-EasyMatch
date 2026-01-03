package es.codeurjc.backend.e2e.client;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

@SpringBootTest (
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = es.codeurjc.easymatch.EasyMatchApplication.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public abstract class BaseAngularUITest {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected WebDriverWait wait;

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }


    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void waitForAngularToFinish() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript(
                        "return window.getAllAngularTestabilities && " +
                        "window.getAllAngularTestabilities()" +
                        ".findIndex(x=>!x.isStable()) === -1"
                    ).equals(true)
            );
        } catch (Exception ignored) {}
    }

    protected void waitForPageReload() {
        wait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
        waitForAngularToFinish();
    }

    protected void loginUser(String email, String password) {
        driver.get("http://localhost:" + port + "/");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-root")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-login")));

        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);

        WebElement loginButton = driver.findElement(By.cssSelector("button.btn-login"));
        loginButton.click();
        if(!email.contains("admin"))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
    }
}
