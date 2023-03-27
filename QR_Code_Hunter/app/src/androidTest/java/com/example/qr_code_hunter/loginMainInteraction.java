package com.example.qr_code_hunter;

import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MainActivity using Robotium. All the UI tests are written here.
 */
@RunWith(AndroidJUnit4.class)
public class loginMainInteraction {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    //Runs after every Method
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Test
    public void testLoginvsHomePageDisplayed() throws Exception {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rule.getActivity());
        boolean accountCreated = prefs.contains(solo.getCurrentActivity().getString(R.string.accountCreated));

        if (accountCreated) {
            //Verify that the homepage_fragment is displayed if the user has not created an account yet
            assertTrue(solo.waitForFragmentByTag("HomepageFragment"));
        } else {

        }
    }

}