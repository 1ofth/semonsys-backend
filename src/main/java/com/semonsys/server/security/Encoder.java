package com.semonsys.server.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public final class Encoder {

    private static final int ITERATION_COUNT = 16384;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int RADIX = 16;

    private Encoder() {
    }

    public static String generatePasswordHash(final String password) {
        char[] chars = password.toCharArray();
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return ITERATION_COUNT + "." + toHex(salt) + "." + toHex(hash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }
        return null;
    }


    private static String toHex(final byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(RADIX);
        int paddingLength = array.length * 2 - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static boolean validatePassword(final String originalPassword, final String storedPassword) {
        String[] parts = storedPassword.split("\\.");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
        try {
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }

        return false;
    }

    private static byte[] fromHex(final String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), RADIX);
        }
        return bytes;
    }
}
