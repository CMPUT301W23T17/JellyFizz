package com.example.qr_code_hunter;

import org.junit.Test;
import com.google.android.gms.maps.model.LatLng;
import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

/**
 * This is a class that tests the methods of QrCode
 */
public class QrCodeTest {
    @Test
    public void testSetName() throws NoSuchAlgorithmException {
        QrCode mockCode = new QrCode("hello");
        String mockCodeName = ""; // initial name is empty
        mockCodeName = mockCode.getName();
        assertEquals("RedSeaGasEraOwlJawLogFluMudAxe", mockCodeName);
    }

    @Test
    public void testVisualRepresentation() throws NoSuchAlgorithmException{
        QrCode mockQR = new QrCode("hello");

        assertEquals("  _--------------_  " +
                "\n { ~~~)      (~~~ } " +
                "\n{| ( + ) || ( + ) |}" +
                "\n{|       ||       |}" +
                "\n |      {__}      | " +
                "\n |_              _| " +
                "\n |    (______)    | " +
                "\n |                | " +
                "\n ------------------ ", mockQR.getVisualRep(mockQR.getBinaryString()));
    }

    @Test
    public void testSetScore() throws NoSuchAlgorithmException {
        QrCode testQR_Code = new QrCode("BFG5DGW54");
        assertEquals("23", testQR_Code.getScore().toString());
    }
    
    @Test
    public void testSetLocation() throws NoSuchAlgorithmException{
        QrCode qrCode = new QrCode("hello");
        LatLng location = new LatLng(40.7128, -74.0060);
        qrCode.setLocation(location);
        assertEquals(location, qrCode.getGeolocation());
    }
}
