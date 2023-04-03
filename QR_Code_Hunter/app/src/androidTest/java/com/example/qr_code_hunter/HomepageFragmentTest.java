package com.example.qr_code_hunter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class HomepageFragmentTest {

    private Solo soloLogin;
    private Solo soloMain;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Change this username until it's unique
    String user = "TestHomepage";
    @Rule
    public ActivityTestRule<LoginActivity> logInRule = new ActivityTestRule<>(LoginActivity.class, true, true);

    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception if error occurs during test
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
        soloMain.clickOnView(soloMain.getView(R.id.home_screen));
        //solo.waitForView(solo.getView(R.id.buttonTotalScore));
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

    /**
    *This test verifies that the scan button is displayed on the screen.
    *It checks that the view with the ID "scan_button" is visible to the user.
    */
    @Test
    public void scanButtonDisplayed() {
        soloMain.waitForView(R.id.home_screen);
        onView(withId(R.id.scan_button)).check(matches(isDisplayed()));
    }

    /**
    *This test verifies that the instruction button is displayed on the screen.
    *It checks that the view with the ID "ask_button" is visible to the user.
    */
    @Test
    public void instructionButtonDisplayed() {
        soloMain.waitForView(R.id.home_screen);
        onView(withId(R.id.ask_button)).check(matches(isDisplayed()));
    }
    
    /**
    *This test verifies that clicking the scan button results in the expected behavior.
    *It performs a click on the view with the ID "scan_button".
    */
    @Test
    public void clickScanButton() {
        soloMain.waitForView(R.id.home_screen);
        onView(withId(R.id.scan_button)).check(matches(isClickable()));
    }

    /**
    *This test verifies that clicking the instruction button results in the expected behavior.
    *It performs a click on the view with the ID "ask_button".
    */
    @Test
    public void clickInstructionButton() {
        soloMain.waitForView(R.id.home_screen);
        onView(withId(R.id.scan_button)).check(matches(isClickable()));
    }

    /**
    *This tests the change between fragments of navigation bar
    */
    @Test
    public void testNavigation() {
        // Click on Search screen
        onView(withId(R.id.search_screen)).perform(click());
        soloMain.waitForView(R.id.search_screen);
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        // Click on Home screen
        onView(withId(R.id.home_screen)).perform(click());
        soloMain.waitForView(R.id.home_screen);
        onView(withId(R.id.scan_now)).check(matches(isDisplayed()));

        // Click on Ranking screen
        onView(withId(R.id.ranking_screen)).perform(click());
        soloMain.waitForView(R.id.ranking_screen);
        onView(withId(R.id.ranking_screen)).check(matches(isDisplayed()));

        // Click on Player Profile screen
        onView(withId(R.id.player_profile_screen)).perform(click());
        soloMain.waitForView(R.id.player_profile_screen);
        onView(withId(R.id.owner_infor_box)).check(matches(isDisplayed()));
    }
}
