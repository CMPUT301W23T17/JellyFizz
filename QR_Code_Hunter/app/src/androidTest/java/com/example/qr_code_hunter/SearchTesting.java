package com.example.qr_code_hunter;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertTrue;

import android.widget.ListAdapter;
import android.widget.ListView;

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
public class SearchTesting {
    private Solo soloLogin;
    private Solo soloMain;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    String user = "DanTestSearch";


    @Rule
    public ActivityTestRule<LoginActivity> logInRule = new ActivityTestRule<>(LoginActivity.class, true, true);

    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception if an error occurs during the test.
     */
    @Before
    public void setUp() throws Exception {
        soloLogin = new Solo(InstrumentationRegistry.getInstrumentation(), logInRule.getActivity());
        soloMain = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());
        // Enter valid user details
        soloLogin.enterText(0, user);
        soloLogin.enterText(1, "akanmu@ualberta.ca");
        soloLogin.enterText(2, "6045556795");
        // Click register button
        soloLogin.clickOnButton("Register");



        soloMain.waitForView(R.id.home_screen);
        soloMain.clickOnView(soloMain.getView(R.id.search_screen));
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
    public void testSearchScreen() throws Exception {
        soloMain.waitForView(R.id.search_screen);
        // Verify that the loginActivity is displayed
        onView(withId(R.id.search_screen)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearching() throws Exception {
        soloMain.waitForView(R.id.search_screen);
//        // Verify that the loginActivity is displayed
        soloMain.enterText(0,"D");
        soloMain.clickOnView(soloMain.getView(R.id.search_bar));

        soloMain.waitForView(R.id.search_list);

        ListView listView = (ListView) soloMain.getView(R.id.search_list);
        ListAdapter adapter = listView.getAdapter();

        assertTrue(adapter.getCount() > 0);
    }

    @Test
    public void testOtherProfile() throws Exception{
        soloMain.waitForView(R.id.search_screen);

        soloMain.enterText(0,"D");
        soloMain.clickOnView((soloMain.getView(R.id.search_bar)));

        soloMain.waitForView(R.id.search_list);

        // Click on the first item in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.search_list))
                .atPosition(0)
                .perform(click());


        soloMain.waitForView(R.id.imageView00);
        onView(withId(R.id.imageView00)).check(matches(isDisplayed()));

        soloMain.clickOnView((soloMain.getView(R.id.otherPlayer_backButton)));
        soloMain.waitForView(R.id.search_bar);
    }

}
