package com.example.qr_code_hunter;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.espresso.Espresso.onView;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers.*;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

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
