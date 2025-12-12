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
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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

        WebElement optionsMenu = wait.until(
            ExpectedConditions.elementToBeClickable(By.className("user-info"))
        );
        scrollIntoView(optionsMenu);
        optionsMenu.click();

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
    @Order(7)
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

    @Test
    @Order(10) 
    public void verifyAdminPageLoads(){
        loginUser("admin@emeal.com", "admin");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-container")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar")));

        String[] menuItems = {
                "/admin/users",
                "/admin/matches",
                "/admin/clubs",
                "/admin/sports"
        };

        for (String route : menuItems) {
            WebElement item = driver.findElement(By.cssSelector("a[routerlink='" + route + "']"));
            assertThat(item.isDisplayed(), is(true));
        }

        WebElement header = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-header h2"))
        );
        assertThat(header.getText(), is("Admin > Usuarios"));

        WebElement table = driver.findElement(By.cssSelector("table.admin-table"));
        assertThat(table.isDisplayed(), is(true));

        List<WebElement> headers = driver.findElements(By.cssSelector("table.admin-table th"));

        String[] expectedColumns = {
                "ID", "Nombre", "Username", "Sexo", "Email", "Nivel", "Descripción", "Acciones"
        };
        for (String column : expectedColumns) {
            boolean match = headers.stream().anyMatch(h -> h.getText().trim().equals(column));
            assertThat(match, is(true));
        }

        WebElement paginator = driver.findElement(By.cssSelector("mat-paginator"));
        assertThat(paginator.isDisplayed(), is(true));

    }

    @Test
    @Order(11)
    void verifyUserCreationWorks() {
        loginUser("admin@emeal.com", "admin");

        waitForTableReady();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement btnCreate = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-entity-btn")));
        scrollIntoView(btnCreate);
        btnCreate.click();

        waitForPageReload();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertThat(title.getText(), containsString("Crear Usuario"));

        WebElement realnameInput = driver.findElement(By.cssSelector("input[formcontrolname='realname']"));
        realnameInput.sendKeys("Alfredo Gomez");

        WebElement usernameInput = driver.findElement(By.cssSelector("input[formcontrolname='username']"));
        usernameInput.sendKeys("alfredo666");

        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        emailInput.sendKeys("alfredo@emeal.com");

        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys("alfredo66");

        WebElement dateInput = driver.findElement(By.cssSelector("input[formcontrolname='birthDate']"));
        dateInput.sendKeys("06/06/1966");

        WebElement genderSelector = driver.findElement(By.cssSelector("mat-select[formcontrolname='gender']"));
        genderSelector.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click();

        WebElement descriptionInput = driver.findElement(By.cssSelector("textarea[formcontrolname='description']"));
        descriptionInput.sendKeys("Usuario creado por Selenium");

        WebElement levelInput = driver.findElement(By.cssSelector("input[formcontrolname='level']"));
        levelInput.sendKeys("6.66");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount + 1));
    }

    @Test
    @Order(12)
    void verifyUserEditionWorks() {
        loginUser("admin@emeal.com", "admin");

         WebElement firstRow = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("table.admin-table tr.mat-mdc-row")
                )
        );

        List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));
        String originalName = cells.get(1).getText();
        assertThat(originalName, is("Admin"));

        WebElement editBtn = firstRow.findElement(By.cssSelector("button .edit-icon"));
        scrollIntoView(editBtn);
        editBtn.click();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertThat(title.getText(), containsString("Editar Usuario"));

        WebElement nameInput = driver.findElement(By.cssSelector("input[formcontrolname='realname']"));
        nameInput.clear();
        String name = "Julio Gomez";
        nameInput.sendKeys(name);

        WebElement saveBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(saveBtn);
        saveBtn.click();

        driver.findElement(By.cssSelector("button.create-btn[type='submit']")).click();

        waitForTableReady();


        WebElement updatedRow = driver.findElement(By.xpath(
                "//td[contains(text(),'"+ name +"')]"
        ));

        assertThat(updatedRow.isDisplayed(), is(true));
    }

    @Test
    @Order(13)
    void verifyUserDeletionWorks() {
        loginUser("admin@emeal.com", "admin");

        List<WebElement> initialRows = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("table.admin-table tr.mat-mdc-row"), 0)
        );
        int initialCount = initialRows.size();

        WebElement deleteBtn = initialRows.get(1).findElement(By.cssSelector("button .delete-icon"));
        scrollIntoView(deleteBtn);
        deleteBtn.click();

        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(), 'Eliminar cuenta')]]")
        ));
        WebElement element = deleteBtn;
        scrollIntoView(deleteBtn);
        confirmBtn.click();

        wait.until(ExpectedConditions.stalenessOf(element));

        waitForPageReload();

        List<WebElement> finalRows = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("table.admin-table tr.mat-mdc-row"), 0)
        );
        int finalCount = initialCount - 1;
        assertThat(finalRows.size(), is(finalCount));
    }

    @Test
    @Order(14)
    void verifyClubCreationWorks() {
        loginUser("admin@emeal.com", "admin");

        goToAdminClubsPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement newClubBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".new-entity-btn")
                )
        );
        scrollIntoView(newClubBtn);
        newClubBtn.click();

        waitForPageReload();

        WebElement nameInput = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        nameInput.sendKeys("Club Selenium Test");
        WebElement phoneInput = driver.findElement(By.cssSelector("input[formcontrolname='phone']"));
        phoneInput.sendKeys("666777888");
        WebElement emailInput = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        emailInput.sendKeys("club@selenium.com");
        WebElement webInput = driver.findElement(By.cssSelector("input[formcontrolname='web']"));
        webInput.sendKeys("www.club-selenium.com");
        WebElement addressInput = driver.findElement(By.cssSelector("input[formcontrolname='address']"));
        addressInput.sendKeys("Calle Test 123");
        WebElement cityInput = driver.findElement(By.cssSelector("input[formcontrolname='city']"));
        cityInput.sendKeys("Madrid");
        WebElement openingTimeInput = driver.findElement(By.cssSelector("input[formcontrolname='openingTime']"));
        openingTimeInput.sendKeys("08:00");
        WebElement closingTimeInput = driver.findElement(By.cssSelector("input[formcontrolname='closingTime']"));
        closingTimeInput.sendKeys("23:00");
        WebElement minPriceInput = driver.findElement(By.cssSelector("input[formcontrolname='minPrice']"));
        minPriceInput.sendKeys("5");
        WebElement maxPriceInput = driver.findElement(By.cssSelector("input[formcontrolname='maxPrice']"));
        maxPriceInput.sendKeys("20");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount + 1));
    }

    @Test
    @Order(15)
    void verifyClubEditionWorks() {
        loginUser("admin@emeal.com", "admin");

        goToAdminClubsPage();

        waitForAngularToFinish();

        WebElement firstRow = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("table.admin-table tr.mat-mdc-row")
                )
        );

        List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));
        String originalName = cells.get(1).getText();
        assertThat(originalName, is("Tennis Club Elite"));

        WebElement editBtn = firstRow.findElement(By.cssSelector("button .edit-icon"));
        scrollIntoView(editBtn);
        editBtn.click();

        waitForPageReload();

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[formcontrolname='name']")
                )
        );
        nameInput.clear();
        String name = "Club Selenium Prueba";
        nameInput.sendKeys(name);

        driver.findElement(By.cssSelector("button.create-btn[type='submit']")).click();

        waitForTableReady();


        WebElement updatedRow = driver.findElement(By.xpath(
                "//td[contains(text(),'"+ name +"')]"
        ));

        assertThat(updatedRow.isDisplayed(), is(true));
    }

    @Test
    @Order(16)
    void verifyClubDeletionWorks() {
        loginUser("admin@emeal.com", "admin");

        goToAdminClubsPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement deleteBtn = initialRows.getLast().findElement(By.cssSelector("button .delete-icon"));
        scrollIntoView(deleteBtn);
        deleteBtn.click();

        WebElement oldElement = deleteBtn;

        WebElement confirmBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(text(),'Eliminar cuenta')] or contains(.,'Eliminar')]")
                )
        );
        scrollIntoView(confirmBtn);
        confirmBtn.click();

        wait.until(ExpectedConditions.stalenessOf(oldElement));

        waitForPageReload();

        List<WebElement> finalRows = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("table.admin-table tr.mat-mdc-row"), 0)
        );

        assertThat(finalRows.size(), is(initialCount - 1));
    }

    @Test
    @Order(17)
    void verifyMatchCreationWorks() {

        loginUser("admin@emeal.com", "admin");
        
        goToAdminMatchesPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement btnCreate = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".new-entity-btn")
                )
        );
        scrollIntoView(btnCreate);
        btnCreate.click();

        waitForPageReload();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertThat(title.getText(), containsString("Crear Partido"));

        WebElement clubSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='club']"));
        clubSelect.click();
        WebElement firstClub = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option"))
        );
        firstClub.click();

        WebElement sportSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='sport']"));
        sportSelect.click();
        WebElement firstSport = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option"))
        );
        firstSport.click();

        WebElement modeSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='mode']"));
        wait.until(ExpectedConditions.elementToBeClickable(modeSelect));

        modeSelect.click();
        WebElement firstMode = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option"))
        );
        firstMode.click();

        driver.findElement(By.cssSelector("input[formcontrolname='price']")).sendKeys("12.50");

        WebElement dateInput = driver.findElement(By.cssSelector("input[formcontrolname='date']"));
        dateInput.sendKeys("04/12/2025");

        WebElement timeInput = driver.findElement(By.cssSelector("input[formcontrolname='time']"));
        timeInput.sendKeys("18:30");

        WebElement typeSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='type']"));
        typeSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click();

        WebElement privacySelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='isPrivate']"));
        privacySelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(1).click();


        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount + 1));
    }

    @Test
    @Order(18)
    void verifyMatchEditionWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminMatchesPage();

        waitForAngularToFinish();

        List<WebElement> rows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        assertThat(rows.size(), greaterThan(0));

        WebElement firstRow = rows.get(0);
        List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));

        String idToEdit = cells.get(0).getText();

        WebElement editBtn = firstRow.findElement(By.cssSelector(".edit-icon"));
        scrollIntoView(editBtn);
        editBtn.click();

        waitForPageReload();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertThat(title.getText(), containsString("Editar Partido"));

        WebElement priceInput = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        priceInput.clear();
        priceInput.sendKeys("99.99");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();

        waitForTableReady();

        WebElement updatedRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//td[contains(text(),'" + idToEdit + "')]/parent::tr")
                )
        );

        String updatedPrice = updatedRow.findElements(By.cssSelector("td")).get(5).getText();
        assertThat(updatedPrice, containsString("99,99"));
    }

    @Test
    @Order(19)
    void verifyMatchDeletionWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminMatchesPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement rowToDelete = initialRows.getLast();
        WebElement deleteBtn = rowToDelete.findElement(By.cssSelector("button .delete-icon"));

        WebElement oldElement = deleteBtn;
        scrollIntoView(deleteBtn);
        deleteBtn.click();

        WebElement confirmBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(text(),'Eliminar cuenta')] or contains(.,'Eliminar')]")
                )
        );
        scrollIntoView(confirmBtn);
        confirmBtn.click();

        wait.until(ExpectedConditions.stalenessOf(oldElement));

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount - 1));
    }

    @Test
    @Order(20)
    void verifySportCreationWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminSportsPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement btnCreate = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".new-entity-btn")
                )
        );
        scrollIntoView(btnCreate);
        btnCreate.click();

        waitForPageReload();

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2"))
        );
        assertThat(title.getText(), containsString("Crear Deporte"));

        WebElement nameInput = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        nameInput.sendKeys("SeleniumBall");

        WebElement scoringSelect = driver.findElement(By.cssSelector("mat-select[formcontrolname='scoringType']"));
        scoringSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElements(By.cssSelector("mat-option")).get(0).click(); 

        List<WebElement> modeRows = driver.findElements(By.cssSelector(".mode-row"));
        WebElement createdMode = modeRows.get(0);

        createdMode.findElement(By.cssSelector("input[formcontrolname='name']"))
                .sendKeys("Modo Selenium");

        createdMode.findElement(By.cssSelector("input[formcontrolname='playersPerGame']"))
                .sendKeys("4");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        assertThat(finalRows.size(), is(initialCount + 1));
    }

    @Test
    @Order(21)
    void verifySportEditWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminSportsPage();

        waitForAngularToFinish();

        List<WebElement> rows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        assertThat(rows.size(), greaterThan(0));
        
        WebElement firstRow = rows.get(0);
        WebElement editBtn = firstRow.findElement(By.cssSelector(".edit-icon"));
        scrollIntoView(editBtn);
        editBtn.click();

        waitForPageReload();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertThat(title.getText(), containsString("Editar Deporte"));

        WebElement nameInput = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        nameInput.clear();
        nameInput.sendKeys("EditedSport");

        WebElement addModeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//mat-icon[text()='add']]")
        ));
        scrollIntoView(addModeBtn);
        addModeBtn.click();

        List<WebElement> modes = driver.findElements(By.cssSelector(".mode-row"));
        WebElement newMode = modes.get(modes.size() - 1);

        WebElement newModeNameInput = newMode.findElement(By.cssSelector("input[formcontrolname='name']"));
        newModeNameInput.sendKeys("Modo Extra");

        WebElement newModePlayersInput = newMode.findElement(By.cssSelector("input[formcontrolname='playersPerGame']"));
        newModePlayersInput.sendKeys("8");
        
        
        WebElement submitBtn = driver.findElement(By.cssSelector("button.create-btn[type='submit']"));
        scrollIntoView(submitBtn);
        submitBtn.click();


        waitForTableReady();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'EditedSport')]")
        ));
    }

    @Test
    @Order(22)
    void verifySportDeletionWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminSportsPage();

        waitForAngularToFinish();

        List<WebElement> initialRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );
        int initialCount = initialRows.size();

        WebElement rowToDelete = initialRows.getLast();
        WebElement deleteBtn = rowToDelete.findElement(By.cssSelector("button .delete-icon"));

        WebElement oldElement = deleteBtn;

        deleteBtn.click();

        WebElement confirmBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[contains(text(),'Eliminar cuenta')] or contains(.,'Eliminar')]")
                )
        );
        confirmBtn.click();

        wait.until(ExpectedConditions.stalenessOf(oldElement));

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount - 1));
    }



    private void loginUser(String email, String password) {
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

    private void waitForPageReload() {
        wait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
        waitForAngularToFinish();
    }

    private void waitForTableReady() {
        waitForPageReload();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("table.admin-table tr.mat-mdc-row"), 0
        ));
        waitForAngularToFinish();
    }
    private void goToAdminClubsPage() {
        WebElement clubsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerlink='/admin/clubs']")
                )
        );
        clubsBtn.click();

        waitForTableReady();
    }

    private void goToAdminMatchesPage() {
        WebElement clubsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerlink='/admin/matches']")
                )
        );
        clubsBtn.click();

        waitForTableReady();
    }

    private void goToAdminSportsPage() {
        WebElement clubsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerlink='/admin/sports']")
                )
        );
        clubsBtn.click();

        waitForTableReady();
    }

    private void waitForAngularToFinish() {
        try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                        webDriver -> ((JavascriptExecutor) webDriver)
                                .executeScript("return window.getAllAngularTestabilities && "
                                        + "window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1")
                                .equals(true)
                );
        } catch (Exception ignored) {}
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

}
