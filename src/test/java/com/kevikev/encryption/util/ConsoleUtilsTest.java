package com.kevikev.encryption.util;

import com.kevikev.encryption.Main;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ConsoleUtilsTest {
    ConsoleUtilsHelper consoleUtils = new ConsoleUtilsHelper();

    @BeforeEach
    void setUp() {
        consoleUtils = new ConsoleUtilsHelper();
    }

    @Test
    void printLn() {
        consoleUtils.printLn("test");
        assertEquals("test", consoleUtils.message);
        assertEquals(Level.INFO, consoleUtils.logLevel);
        assertNull(consoleUtils.exitCode);
    }

    @Test
    void printLnWithLevel(){
        consoleUtils.printLn("test", Level.DEBUG);
        assertEquals("test", consoleUtils.message);
        assertEquals(Level.DEBUG, consoleUtils.logLevel);
        assertNull(consoleUtils.exitCode);
    }

    @Test
    void exitWithErrorCode() {
        consoleUtils.exit(100);
        assertEquals(Main.USAGE, consoleUtils.message);
        assertEquals(Level.ERROR, consoleUtils.logLevel);
        assertEquals(100, consoleUtils.exitCode);
    }

    @Test
    void exitWithMessage() {
        consoleUtils.exit(0, "test");
        assertEquals("test", consoleUtils.message);
        assertEquals(Level.INFO, consoleUtils.logLevel);
        assertEquals(0, consoleUtils.exitCode);
    }

    @Test
    void exitWithMessages() {
        consoleUtils.exit(0, "test1", "test2");
        assertEquals("test1\ntest2", consoleUtils.message);
        assertEquals(Level.INFO, consoleUtils.logLevel);
        assertEquals(0, consoleUtils.exitCode);
    }

    @Test
    void exitWithMessageAndErrorCode() {
        consoleUtils.exit(100, "test");
        assertEquals("test", consoleUtils.message);
        assertEquals(Level.ERROR, consoleUtils.logLevel);
        assertEquals(100, consoleUtils.exitCode);
    }

    public static class ConsoleUtilsHelper extends ConsoleUtils {
        public Integer exitCode;
        public String message;
        public Level logLevel;

        @Override
        protected void systemExit(int exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        public void printLn(String msg, Level level) {
            super.printLn(msg, level);
            appendMessage(msg);
            this.logLevel = level;
        }

        private void appendMessage(String msg) {
            if (message == null) {
                message = msg;
            } else {
                message += "\n" + msg;
            }
        }
    }
}
