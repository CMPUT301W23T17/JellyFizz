package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is a class that tests the various elements of the UI
 */

public class UItest {
  
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
  
}
