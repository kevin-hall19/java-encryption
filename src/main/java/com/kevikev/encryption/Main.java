package com.kevikev.encryption;

import com.kevikev.encryption.aes.AESEncryption;
import com.kevikev.encryption.util.ConsoleUtils;

/**
 * Main class for running the jar directly. Encryption and Decryption from command-line.
 */
public class Main {
    public static final String USAGE = "Usage: java -jar java-encryption-<version>-all.jar <encrypt|decrypt> <data> <passphrase> [<numIterations>] [<keyLength>] [<saltLength>]";
    static ConsoleUtils console = new ConsoleUtils();

    private Main() {
        throw new IllegalStateException("Main class");
    }

    static void main(String[] args) {
        String method = getParam(args, 0, "method");
        String data = getParam(args, 1, "data");
        String passphrase = getParam(args, 2, "passphrase");
        if (isRequestBlank(method, data, passphrase)) {
            // Should never get here unless testing.
            return;
        }
        if ("encrypt".equalsIgnoreCase(method)) {
            int numIterations = getNumericParam(args, 3, "numIterations", AESEncryption.DEFAULT_ITERATIONS);
            int keyLength = getNumericParam(args, 4, "keyLength", AESEncryption.DEFAULT_KEY_LENGTH);
            int saltLength = getNumericParam(args, 4, "saltLength", AESEncryption.DEFAULT_KEY_LENGTH);
            if(numIterations > 0 && keyLength > 0 && saltLength > 0) {
                String encrypted = AESEncryption.encrypt(data, passphrase, numIterations, keyLength, saltLength);
                console.printLn(encrypted);
            }
        } else if ("decrypt".equalsIgnoreCase(method)) {
            String decrypted = AESEncryption.decrypt(data, passphrase);
            console.printLn(decrypted);
        } else {
            console.exit(101, String.format("Method not supported: %s", method), USAGE);
        }
    }

    private static String getParam(String[] args, int index, String paramName) {
        String value = getParam(args, index, paramName, null);
        if (value == null || value.isBlank()) {
            console.exit(index + 1);
        }
        return value;
    }

    private static String getParam(String[] args, int index, String paramName, String defaultValue) {
        String value = System.getenv(paramName);
        if (args.length > index) {
            value = args[index];
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private static int getNumericParam(String[] args, int index, String paramName, int defaultValue) {
        String value = getParam(args, index, paramName, String.valueOf(defaultValue));
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            console.exit(201+index, String.format("Invalid arg. Must be numeric. Data: \"%s\". Arg: \"%s\"", value, paramName), USAGE);
            return -1;
        }
        return Integer.parseInt(value);
    }

    private static boolean isRequestBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
