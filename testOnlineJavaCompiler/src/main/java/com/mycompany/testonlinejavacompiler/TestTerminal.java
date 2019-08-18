
package com.mycompany.testonlinejavacompiler;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class TestTerminal {

    /**
     * set focus on the terminal
     */
    @BeforeClass
    public void beforeClass() {

        Util.terminal.click();
    }

    /**
     * clear the terminal
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {

        Util.clear();

        // a little pause between the cases (not necessary)
        try { Thread.sleep(300); } catch (InterruptedException e) {}
    }


    // use priorities to keep the order of test runs.
    // not necessary, but more convenient

    /**
     * test if "java -version" works as expected
     */
    @Test(priority = 1, enabled = true)
    public void testVersion() {

        Util.execInTerminal("java -version");
        Util.waitForTerminalText("openjdk");
        Util.checkZeroStatus();
    }

    /**
     * test command line compile
     */
    @Test(priority = 2, enabled = true)
    public void testCompile() {

        Util.execInTerminal("rm -f *.class"); // make sure no class files
                                             // in the current dir
        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.java"); // make sure sources are here
        Util.execInTerminal("javac HelloWorld.java");
        Util.checkZeroStatus();
        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.class");
        Util.execInTerminal("rm *.class");
    }

    /**
     * test command line run
     */
    @Test(priority = 3, enabled = true)
    public void testCompileAndRun() {

        Util.execInTerminal("rm -f *.class");
        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.java"); // make sure sources are here
        Util.execInTerminal("javac HelloWorld.java");
        Util.checkZeroStatus();
        Util.execInTerminal("java HelloWorld");
        Util.waitForTerminalText("Hello World");
        Util.checkZeroStatus();
        // clear
        Util.execInTerminal("rm *.class");
    }

    /**
     * test if an error message appears when trying to compile an absent file
     */
    @Test(priority = 4, enabled = true)
    public void testCompileWrongFile() {

        Util.execInTerminal("javac DummyClass.java");
        Util.waitForTerminalText("file not found");
    }

    /**
     * test if an error message appears when using a wrong class name
     */
    @Test(priority = 5, enabled = true)
    public void testCompileWrongClassName() {

        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.java"); // make sure sources are here
        Util.execInTerminal("cp HelloWorld.java Dummy.java");
        Util.execInTerminal("javac Dummy.java");
        Util.execInTerminal("rm Dummy.java");
        Util.waitForTerminalText(
                "should be declared in a file named HelloWorld.java");
    }

    /**
     * test if an error message appears when trying to compile an invalid file
     */
    @Test(priority = 6, enabled = true)
    public void testCompileInvalidFile() {

        Util.execInTerminal("echo \"text text text\" > Invalid.java");
        Util.execInTerminal("javac Invalid.java");
        Util.execInTerminal("rm Invalid.java");
        Util.waitForTerminalText(
                "class, interface, or enum expected");
    }

    /**
     * test if an error message appears when trying to run an invalid file
     */
    @Test(priority = 7, enabled = true)
    public void testRunWrongFile() {

        Util.execInTerminal("javac HelloWorld.java"); // may skip this
        Util.execInTerminal("java HellWorld");
        Util.waitForTerminalText("Could not find or load main class");
        Util.execInTerminal("rm *.class");
    }

    /**
     * test command line jar creation
     */
    @Test(priority = 8, enabled = true)
    public void testJar() {

        Util.execInTerminal("rm -f *.jar"); // just in case
        Util.execInTerminal("javac HelloWorld.java");
        Util.execInTerminal("ls");
        Util.waitForTerminalText("HelloWorld.class");
        Util.execInTerminal("jar cfe test.jar HelloWorld HelloWorld.class");
        Util.checkZeroStatus();
        Util.clear();
        Util.execInTerminal("ls");
        Util.waitForTerminalText("test.jar");
        Util.execInTerminal("java -jar test.jar");
        Util.waitForTerminalText("Hello World");
        // clear
        Util.execInTerminal("rm *.class");
        Util.execInTerminal("rm *.jar");
    }

    /**
     * test passing args
     */
    @Test(priority = 9, enabled = true)
    public void testArgs() {

        Util.execInTerminal(
                "echo \"public class Args { "
                + "public static void main(String args[]) { System.out.println("
                + "args.length + \\\"->\\\" + args[0] + args[1]); } }\" > Args.java");
        Util.execInTerminal("javac Args.java");
        Util.execInTerminal("java Args TEST 123");
        Util.waitForTerminalText("2->TEST123");
        // clear
        Util.execInTerminal("rm Args.*");
    }

    /**
     * test passing properties
     */
    @Test(priority = 10, enabled = true)
    public void testProp() {

        Util.execInTerminal("echo \"public class Prop { "
                + "public static void main(String args[]) { System.out.println("
                + "\\\"->\\\" + System.getProperty(\\\"testProp\\\")); } }\" > Prop.java");
        Util.execInTerminal("javac Prop.java");
        Util.execInTerminal("java -DtestProp=Test123 Prop");
        Util.execInTerminal("rm Prop.*");
        Util.waitForTerminalText("->Test123");
    }

    /**
     * test multi-source compilation ("javac *" command)
     */
    @Test(priority = 11, enabled = true)
    public void testJavacAll() {

        Util.execInTerminal("echo \"public class A { "
                + "public static String s() { return \\\"A\\\"; } }\" > A.java");
        Util.execInTerminal("echo \"public class B { "
                + "public static final String S = \\\"B\\\"; }\" > B.java");
        Util.execInTerminal("echo \"public class C { "
                + "public String s() { return \\\"C\\\"; } }\" > C.java");
        Util.execInTerminal("echo \"public class Main { "
                + "public static void main(String args[]) { System.out.println("
                + "\\\"->\\\" + A.s() + B.S + (new C()).s()); } }\" > Main.java");
        Util.execInTerminal("javac *.java");
        Util.checkZeroStatus();
        Util.execInTerminal("java Main");
        Util.waitForTerminalText("->ABC");
        // clear
        Util.execInTerminal("rm *.class");
        Util.execInTerminal("rm A.java B.java C.java Main.java");
        Util.clear();
    }

    /**
     * test javap
     */
    @Test(priority = 12, enabled = true)
    public void testJavap() {

        Util.execInTerminal("javac HelloWorld.java");
        Util.execInTerminal("javap HelloWorld.class");

        Util.waitForTerminalText("Compiled from");
        Util.waitForTerminalText("public class HelloWorld");
        // check the constructor and main are there
        Util.waitForTerminalText("public HelloWorld()");
        Util.waitForTerminalText("public static void main");
    }
}
