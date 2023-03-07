package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;


/**
 * This is a class that tests the methods of QR_Code
 */
public class QRCodeTest {
    @Test
    public void testHexadecimal() throws NoSuchAlgorithmException {
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
    public void testShaHexadecimal() throws NoSuchAlgorithmException {
        QRCode mockCode = new QRCode("1011000110101011001001");
        String result = mockCode.shaGeneratorHexadecimal("8227ad036b504e39fe29393ce170908be2b1ea636554488fa86de5d9d6cd2c32");
        assertEquals("38323237616430333662353034653339666532393339336365313730393038626532623165613633363535343438386661383664653564396436636432633332", result);
    }
    @Test
    public void testSetName() throws NoSuchAlgorithmException {
        QRCode mockCode = new QRCode("hello");
        String mockCodeName = ""; // initial name is empty
        mockCodeName = mockCode.getName();
        assertEquals("RedSeaGasEraOwlJawLogFluMudAxe", mockCodeName);
    }

    @Test
    public void testVisualRepresentation() throws NoSuchAlgorithmException {
        QRCode mockQR = new QRCode("hello");
        assertEquals("  _--------------_ \n" +
                " { ~~~>      <~~~ }\n" +
                "{| < + > || < + > |}\n" +
                "{|       ||       |}\n" +
                " |      {__}      | \n" +
                " |_              _| \n" +
                " |    (______)    | \n" +
                " |                | \n" +
                " ------------------ ", mockQR.getVisualRep());
    }

    @Test
    public void testSetscore() throws NoSuchAlgorithmException {
        QRCode testQR_Code = new QRCode("BFG5DGW54");
        assertEquals("19", testQR_Code.getScore().toString());
}

}