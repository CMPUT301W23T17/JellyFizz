package com.example.qr_code_hunter;

import java.util.*;
import java.math.BigInteger;


public class QR_Code {

    private String hashString;
    private int score;
    private static Map<Character, Integer> alphabetPoints = Map.of(
            'a', 10,
            'b', 11,
            'c', 12,
            'd', 13,
            'e', 14,
            'f', 15
    );


    /**
     * This method is the constructor for the QR_Code class, receives the scanned QR_Code string and calls all initialization methods
     *
     * @param scannedString this will be the scanned String representation of a QR code
     */
    QR_Code(String scannedString) {
        //all initialization methods of the class should be called here, (visual rep, score, hashing, geolocation, etc...)
        // will be called when a QR_Code is made

        //Hashing methods should be called first

        this.hashString = shaGeneratorHexadecimal(scannedString);
//        this.score = scoreQR_Code(this.hashString);
    }


    public int getScore() {
        return score;
    }

    public String getHashString() {
        return hashString;
    }

    /**
     * This creates the string of first 12 bits of the binary value based on QR Code
     *
     * @param input This is input of the QR code
     */
    public String shaGeneratorBinary(String input) {
        byte[] inputData = input.getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = sha.encryptSHA(inputData, "SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int resultInt1 = getBit(outputData, 0);
        int resultInt2 = getBit(outputData, 1);
        String result = String.valueOf(resultInt1) + String.valueOf(resultInt2);
        return result;
    }

    ;

    /**
     * This creates the string of SHA QR Code
     *
     * @param input This is input of the QR code
     */
    public String shaGeneratorHexadecimal(String input) {
        byte[] inputData = input.getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = sha.encryptSHA(inputData, "SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputData.toString();
    }

    ;

    /**
     * This creates the integer represented bits
     *
     * @param data,pos This is data, pos
     */
    private static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 1)) & 0x0001;
        return valInt;
    }





    /**
     * @param hashString this is the hash of the QR_Code scanned representation
     * @return this methods returns the score of the QR_Code based on the scoring system on eclass
     */

    public static int scoreQR_Code(String hashString) {
        int totalScore = 0;
        char currentChar = hashString.charAt(0);
        int count = 0;

        for (int i = 1; i < hashString.length(); i++) {

            if (hashString.charAt(i) == currentChar) {
                count++;
            } else {
                int iterationScore = 0;

                if (Character.isDigit(currentChar)) {
                    int repeatedInt = currentChar - 48;

                    if (repeatedInt == 0) {
                        iterationScore = (int) Math.pow(20, count);
                    } else {
                        iterationScore = (int) Math.pow(repeatedInt, count);
                    }
                } else {
                    int valueOfChar = alphabetPoints.get(currentChar);
                    iterationScore = (int) Math.pow(valueOfChar, count);
                }


                if (count > 0) totalScore += iterationScore;
                currentChar = hashString.charAt(i);
                count = 0;
            }
        }

        return totalScore;
    }
}