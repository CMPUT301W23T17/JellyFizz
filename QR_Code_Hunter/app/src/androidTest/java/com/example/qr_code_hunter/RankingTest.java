package com.example.qr_code_hunter;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RankingTest {
    private Solo soloLogin;
    private Solo soloMain;
    // Change this username until it's unique
    String user = "BaNhano";
    @Rule
    public ActivityTestRule<loginActivity> logInRule = new ActivityTestRule<>(loginActivity.class, true, true);

    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        soloLogin = new Solo(InstrumentationRegistry.getInstrumentation(), logInRule.getActivity());
        soloMain = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());
        // Enter valid user details
        soloLogin.enterText(0, user);
        soloLogin.enterText(1, "tmquach@ualberta.ca");
        soloLogin.enterText(2, "6043765432");
        // Click register button
        soloLogin.clickOnButton("Register");
    }
    @Test
    public void testRankingFragment() throws Exception {
        // Wait
        soloMain.waitForView(R.id.scan_now);
        // Click register button
        soloMain.clickOnView(soloMain.getView(R.id.ranking_screen));
        //solo.waitForView(solo.getView(R.id.buttonTotalScore));
        soloMain.waitForView(R.id.buttonTotalScore);
        // Verify that the loginActivity is displayed
        onView(withId(R.id.ranking_screen)).check(matches(isDisplayed()));
        // Test highest button is working or not
        onView(withId(R.id.buttonHighestCode)).check(matches(isClickable()));
        // Test total button is working or not
        onView(withId(R.id.buttonTotalScore)).check(matches(isClickable()));
    }

}
