package com.example.qr_code_hunter;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.widget.Button;

import com.example.qr_code_hunter.MainActivity;
import com.example.qr_code_hunter.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class CodeDetailsFragmentTest {
    private Solo soloLogin;
    private Solo soloMain;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Change this username until it's unique
    String user = "TestRanking";
    @Rule
    public ActivityTestRule<LoginActivity> logInRule = new ActivityTestRule<>(LoginActivity.class, true, true);

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
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        soloMain.waitForView(R.id.more_button);
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
*This method is a unit test that verifies the behavior of the back button on the app.
*It waits for the more button to be visible on the screen and then checks that it is clickable.
*/
    @Test
    public void testBackButton() {
        //soloMain.waitForView(R.id.details_backBtn);
        soloMain.waitForView(R.id.more_button);
        onView(withId(R.id.more_button)).check(matches(isClickable()));
    }

}
