package com.example.qr_code_hunter;

public class QRCode {
    String code_name = "";

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
     * This returns the name of a code
     * @return
     *      Returns a string-type
     */
    public String getName() {
        return code_name;
    }
}
