package es.codeurjc.backend.e2e.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Tag("e2e")
@TestMethodOrder(OrderAnnotation.class)
class RegisteredUserAngularTest extends BaseAngularUITest {
    @Test
    @Order(1) 
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
    @Order(2) 
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
    @Order(3) 
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
    @Order(4)
    public void verifyJoinMatchAndMyMatchesPage(){
        loginUser("pedro@emeal.com","pedroga4");
        WebElement matchToJoin = driver.findElement(By.id("match-card4"));
        WebElement joinBtn = matchToJoin.findElement(By.className("join-button"));
        scrollIntoView(joinBtn);
        joinBtn.click();

        WebElement dialog = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-join-match-dialog")));

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

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("app-confirm-dialog")));
        
        waitForPageReload();
        By optionsMenu = By.className("user-info");

        wait.until(d ->
            d.findElements(optionsMenu).size() == 1 &&
            d.findElement(optionsMenu).isDisplayed()
        );

        wait.until(ExpectedConditions.elementToBeClickable(optionsMenu));
        driver.findElement(optionsMenu).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my-matches-btn")));

        WebElement myMatchesBtn = driver.findElement(By.className("my-matches-btn"));
        scrollIntoView(myMatchesBtn);
        myMatchesBtn.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-my-matches")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));

        WebElement joinedMatch = driver.findElement(By.id("match-card4"));
        assertThat(joinedMatch.findElement(By.id("number-players")).getText(), containsString("2/14"));
    }

    @Test
    @Order(5)
    public void verifyLeaveMatchAndMyMatchesPage(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement optionsMenu = wait.until(
            ExpectedConditions.elementToBeClickable(By.className("user-info"))
        );
        optionsMenu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my-matches-btn")));
        driver.findElement(By.className("my-matches-btn")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-my-matches")));

        WebElement joinedMatch = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("match-card4"))
        );

        WebElement leaveBtn = joinedMatch.findElement(By.className("leave-button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", leaveBtn);
        wait.until(ExpectedConditions.elementToBeClickable(leaveBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", leaveBtn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-confirm-dialog")));

        WebElement confirmBtn = driver.findElement(By.id("confirm-btn"));
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("app-confirm-dialog")));

        driver.findElement(By.id("matches-btn")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector("#match-card4 #number-players"),
            "1/14"
        ));

        String text = driver.findElement(By.cssSelector("#match-card4 #number-players")).getText();
        assertThat(text, containsString("1/14"));
    }

    @Test
    @Order(7)
    public void verifyProfileEditionWorks(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        profileBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-user")));

        WebElement editBtn = driver.findElement(By.id("edit-profile-btn"));
        editBtn.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("edit-form")));

        WebElement descriptionInput = driver.findElement(By.cssSelector("textarea[formcontrolname='description']"));
        descriptionInput.clear();
        descriptionInput.sendKeys("Nuevo perfil actualizado");

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("save-profile-btn")));
        saveButton.click();

        wait.until(ExpectedConditions.urlContains("/profile"));
        assertThat(driver.getCurrentUrl(), containsString("/profile"));

    }

    @Test
    @Order(10) 
    public void verifyProfilePageLoadsAndAccountDeletionWorks(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement profileBtn = driver.findElement(By.id("profile-btn"));
        profileBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-user")));

        WebElement currentUser = driver.findElement(By.id("user-card"));
        assertThat(currentUser.findElement(By.cssSelector(".username")).getText(), equalTo("@pedro123"));
        assertThat(currentUser.findElement(By.cssSelector(".birthdate")).getText(), containsString("mayo 1990"));
        assertThat(currentUser.findElement(By.cssSelector(".description")).getText(), equalTo("Nuevo perfil actualizado"));
        assertThat(currentUser.findElement(By.id("totalMatches")).getText(), containsString("3"));
        assertThat(currentUser.findElement(By.id("wins")).getText(), containsString("2"));
        assertThat(currentUser.findElement(By.id("winRate")).getText(), containsString("66,67%"));
        assertThat(currentUser.findElement(By.id("maxLevel")).getText(), containsString("5,12"));
        
        WebElement deleteBtn = driver.findElement(By.id("delete_account_btn"));
        deleteBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-confirm-dialog")));

        WebElement confirmBtn = driver.findElement(By.id("confirm-btn"));
        confirmBtn.click();

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

    @Test
    @Order(9)
    public void verifyMatchResultEditionWorks(){
        loginUser("pedro@emeal.com","pedroga4");

        WebElement optionsMenu = driver.findElement(By.className("user-info"));
        optionsMenu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my-matches-btn")));
        WebElement myMatchesBtn = driver.findElement(By.className("my-matches-btn"));
        scrollIntoView(myMatchesBtn);
        myMatchesBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-my-matches")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-match")));
        WebElement matchWithResult = driver.findElement(By.id("match-card7"));
        WebElement editResultBtn = matchWithResult.findElement(By.className("edit-result-btn"));
        scrollIntoView(editResultBtn);
        editResultBtn.click();

        WebElement resultDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-match-result-dialog")));
        List<WebElement> setInputs = resultDialog.findElements(By.className("set-input"));
        assertEquals(4, setInputs.size());

        // TEAM A
        setInputs.get(0).clear();
        setInputs.get(0).sendKeys("6");
        setInputs.get(1).clear();
        setInputs.get(1).sendKeys("6");

        // TEAM B
        setInputs.get(2).clear();
        setInputs.get(2).sendKeys("4");
        setInputs.get(3).clear();
        setInputs.get(3).sendKeys("4");

        WebElement saveBtn = resultDialog.findElement(By.className("confirm-btn"));
        saveBtn.click();

        wait.until(ExpectedConditions.invisibilityOf(resultDialog));

        waitForPageReload();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("match-card7")));
        matchWithResult = driver.findElement(By.id("match-card7"));

        WebElement viewResultBtn = wait.until(
            ExpectedConditions.elementToBeClickable(
                matchWithResult.findElement(By.className("view-result-btn"))
            )
        );
        viewResultBtn.click();

        WebElement viewDialog = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("app-match-result-dialog")
            )
        );

        List<WebElement> setTexts = viewDialog.findElements(By.className("set-text"));
        assertEquals(4, setTexts.size());

        assertEquals("6", setTexts.get(0).getText());
        assertEquals("6", setTexts.get(1).getText());


        assertEquals("4", setTexts.get(2).getText());
        assertEquals("4", setTexts.get(3).getText());

        WebElement cancelBtn = viewDialog.findElement(By.xpath(".//button[.//span[contains(text(),'Cancelar')]]"));
        cancelBtn.click();

        wait.until(ExpectedConditions.invisibilityOf(viewDialog));
    }

}
