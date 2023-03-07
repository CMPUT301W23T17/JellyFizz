package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;


/**
 * This is a class that tests the methods of QR_Code
 */
public class QRCodeTest {
    @Test
    public void testShaHexadecimal() throws NoSuchAlgorithmException {
        QRCode mockCode = new QRCode("1011000110101011001001");
        String result = mockCode.shaGeneratorHexadecimal("hello");
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }

    @Test
    public void testShaBinary() throws NoSuchAlgorithmException {
        QRCode mockCode = new QRCode("1011000110101011001001");
        String result = mockCode.shaGeneratorBinary("hello");
        assertEquals("0011001001100011011001100011001000110100", result);
    }

    @Test
    public void testSetName() throws NoSuchAlgorithmException {
        QRCode mockCode = new QRCode("hello");
        String mockCodeName = ""; // initial name is empty
        mockCodeName = mockCode.getName();
        assertEquals("RedSeaGasEraOwlJawLogFluMudAxe", mockCodeName);
    }

//    @Test
//    public void testVisualRepresentation() {
//        QRCode mockQR = new QRCode("11010010");
//        String rep = mockQR.getVisualRep();
//        System.out.println(rep);
//    }

//    @Test
//    public void testSetscore() {
//        QRCode testQR_Code = new QRCode("1011000110101011001001");
//        assertEquals(111, testQR_Code.getScore());
//}

}