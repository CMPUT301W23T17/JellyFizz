package com.example.qr_code_hunter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class QRCode {
    //QR_Code Attribute
    private String code_name = "";
    private Integer score;
    private String visual_rep = "";


    //Supported Attribute
    private static Map<Character, Integer> alphabetPoints = Map.of(
            'a', 10,
            'b', 11,
            'c', 12,
            'd', 13,
            'e', 14,
            'f', 15
    );


    QRCode(String scannedString) throws NoSuchAlgorithmException {
        setName(this.shaGeneratorBinary(scannedString));
        setVisualRep(this.shaGeneratorBinary(scannedString));
        setScore(this.shaGeneratorHexadecimal(scannedString));
    }


    /**
     * Returns a string using SHA-256
     * @param input a scanned string
     * @return  a string representation of QR code
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


    /**
     * Returns a binary string using SHA-256
     * @param input a scanned string
     * @return  a binary string representation of QR code
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
    public void setScore(String hashString) {
        this.score = 0;
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
                if (count > 0) this.score += iterationScore;
                currentChar = hashString.charAt(i);
                count = 0;
            }
        }
    }


    /**
     * Returns a visual representation of the QR code
     * @param binary binary number that will determine the attributes of the visual
     * @return string representation of QR code
     */
    public void setVisualRep(String binary){
        char bin[] = binary.toCharArray();
        if(bin[0] == '1'){
            visual_rep ="  _||||||||||||||_ ";
        }else{
            visual_rep ="  _--------------_ ";
        }
        if(bin[1] == '1'){
            visual_rep = visual_rep.concat("\n { ----      ---- }");
        }else{
            visual_rep = visual_rep.concat("\n { ~~~>      <~~~ }");
        }
        if(bin[2] == '1'){
            visual_rep = visual_rep.concat("\n{| < + > || < + > |}");
        }else{
            visual_rep = visual_rep.concat("\n>|-[ @]--||--[ @]-|<");
        }
        if(bin[3] == '1'){
            visual_rep = visual_rep.concat("\n{|       ||       |}");
            visual_rep = visual_rep.concat("\n |      {__}      | ");
        }else{
            visual_rep = visual_rep.concat("\n>|       ||       |<");
            visual_rep = visual_rep.concat("\n |      <..>      | ");
        }
        if(bin[4] == '1'){
            visual_rep = visual_rep.concat("\n |   _~~~~~~~~_   | ");
        }else{
            visual_rep = visual_rep.concat("\n |_              _| ");
        }
        if(bin[5] == '1'){
            if(bin[6] == '1'){
                visual_rep = visual_rep.concat("\n  |_  (______)  _| ");
            }else{
                visual_rep = visual_rep.concat("\n  |_  [||||||]  _| ");
            }
            visual_rep = visual_rep.concat("\n   |_          _| ");
        }else{
            if(bin[6] == '1'){
                visual_rep = visual_rep.concat("\n |    (______)    | ");
            }else{
                visual_rep = visual_rep.concat("\n |    [||||||]    | ");
            }
            visual_rep = visual_rep.concat("\n |                | ");
        }
        if(bin[7] == '1'){
            visual_rep = visual_rep.concat("\n -----||||||||----- ");
        }else{
            visual_rep = visual_rep.concat("\n ------------------ ");
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
    public String getVisualRep() {return visual_rep;}
    public String QRCode_Scanner(){
      //code...
      return "";
    }

}
