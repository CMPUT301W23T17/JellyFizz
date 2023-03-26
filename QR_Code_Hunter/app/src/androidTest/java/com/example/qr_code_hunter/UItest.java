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

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * This is a class that tests the various elements of the UI
 */

public class UItest {
  
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
  
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
    public void testNavigation() {
        // Click on Map screen
        onView(withId(R.id.map_screen)).perform(click());
        onView(isAssignableFrom(MapFragment.class)).check(matches(isDisplayed()));

        // Click on Search screen
        onView(withId(R.id.search_screen)).perform(click());
        onView(isAssignableFrom(SearchFragment.class)).check(matches(isDisplayed()));

        // Click on Home screen
        onView(withId(R.id.home_screen)).perform(click());
        onView(isAssignableFrom(HomepageFragment.class)).check(matches(isDisplayed()));

        // Click on Ranking screen
        onView(withId(R.id.ranking_screen)).perform(click());
        onView(isAssignableFrom(RankingFragment.class)).check(matches(isDisplayed()));

        // Click on Player Profile screen
        onView(withId(R.id.player_profile_screen)).perform(click());
        onView(isAssignableFrom(PlayerProfileFragment.class)).check(matches(isDisplayed()));
    }
  
}
