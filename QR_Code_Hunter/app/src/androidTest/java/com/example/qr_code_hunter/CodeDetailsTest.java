package com.example.qr_code_hunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.anything;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeDetailsTest {
    FragmentScenario<PlayerProfileFragment> playerProfileScreen;

    @Before
    public void setUp() {
        //Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // Launch player fragment
        playerProfileScreen = FragmentScenario.launchInContainer(PlayerProfileFragment.class);
    }

    @Test
    public void testCodeDetailScreenLaunched() {
        // Click on more button to open player's list of codes
        onView(withId(R.id.more_button)).perform(click());

        // Click anywhere on the ListView
        onData(anything()).inAdapterView(withId(R.id.qr_code_lister)).atPosition(0).perform(click());
        onView(withId(R.id.qr_code_lister)).perform(click());

        // Check that the screen is not on Player Codes anymore
        // by checking if the ListView is gone from the screen
        onView(withId(R.id.qr_code_lister)).check(doesNotExist());
    }

}

