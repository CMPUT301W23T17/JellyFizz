package com.example.qr_code_hunter;

import java.math.BigInteger;

public class QR_Code {
    /**
     * This creates the string of first 12 bits of the binary value based on QR Code
     * @param input
     *      This is input of the QR code
     */
    public String shaGeneratorBinary (String input){
        byte[] inputData = input.getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = sha.encryptSHA(inputData, "SHA-256");
        } catch (Exception e){
            e.printStackTrace();
        }
        int resultInt1 = getBit(outputData, 0);
        int resultInt2 = getBit(outputData, 1);
        String result = String.valueOf(resultInt1) + String.valueOf(resultInt2);
        return result;
    };
    /**
     * This creates the string of SHA QR Code
     * @param input
     *      This is input of the QR code
     */
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
    /**
     * This creates the integer represented bits
     * @param data,pos
     *      This is data, pos
     */
    private static int getBit(byte[] data, int pos) {
        int posByte = pos/8;
        int posBit = pos%8;
        byte valByte = data[posByte];
        int valInt = valByte>>(8-(posBit+1)) & 0x0001;
        return valInt;
    }
}
