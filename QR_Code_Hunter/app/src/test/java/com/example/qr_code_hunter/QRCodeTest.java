package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * This is a class that tests the methods of QR_Code
 */
public class QRCodeTest {
    @Test
    public void testSha


    @Test
    public void testSetName() {
        QRCode mockCode = new QRCode("1011000110101011001001");
        String mockCodeName = ""; // initial name is empty
        mockCodeName = mockCode.getName();
        assertEquals("JetSeaGasEraOwlJawOakIceOilSaw", mockCodeName);
    }

//    @Test
//    public void testVisualRepresentation() {
//        QRCode mockQR = new QRCode("11010010");
//        String rep = mockQR.getVisualRep();
//        System.out.println(rep);
//    }
    @Test
    public void testSetscore() {
        QRCode testQR_Code = new QRCode("1011000110101011001001");
        assertEquals(111, testQR_Code.getScore());
}

}