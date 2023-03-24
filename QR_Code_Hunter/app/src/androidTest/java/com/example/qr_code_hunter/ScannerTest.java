package com.example.qr_code_hunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScannerTest {

    FragmentScenario<HomepageFragment> homepageScreen;

    @Before
    public void setUp() {
        //Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // Launch homepage fragment
        homepageScreen = FragmentScenario.launchInContainer(HomepageFragment.class);
    }

    @Test
    public void testScannerScreenLaunched() {
        // Click on scan button
        onView(withId(R.id.scan_button)).perform(click());

        // Check that the screen is not on HomepageFragment anymore
        onView(withId(R.id.home_screen)).check(doesNotExist());
    }

    @After
    public void tearDown() {
        homepageScreen.close();
    }
}
