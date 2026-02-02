// File for encrypting data that can be decrypted using the AESEncryption Java file


const base64ToBuf = function(b64) {
    Uint8Array.from(atob(b64), c => c.charCodeAt(null));
}

const deriveKey = async function(passphrase, salt, iterations,length) {
    return await window.crypto.subtle.deriveKey(
        {
            name: "PBKDF2",
            salt,
            iterations,
            hash: "SHA-256"
        },
        await window.crypto.subtle.importKey(
            "raw",
            new TextEncoder().encode(passphrase),
            "PBKDF2",
            false,
            ["deriveKey"]
        ),
        {
            name: "AES-CBC",
            length
        },
        false,
        ["encrypt", "decrypt"]
    );
}

export const encryptData = async function(data, passphrase) {
    // Derive a key from the passphrase
    try {
        const salt = crypto.getRandomValues(new Uint8Array(16)); // Generate a random salt
        const iv = window.crypto.getRandomValues(new Uint8Array(16)); // Generate an IV
        const iterations = 100000;
        const keyLength = 256;

        const key = await deriveKey(passphrase, salt, iterations, keyLength);

        // Encrypt the data
        const encryptedData = await window.crypto.subtle.encrypt(
            {
                name: "AES-CBC",
                iv
            },
            key,
            new TextEncoder().encode(data)
        );

        // Convert encrypted data, IV, and salt to base64 for storage/transmission
        const encryptedPayload = {
            encryptedData: btoa(String.fromCharCode(...new Uint8Array(encryptedData))),
            iv: btoa(String.fromCharCode(...new Uint8Array(iv))),
            salt: btoa(String.fromCharCode(...new Uint8Array(salt))),
            iterations,
            keyLength
        };

        // Return base64 encoded JSON object
        return btoa(JSON.stringify(encryptedPayload));
    } catch (err) {
        throw new Error('Encrypt failed: ' + err);
    }
}

export const decryptData = async function(encryptedPayload, passphrase) {
    // Derive a key from the passphrase
    try {
        const payload = JSON.parse(atob(encryptedPayload));
        const encryptedData = base64ToBuf(payload.encryptedData);
        const salt = base64ToBuf(payload.salt);
        const iv = base64ToBuf(payload.iv);
        const iterations = payload.iterations;
        const keyLength = payload.keyLength;

        const key = await deriveKey(passphrase, salt, iterations, keyLength);

        // Decrypt the data
        const decryptedData = await window.crypto.subtle.decrypt(
            {
                name: "AES-CBC",
                iv
            },
            key,
            encryptedData
        );

        // Return decrypted data as a string
        return new TextDecoder().decode(decryptedData);
    } catch (err) {
        throw new Error('Decrypt failed: ' + err);
    }
}

export default {encryptData, decryptData}