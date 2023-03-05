package com.example.qr_code_hunter;

import java.math.BigInteger;

public class QR_Code {
    public String shaGeneratorHexadecimal (String input){
        byte[] inputData = input.getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = sha.encryptSHA(inputData, "SHA-256");
        } catch (Exception e){
            e.printStackTrace();
        }
        return outputData.toString();
    };
}
