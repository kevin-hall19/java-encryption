package com.kevikev.encryption.util;


import com.kevikev.encryption.Main;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsoleUtils {
    private static final Logger logger = LogManager.getLogger();

    public void exit(int exitCode) {
        exit(exitCode, Main.USAGE);
    }

    public void exit(int exitCode, String... message) {
        for (String msg : message) {
            printLn(msg, exitCode);
        }
        systemExit(exitCode);
    }

    void systemExit(int exitCode) {
        System.exit(exitCode);
    }

    void printLn(String msg, int exitCode) {
        printLn(msg, exitCode == 0 ? Level.INFO : Level.ERROR);
    }

    public void printLn(String msg, Level level) {
        logger.log(level, msg);
    }

    public void printLn(String msg) {
        printLn(msg, Level.INFO);
    }
}
