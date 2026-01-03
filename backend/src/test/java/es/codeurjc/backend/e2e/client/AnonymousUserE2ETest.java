package es.codeurjc.backend.e2e.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Tag("e2e")
@TestMethodOrder(OrderAnnotation.class)
class AnonymousUserE2ETest extends BaseAngularUITest {

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
}
