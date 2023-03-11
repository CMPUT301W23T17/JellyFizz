package com.example.qr_code_hunter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaGenerator {
    /**
     * This converts a string into binary representation using SHA-256 algorithm
     * @param
     *     input string from a scanned QrCode
     * @return
     *     the binary string representation of QrCode
     */
    public String shaGeneratorBinary(String input) throws NoSuchAlgorithmException {
        String target = this.shaGeneratorHexadecimal(input);
        target = target.substring(0,5);
        byte[] bytes = target.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int value = b;
            for (int i = 0; i < 8; i++) {
                binary.append((value & 128) == 0 ? 0 : 1);
                value <<= 1;
            }
        }
        return binary.toString();
    }

    /**
     * This converts a string into hexadecimal representation using SHA-256 algorithm
     * @param
     *     input string from a scanned QrCode
     * @return
     *     the hexadecimal string representation of QrCode
     */
    public String shaGeneratorHexadecimal(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] encryptSHA(byte[] data, String shaN) {
        // method not done
        return new byte[0];
    }

    public int getBit(byte[] data, int pos) {
        // method not done
        return 0;
    }
}
