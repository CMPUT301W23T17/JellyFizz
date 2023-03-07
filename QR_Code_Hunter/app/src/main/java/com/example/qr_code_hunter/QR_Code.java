package com.example.qr_code_hunter;

import java.util.Map;

public class QR_Code {
    private String code_name = "";
    private Integer score;






    private static Map<Character, Integer> alphabetPoints = Map.of(
            'a', 10,
            'b', 11,
            'c', 12,
            'd', 13,
            'e', 14,
            'f', 15
    );












    /**
     * This creates a unique name corresponding to one QR code using the first 10 bits of the binary value
     * @param binary
     *      This is the binary value of the SHA-256 hash of the code
     */
    public void setName(String binary) {
        char bin[] = binary.toCharArray();
        String[][] namesArray = new String[][]{
                new String[]{"Red", "Jet"},
                new String[]{"Sea", "Bay"},
                new String[]{"Car", "Gas"},
                new String[]{"Art", "Era"},
                new String[]{"Owl", "Bat"},
                new String[]{"Jaw", "Eye"},
                new String[]{"Oak", "Log"},
                new String[]{"Flu", "Ice"},
                new String[]{"Mud", "Oil"},
                new String[]{"Saw", "Axe"}
        };

        for (int i = 0; i < 10; i++) {
            if (bin[i] == '1') {
                code_name = code_name.concat(namesArray[i][1]);
            } else {
                code_name = code_name.concat(namesArray[i][0]);
            }
        }
    }

    /**
     * @param hashString this is the hash of the QR_Code scanned representation
     * @return this methods returns the score of the QR_Code based on the scoring system on eclass
     */
    public static void scoreQR_Code(String hashString) {
        int score = 0;
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
                if (count > 0) score += iterationScore;
                currentChar = hashString.charAt(i);
                count = 0;
            }
        }
    }




























    /**
     * This returns the name of a code
     * @return
     *      Returns a string-type
     */
    public String getName() {return code_name;}
    /**
     * This returns the visual score of a code
     * @return
     *      Returns a integer-type
     */
    public Integer getScore() {return score;}
    /**
     * This returns the visual reprsentation of a code
     * @return
     *      Returns a string-type
     */
    public String getVisualRep() {return code_name;}

}
