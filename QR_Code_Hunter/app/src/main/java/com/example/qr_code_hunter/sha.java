package com.example.qr_code_hunter;
import java.security.MessageDigest;

public class sha {
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }
}
