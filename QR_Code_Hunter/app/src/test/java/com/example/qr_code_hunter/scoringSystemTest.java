package com.example.qr_code_hunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class scoringSystemTest {

    private QR_Code createTestCode() {
        return new QR_Code("filler text until hashing is done");
    }

    @Test
    void scoreTest() {
        QR_Code testQR_Code = createTestCode();
        assertEquals(111, testQR_Code.getScore());
    }

}
