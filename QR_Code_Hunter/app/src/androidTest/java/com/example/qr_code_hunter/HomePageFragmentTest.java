package com.example.qr_code_hunter;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

@RunWith(AndroidJUnit4.class)
public class HomepageFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void launchFragment() {
        ActivityScenario scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            HomepageFragment homepageFragment = new HomepageFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, homepageFragment, "homepage_fragment")
                    .commit();
        });
    }

    @Test
    public void scanButtonDisplayed() {
        onView(withId(R.id.scan_button)).check(matches(isDisplayed()));
    }

    @Test
    public void instructionButtonDisplayed() {
        onView(withId(R.id.ask_button)).check(matches(isDisplayed()));
    }

    @Test
    public void clickScanButton() {
        onView(withId(R.id.scan_button)).perform(click());
    }

    @Test
    public void clickInstructionButton() {
        onView(withId(R.id.ask_button)).perform(click());
    }
}
