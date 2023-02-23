package com.example.qr_code_hunter;

import java.util.ArrayList;

public class Player {
    ArrayList<QR_Code> myQR_Codes = new ArrayList<QR_Code>();
    Integer total_codes_scanned;
    Boolean privacy = false; // false = not hidden, true = hidden -- default is shown

    /**
     * This changes the visibility of user information (email & phone num.)
     * @param visibility
     *      true -> hide details from other players, false -> details visible to other players
     */
    public void setPrivacy(Boolean visibility) {
        this.privacy = visibility;
    }

    /**
     * This counts the number of QR_Code objects in the array
     */
    public void totalQR_Codes() {
        total_codes_scanned = myQR_Codes.size();
    }

    /**
     * This is a getter method for the number of QR Codes scanned
     * @return
     *      returns the total number of QR codes scanned
     */
    public int getTotalQR_Code() {
        return total_codes_scanned;
    }
}
