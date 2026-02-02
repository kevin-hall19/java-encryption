package com.kevikev.encryption.aes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Map;

public class AESEncryption {
    public static final int DEFAULT_ITERATIONS = 100000;
    public static final int DEFAULT_KEY_LENGTH = 256;
    public static final int DEFAULT_SALT_LENGTH = 16;
    public static final int IV_LENGTH_BYTES = 16;

    private static final Logger logger = LogManager.getLogger();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private AESEncryption() {
        throw new IllegalStateException("AESEncryption Utility class");
    }

    private static SecretKeySpec deriveKey(String passphrase, byte[] salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Derive the key from the passphrase
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLength);
        SecretKey temp = factory.generateSecret(spec);
        return new SecretKeySpec(temp.getEncoded(), "AES");
    }

    public static String decrypt(String payloadBase64, String passphrase) {
        if (payloadBase64 == null || payloadBase64.isBlank()) {
            logger.debug("No payload to decrypt");
            return "";
        }
        if (passphrase == null || passphrase.isBlank()) {
            logger.debug("No passphrase to decrypt");
            return "";
        }
        try {
            // Decode base64 encoded values
            String payload = new String(DECODER.decode(payloadBase64));
            Map<String, Object> payloadMap = OBJECT_MAPPER.readValue(payload, new TypeReference<>() {
            });

            byte[] encryptedData = DECODER.decode((String) payloadMap.get("encryptedData"));
            byte[] iv = DECODER.decode((String) payloadMap.get("iv"));
            byte[] salt = DECODER.decode((String) payloadMap.get("salt"));
            int iterations = (Integer) payloadMap.get("iterations");
            int keyLength = (Integer) payloadMap.get("keyLength");

            // Derive the key from the passphrase
            SecretKeySpec key = deriveKey(passphrase, salt, iterations, keyLength);

            // Decrypt the data
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] decryptedBytes = cipher.doFinal(encryptedData);

            // Convert decrypted bytes to string
            return new String(decryptedBytes).strip().trim();
        } catch (Exception e) {
            logger.error("Error decrypting payload", e);
            return "";
        }
    }

    public static String encrypt(String data, String passphrase) {
        return encrypt(data, passphrase, DEFAULT_ITERATIONS, DEFAULT_KEY_LENGTH, DEFAULT_SALT_LENGTH);
    }

    public static String encrypt(String data, String passphrase, int numIterations) {
        return encrypt(data, passphrase, numIterations, DEFAULT_KEY_LENGTH, DEFAULT_SALT_LENGTH);
    }

    public static String encrypt(String data, String passphrase, int numIterations, int keyLength) {
        return encrypt(data, passphrase, numIterations, keyLength, DEFAULT_SALT_LENGTH);
    }

    public static String encrypt(String data, String passphrase, int numIterations, int keyLength, int saltLength) {
        if (data == null || data.isBlank()) {
            logger.debug("No data to encrypt");
            return "";
        }
        if (passphrase == null || passphrase.isBlank()) {
            logger.debug("No passphrase to encrypt");
            return "";
        }
        try {
            byte[] salt = new byte[saltLength];
            SECURE_RANDOM.nextBytes(salt);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            // Derive the key from passphrase
            SecretKeySpec key = deriveKey(passphrase, salt, numIterations, keyLength);

            //Encrypt the data
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encryptedData = cipher.doFinal(plaintext);

            // Create Map to hold encrypted payload
            Map<String, Object> payloadMap = Map.of(
                    "encryptedData", new String(ENCODER.encodeToString(encryptedData)),
                    "iv", new String(ENCODER.encodeToString(iv)),
                    "salt", new String(ENCODER.encodeToString(salt)),
                    "iterations", numIterations,
                    "keyLength", keyLength
            );

            // Convert payload to JSON String
            byte[] jsonPayload = OBJECT_MAPPER.writeValueAsBytes(payloadMap);

            // Return Base64 encoded payload
            return new String(ENCODER.encode(jsonPayload));
        } catch (Exception e) {
            logger.error("Error encrypting payload", e);
            return "";
        }
    }
}
