package es.codeurjc.backend.e2e.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

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
class AdminUserAngularTest extends BaseAngularUITest {
    @Test
    @Order(1) 
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
    @Order(2)
    void verifyUserCreationAsAdminWorks() {
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
    @Order(3)
    void verifyUserEditionAsAdminWorks() {
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
    @Order(4)
    void verifyUserDeletionAsAdminWorks() {
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

    /* @Test
    @Order(5)
    void verifyClubCreationWorks() {
        loginUser("admin@emeal.com", "admin");

        goToAdminClubsPage();

        waitForAngularToFinish();

        waitForTableReady();
        List<WebElement> initialRows = driver.findElements(By.cssSelector("table.admin-table tr.mat-mdc-row")); 
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

        WebElement oldElement = submitBtn;
        wait.until(ExpectedConditions.stalenessOf(oldElement));

        wait.until(ExpectedConditions.urlContains("/admin/clubs"));

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount + 1));
    } */

    @Test
    @Order(6)
    void verifyClubEditionAsAdminWorks() {
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
    @Order(7)
    void verifyClubDeletionAsAdminWorks() {
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
    @Order(8)
    void verifyMatchCreationAsAdminWorks() {

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
    @Order(9)
    void verifyMatchEditionAsAdminWorks() {

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
    @Order(10)
    void verifyMatchDeletionAsAdminWorks() {

        loginUser("admin@emeal.com", "admin");

        goToAdminMatchesPage();

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

        waitForTableReady();

        List<WebElement> finalRows = driver.findElements(
                By.cssSelector("table.admin-table tr.mat-mdc-row")
        );

        assertThat(finalRows.size(), is(initialCount - 1));
    }

    @Test
    @Order(11)
    void verifySportCreationAsAdminWorks() {

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
    @Order(12)
    void verifySportEditionAsAdminWorks() {

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
    @Order(13)
    void verifySportDeletionAsAdminWorks() {

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
        WebElement matchesBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerlink='/admin/matches']")
                )
        );
        matchesBtn.click();

        waitForTableReady();
    }

    private void goToAdminSportsPage() {
        WebElement sportsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerlink='/admin/sports']")
                )
        );
        sportsBtn.click();

        waitForTableReady();
    }
}
