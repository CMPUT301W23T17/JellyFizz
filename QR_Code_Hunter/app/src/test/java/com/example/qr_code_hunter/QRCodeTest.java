package com.example.qr_code_hunter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is a class that tests the methods of QR_Code
 */
public class QRCodeTest {
    @Test
    void testSetName() {
        QRCode mockCode = new QRCode();
        String mockBinaryCode = "1011000110101011001001";
        String mockCodeName = ""; // initial name is empty
        mockCode.setName(mockBinaryCode);
        mockCodeName = mockCode.getName();
        assertEquals("JetSeaGasEraOwlJawOakIceOilSaw", mockCodeName);
    }
}