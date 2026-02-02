package com.kevikev.encryption.aes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AESEncryptionTest {
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final String PASSPHRASE = "test passphrase";

    @Test
    void encryptBlank() {
        String encrypted = AESEncryption.encrypt("", PASSPHRASE);
        assertEquals("", encrypted);

        encrypted = AESEncryption.decrypt(null, PASSPHRASE);
        assertEquals("", encrypted);

        encrypted = AESEncryption.decrypt("test", "");
        assertEquals("", encrypted);

        encrypted = AESEncryption.decrypt("test", null);
        assertEquals("", encrypted);
    }

    @Test
    void decryptBlank() {
        String decrypted = AESEncryption.decrypt("", PASSPHRASE);
        assertEquals("", decrypted);

        decrypted = AESEncryption.decrypt(null, PASSPHRASE);
        assertEquals("", decrypted);

        decrypted = AESEncryption.decrypt("test", "");
        assertEquals("", decrypted);

        decrypted = AESEncryption.decrypt("test", null);
        assertEquals("", decrypted);
    }

    @Test
    void encrypt() throws JsonProcessingException {
        String toEncrypt = "test encrypt";
        String encrypted = AESEncryption.encrypt(toEncrypt, PASSPHRASE);
        assertEncrypted(toEncrypt, encrypted, 256, 100000, 16);

        String encrypted2 = AESEncryption.encrypt(toEncrypt, PASSPHRASE);
        assertNotEquals(encrypted, encrypted2);
        assertEncrypted(toEncrypt, encrypted2, 256, 100000, 16);
    }

    @Test
    void encryptWithIterations() throws JsonProcessingException {
        String toEncrypt = "test encryptWithIterations";
        String encrypted = AESEncryption.encrypt(toEncrypt, PASSPHRASE, 1);
        assertEncrypted(toEncrypt, encrypted, 256, 1, 16);
    }

    @Test
    void encryptWithKeyLength() throws JsonProcessingException {
        String toEncrypt = "test encryptWithKeyLength";
        String encrypted = AESEncryption.encrypt(toEncrypt, PASSPHRASE, 1, 128);
        assertEncrypted(toEncrypt, encrypted, 128, 1, 16);

        String encrypted2 = AESEncryption.encrypt(toEncrypt, PASSPHRASE, 2, 192);
        assertEncrypted(toEncrypt, encrypted2, 192, 2, 16);

        String encrypted3 = AESEncryption.encrypt(toEncrypt, PASSPHRASE, 3, 256);
        assertEncrypted(toEncrypt, encrypted3, 256, 3, 16);
    }

    @Test
    void encryptWithSaltLength() throws JsonProcessingException {
        String toEncrypt = "test encryptWithSaltLength";
        String encrypted = AESEncryption.encrypt(toEncrypt, PASSPHRASE, 1, 128, 28);
        assertEncrypted(toEncrypt, encrypted, 128, 1, 28);
    }

    private void assertEncrypted(String initial, String encrypted, int keyLength, int iterations, int saltLength) throws JsonProcessingException {
        assertNotEquals(initial, encrypted);
        Map<String, Object> encryptedPayload = getPayload(encrypted);
        assertEquals(keyLength, encryptedPayload.get("keyLength"));
        assertEquals(iterations, encryptedPayload.get("iterations"));
        assertEquals(saltLength, DECODER.decode(encryptedPayload.get("salt").toString()).length);
        assertEquals(16, DECODER.decode(encryptedPayload.get("iv").toString()).length);

        String decrypted = AESEncryption.decrypt(encrypted, PASSPHRASE);
        assertEquals(initial, decrypted);
    }

    private Map<String, Object> getPayload(String encodedPayload) throws JsonProcessingException {
        return new ObjectMapper().readValue(decode(encodedPayload), new TypeReference<>(){});
    }

    private String decode(String encoded) {
        return new String(DECODER.decode(encoded));
    }
}
