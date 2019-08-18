
package com.mycompany.testonlinejavacompiler;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;

import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class TestUI {

    // some UI elements used
    private WebElement compileButton, executeButton, saveButton, code, edit;
    // web driver wait
    private WebDriverWait wait;

    /**
     * initialize UI element references
     */
    @BeforeClass
    public void beforeClass() {

        compileButton = Util.driver.findElement(By.id("compile"));

        executeButton = Util.driver.findElement(By.id("execute"));

        code = Util.driver.findElement(
                By.id("/home/cg/root/HelloWorld.java")).findElement(
                By.xpath(".//div[2]/div[1]"));

        edit = Util.driver.findElement(By.id("edit"));

        saveButton = Util.driver.findElement(By.className("icon-save-project"));

        wait = new WebDriverWait(Util.driver, 5);
    }

    /**
     * clear the terminal (not always necessary)
     * @param m test method
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method m) {

        // no need to clean terminal when using an alternative one
        if (m.getName().equals("testAlternativeTerminal")) { return; }

        Util.terminal.click();
        Util.clear();

        // a little pause between the cases (not necessary)
        try { Thread.sleep(300); } catch (InterruptedException e) {}
    }

    // use priorities to keep the order of test runs.
    // not necessary, but more convenient

    /**
     * test compile and run using "compile" and "execute" buttons
     */
    @Test(priority = 1, enabled = true)
    public void testCompileAndRun() {

        compileButton.click();
        executeButton.click();
        Util.execInTerminal("rm *.class");
        Util.waitForTerminalText("Hello World");
    }


    /**
     * test "refresh" functionality
     */
    @Test(priority = 2, enabled = true)
    public void testRefreshFiles() {

        compileButton.click();
        Util.terminal.click();
        Util.execInTerminal("touch tmp.txt");
        Util.execInTerminal("javac HelloWorld.java");
        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.class");
        WebElement refreshButton =
                Util.driver.findElement(By.className("icon-refresh-project"));
        refreshButton.click();
        WebElement home = Util.driver.findElement(By.id("home"));
        wait.until(ExpectedConditions.textToBePresentInElement(home, "tmp.txt"));
        wait.until(ExpectedConditions.textToBePresentInElement(home, "HelloWorld.class"));
        // clear
        Util.terminal.click();
        Util.execInTerminal("rm *.class tmp.txt");
        Util.checkZeroStatus(); // just to make some delay
        refreshButton.click();
        wait.until(
            ExpectedConditions.textToBePresentInElement(home, "HelloWorld.java"));
        Assert.assertFalse(home.getText().contains("tmp.txt") ||
                home.getText().contains("HelloWorld.class"));
    }

    /**
     * test "find and replace" functionality
     */
    @Test(priority = 3, enabled = true)
    public void testFindReplace() {

        edit.click();
        WebElement replace = Util.driver.findElement(By.id("findreplace"));
        wait.until(ExpectedConditions.visibilityOf(replace));
        replace.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ace_search")));

        WebElement search = Util.driver.findElement(By.className("ace_search"));
        WebElement text1 = search.findElement(By.xpath(".//div[1]/input[1]"));
        WebElement text2 = search.findElement(By.xpath(".//div[2]/input[1]"));
        text1.sendKeys("Hello World");
        text2.sendKeys("Hello User");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//div[2]/button[1]")));
        search.findElement(By.xpath(".//div[2]/button[1]")).click();

        saveButton.click();
        compileButton.click();
        executeButton.click();
        Util.waitForTerminalText("Hello User");
    }

    /**
     * test "undo/redo" functionality
     */
    @Test(priority = 4, enabled = true)
    public void testUndoRedo() {

        code.click();
        code.sendKeys(Keys.PAGE_DOWN);
        code.sendKeys(Keys.ENTER);
        CharSequence comment = "// some comment";
        code.sendKeys(comment);
        edit.click();
        WebElement undo = Util.driver.findElement(By.id("undo"));
        wait.until(ExpectedConditions.visibilityOf(undo));
        undo.click();

        code.click();
        Assert.assertFalse(code.getText().contains(comment),
                "undo does not work");

        edit.click();
        WebElement redo = Util.driver.findElement(By.id("redo"));
        wait.until(ExpectedConditions.visibilityOf(redo));
        redo.click();
        wait.until(ExpectedConditions.textToBePresentInElement(
                code, comment.toString()));
    }

    /**
     * test "copy/paste" functionality
     */
    @Test(priority = 5, enabled = true)
    public void testCopyPaste() {

        edit.click();
        WebElement find = Util.driver.findElement(By.id("find"));
        wait.until(ExpectedConditions.visibilityOf(find));
        find.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ace_search")));

        WebElement search = Util.driver.findElement(By.className("ace_search"));
        WebElement text = search.findElement(By.xpath(".//div[1]/input[1]"));
        text.sendKeys("Hello");
        text.sendKeys(Keys.ENTER);

        edit.click();
        WebElement copy = Util.driver.findElement(By.id("copy"));
        wait.until(ExpectedConditions.visibilityOf(copy));
        copy.click();

        edit.click();
        WebElement paste = Util.driver.findElement(By.id("paste"));
        wait.until(ExpectedConditions.visibilityOf(paste));
        paste.click();

        saveButton.click();

        compileButton.click();
        executeButton.click();
        Util.waitForTerminalText("HelloHello");
    }

    /**
     * test "select all" functionality
     */
    @Test(priority = 6, enabled = true)
    public void testSelectAll() {

        edit.click();
        WebElement selectAll = Util.driver.findElement(By.id("select"));
        wait.until(ExpectedConditions.visibilityOf(selectAll));
        selectAll.click();

        edit.click();
        WebElement cut = Util.driver.findElement(By.id("cut"));
        wait.until(ExpectedConditions.visibilityOf(cut));
        cut.click();

        code.click();
        Assert.assertTrue(code.getText().equals(""), "select all does not work");

        edit.click();
        WebElement undo = Util.driver.findElement(By.id("undo"));
        wait.until(ExpectedConditions.visibilityOf(undo));
        undo.click();
    }

    /**
     * test alternative terminal usage (move to TestTerminal?).
     * run this test in the end!
     */
    @Test(priority = 7, enabled = true)
    public void testAlternativeTerminal() {

        WebElement terms = Util.driver.findElement(By.id("terminals"));
        WebElement termBtn = terms.findElement(
                By.xpath(".//div[1]/div[3]/ul[1]/li[2]"));
        termBtn.click();
        WebElement term1 = Util.driver.findElement(By.id("terminal1"));
        wait.until(ExpectedConditions.visibilityOf(term1));

        term1.click();
        term1.sendKeys("javac HelloWorld.java");
        term1.sendKeys(Keys.ENTER);
        term1.sendKeys("java HelloWorld");
        term1.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.textToBePresentInElement(term1, "Hello "));

        term1.sendKeys("java HelloWorld");
        term1.sendKeys(Keys.ENTER);
    }
}
