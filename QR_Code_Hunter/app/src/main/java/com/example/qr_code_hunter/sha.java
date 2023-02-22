package com.example.qr_code_hunter;

import java.security.MessageDigest;

/**
 ** http://lugeek.com
 */

public class sha {
    /**
     *
     * @param data to be encrypted
     * @param shaN encrypt method,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }
}
