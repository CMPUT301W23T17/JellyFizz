package com.example.qr_code_hunter;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class RankingTest {
    private Solo soloLogin;
    private Solo soloMain;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Change this username until it's unique
    String user = "TestRanking";
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

        // Wait
        soloMain.waitForView(R.id.scan_now);
        // Click register button
        soloMain.clickOnView(soloMain.getView(R.id.ranking_screen));
    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();

        db.collection("Players").document(user).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });
        completeDelete1.join();
    }


    @Test
    public void testRankingFragment() throws Exception {
        soloMain.waitForView(R.id.buttonTotalScore);
        // Verify that the loginActivity is displayed
        onView(withId(R.id.ranking_screen)).check(matches(isDisplayed()));
    }

    @Test
    public void testHighestButtonHighestCode() {
        soloMain.waitForView(R.id.buttonTotalScore);
        onView(withId(R.id.buttonHighestCode)).check(matches(isClickable()));
    }

    @Test
    public void testHighestButtonTotalScore() {
        soloMain.waitForView(R.id.buttonTotalScore);
        onView(withId(R.id.buttonTotalScore)).check(matches(isClickable()));
    }

    @Test
    public void testDisplayOwner() {
        soloMain.waitForView(R.id.buttonTotalScore);
        assertEquals("0th", ((TextView) soloMain.getView(R.id.yourRank)).getText().toString());
        assertEquals("0 pts", ((TextView) soloMain.getView(R.id.yourPoints)).getText().toString());
        assertEquals(user, ((TextView) soloMain.getView(R.id.yourName)).getText().toString());
    }
    @Test
    public void testSeeOtherPlayerProfile(){
        soloMain.waitForView(R.id.buttonTotalScore);
        soloMain.clickInList(0);
        soloMain.waitForView(R.id.other_player_profile_frag);
        onView(withId(R.id.other_player_profile_frag)).check(matches(isDisplayed()));
    }
}
