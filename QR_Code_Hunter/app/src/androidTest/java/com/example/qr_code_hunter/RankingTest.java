package com.example.qr_code_hunter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qr_code_hunter.R;
import com.example.qr_code_hunter.RankingFragment;
import com.example.qr_code_hunter.loginActivity;
import com.robotium.solo.Solo;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RankingTest {
    private Solo soloLogin;
    private Solo soloMain;
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
        soloLogin = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // Enter valid user details
        soloLogin.enterText(0, "teser114");
        soloLogin.enterText(1, "testuser@example.com");
        soloLogin.enterText(2, "1234567890");
        // Click register button
        soloLogin.clickOnButton("Register");
    }
    @Test
    public void testRankingFragment() throws Exception {
        // Click register button
        solo.clickOnView(solo.getView(R.id.ranking_screen));
        //solo.waitForView(solo.getView(R.id.buttonTotalScore));
        // Verify that the loginActivity is displayed
        //solo.assertCurrentActivity("Expected Ranking fragment", RankingFragment.class);
    }

}