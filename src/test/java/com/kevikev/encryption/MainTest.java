package com.kevikev.encryption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevikev.encryption.aes.AESEncryption;
import com.kevikev.encryption.util.ConsoleUtilsTest;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    ConsoleUtilsTest.ConsoleUtilsHelper console;

    @BeforeEach
    void setUp() {
        Main.console = console = new ConsoleUtilsTest.ConsoleUtilsHelper();
    }

    @Test
    void mainEncrypt() {
        Main.main(List.of("encrypt", "test", "test123").toArray(new String[0]));

        assertNull(console.exitCode);
        assertEquals(Level.INFO, console.logLevel);
        assertNotNull(console.message);

        assertEquals("test", AESEncryption.decrypt(console.message, "test123"));
    }

    @Test
    @SetEnvironmentVariable(key = "method", value = "encrypt")
    @SetEnvironmentVariable(key = "data", value = "test")
    @SetEnvironmentVariable(key = "passphrase", value = "test123")
    void mainEncryptEnv() {
        Main.main(List.of().toArray(new String[0]));

        assertNull(console.exitCode);
        assertEquals(Level.INFO, console.logLevel);
        assertNotNull(console.message);

        assertEquals("test", AESEncryption.decrypt(console.message, "test123"));
    }

    @Test
    @SetEnvironmentVariable(key = "method", value = "encrypt")
    @SetEnvironmentVariable(key = "data", value = "test")
    @SetEnvironmentVariable(key = "passphrase", value = "test123")
    @SetEnvironmentVariable(key = "numIterations", value = "1")
    @SetEnvironmentVariable(key = "keyLength", value = "128")
    @SetEnvironmentVariable(key = "saltLength", value = "12")
    void mainEncryptEnvAndOptionalArgs() throws IOException {
        Main.main(List.of().toArray(new String[0]));

        assertNull(console.exitCode);
        assertEquals(Level.INFO, console.logLevel);
        assertNotNull(console.message);

        assertEquals("test", AESEncryption.decrypt(console.message, "test123"));

        Map<String, String> payload = new ObjectMapper().readValue(Base64.getDecoder().decode(console.message), new TypeReference<>(){});
        assertEquals("1", payload.get("iterations"));
        assertEquals("128", payload.get("keyLength"));
        assertEquals(12, Base64.getDecoder().decode(payload.get("salt")).length);
        assertEquals(16, Base64.getDecoder().decode(payload.get("iv")).length);
    }

    @Test
    void mainDecrypt() {
        String encryptedData = AESEncryption.encrypt("test", "test123");
        Main.main(List.of("decrypt", encryptedData, "test123").toArray(new String[0]));

        assertNull(console.exitCode);
        assertEquals(Level.INFO, console.logLevel);
        assertEquals("test", console.message);
    }

    @Test
    void mainInvalidMethod() {
        Main.main(List.of("testMethod", "test", "test123").toArray(new String[0]));

        assertEquals(101, console.exitCode);
        assertEquals(Level.ERROR, console.logLevel);
        assertEquals("Method not supported: testMethod\n" + Main.USAGE, console.message);
    }

    @Test
    void mainMissingArguments() {
        Main.main(List.of("encrypt", "test").toArray(new String[0]));

        assertEquals(3, console.exitCode);
        assertEquals(Level.ERROR, console.logLevel);
        assertEquals(Main.USAGE, console.message);
    }

    @Test
    void mainInvalidNumeric() {
        Main.main(List.of("encrypt", "test", "test123", "test456").toArray(new String[0]));

        assertEquals(204, console.exitCode);
        assertEquals(Level.ERROR, console.logLevel);
        assertEquals("Invalid arg. Must be numeric. Data: \"test456\". Arg: \"numIterations\"\n"+Main.USAGE, console.message);
    }

    @Test
    void mainEmptyNumeric() {
        Main.main(List.of("encrypt", "test", "test123", " ").toArray(new String[0]));

        assertNull(console.exitCode);
        assertEquals(Level.INFO, console.logLevel);
        assertNotNull(console.message);

        assertEquals("test", AESEncryption.decrypt(console.message, "test123"));
    }
}
