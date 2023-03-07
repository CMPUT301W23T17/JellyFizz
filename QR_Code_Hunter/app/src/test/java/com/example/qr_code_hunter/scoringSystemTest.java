package com.example.qr_code_hunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.util.Log;

import org.junit.jupiter.api.Test;

public class scoringSystemTest {

    private QR_Code createTestCode(String scannedString) {
        return new QR_Code(scannedString);
    }

    @Test
    void scoreTest() {
        //        eclass qr code input string: BFG5DGW54
        QR_Code testQR_Code = createTestCode("Enter test qr code input here");

        //enter score where 111 is below
        assertEquals(111, testQR_Code.getScore());
    }

}
