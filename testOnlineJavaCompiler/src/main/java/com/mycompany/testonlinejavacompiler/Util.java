
package com.mycompany.testonlinejavacompiler;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

// to run the suites in netbeans please set the following "run" settings:
// main class = org.testng.TestNG
// argument = src/main/java/testng.xml

public class Util {

    /**
     * the Web resource URL
     */
    private static final String URL =
            "https://www.tutorialspoint.com/compile_java_online.php";


    /**
     * the driver
     */
    public static WebDriver driver;
    /**
     * the IDE terminal
     */
    public static WebElement terminal;

    public static void execInTerminal(CharSequence command) {

        //terminal.click();
        terminal.sendKeys(command);
        terminal.sendKeys(Keys.ENTER);
    }

    /**
     * clear the terminal
     */
    public static void clear() { execInTerminal("clear"); }

    /**
     * wait until the text specified appears in the terminal
     * @param text the expected text
     */
    public static void waitForTerminalText(String text) {

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.textToBePresentInElement(terminal, text));
    }

    /**
     * check if the terminal command execution was successful
     */
    public static void checkZeroStatus() {

        execInTerminal("echo \"status = $?\"");
        waitForTerminalText("status = 0");
    }


    /**
     * set up the driver
     */
    @BeforeSuite
    public void init() {

        System.out.println("setting up driver");
        
        // platform-specific (Linux), please remove if unnecessary
        System.setProperty("webdriver.gecko.driver",
            "/home/user/install/gecko/geckodriver");
        driver = new FirefoxDriver();
    }

    /**
     * open the URL, wait until the IDE elements are visible
     */
    @Test
    public void openOnlineIDE() {

        driver.navigate().to(URL);
        Wait<WebDriver> wait = new WebDriverWait(driver, 5, 1000);
        terminal = driver.findElement(By.id("terminal0"));
        wait.until(ExpectedConditions.visibilityOf(terminal));
    }

    /**
     * quit after all the tests were executed
     */
    @AfterSuite
    public void quit() {
        System.out.println("quitting driver");
        driver.quit();
    }
}
