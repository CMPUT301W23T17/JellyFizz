package com.example.qr_code_hunter;

import org.junit.Test;

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
                "\n ------------------ ", mockQR.getVisualRep());
    }

    @Test
    public void testSetScore() throws NoSuchAlgorithmException {
        QrCode testQR_Code = new QrCode("BFG5DGW54");
        assertEquals("19", testQR_Code.getScore().toString());
    }
    
    @Test
    public void testInstructionButton() {
        // First, find the instruction button and welcome owner text view
        ImageButton instructionButton = (ImageButton) activityRule.getActivity().findViewById(R.id.ask_button);
        TextView welcomeOwner = (TextView) activityRule.getActivity().findViewById(R.id.welcome_user);

        // Set the owner name to a known value
        String ownerName = "John";
        loginActivity.setOwnerName(ownerName);

        // Click on the instruction button
        instructionButton.performClick();

        // Wait for the dialog to appear
        onView(withText("Instructions")).inRoot(isDialog()).check(matches(isDisplayed()));

        // Check that the welcome owner text view displays the correct message
        onView(withId(R.id.welcome_user)).check(matches(withText("WELCOME " + ownerName)));
    }
    
    @Test
    public void testSetPrivacy() {
        QrCode qrCode = new QrCode();
        qrCode.setPrivacy(true);
        assertTrue(qrCode.getDataPrivacy());
    }

    @Test
    public void testSetLocation() {
        QrCode qrCode = new QrCode();
        LatLng location = new LatLng(40.7128, -74.0060);
        qrCode.setLocation(location);
        assertEquals(location, qrCode.getGeolocation());
    }
    
}
