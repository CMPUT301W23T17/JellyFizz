package com.example.qr_code_hunter;

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


    QRCode(String scannedString) {
        setName(scannedString);
        setVisualRep(scannedString);
//        setScore(shaGeneratorHexadecimal(scannedString));
    }

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

}
