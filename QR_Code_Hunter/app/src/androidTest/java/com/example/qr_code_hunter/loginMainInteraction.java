package com.example.qr_code_hunter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class loginMainInteraction {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testLoginVsHomePageDisplayed() throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        boolean accountCreated = prefs.contains(
                mActivityTestRule.getActivity().getString(R.string.accountCreated));

        if (accountCreated) {
            // Verify that the homepage_fragment is displayed if the user has not created an account yet
            Espresso.onView(ViewMatchers.withId(R.id.frame_layout)).check(matches(isDisplayed()));

            // Verify that the username has been saved on the user's phone and matches what loginActivity holds
            String accountCreatedKey = mActivityTestRule.getActivity().getString(R.string.accountCreated);
            String savedUsername = prefs.getString(accountCreatedKey, "");
            assertEquals(savedUsername, loginActivity.getOwnerName());
        } else {
            // make sure the LoginActivity is displayed
            Espresso.onView(ViewMatchers.withId(R.id.loginActivityHolder)).check(matches(isDisplayed()));
        }
    }
}
