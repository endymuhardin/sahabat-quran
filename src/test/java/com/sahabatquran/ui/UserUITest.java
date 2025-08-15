package com.sahabatquran.ui;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUITest extends BaseUITest {

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/users.csv", numLinesToSkip = 1)
    public void testAddUser(String username, String fullname, String email) {
        driver.get(getBaseUrl() + "/admin/users");

        driver.findElement(By.linkText("Add User")).click();

        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("fullname")).sendKeys(fullname);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("save-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("users-table")));
        assertTrue(table.getText().contains(username));
    }
}
