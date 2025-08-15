package com.sahabatquran.ui;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PermissionUITest extends BaseUITest {

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/permissions.csv", numLinesToSkip = 1)
    public void testAddPermission(String name) {
        driver.get(getBaseUrl() + "/admin/permissions");

        driver.findElement(By.linkText("Add Permission")).click();

        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("save-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("permissions-table")));
        assertTrue(table.getText().contains(name));
    }
}
