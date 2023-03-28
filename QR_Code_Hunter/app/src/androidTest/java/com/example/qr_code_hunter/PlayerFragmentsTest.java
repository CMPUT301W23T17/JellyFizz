package com.example.qr_code_hunter;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.widget.ListView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class PlayerFragmentsTest {
    private Solo soloLogin;
    private Solo soloMain;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Change this username until it's unique
    String user = "UserFragTest";
    String tempPath = "8sHg7GKj9nLp5fDx4E7Z";
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
        soloLogin.enterText(1, "nic@ualberta.ca");
        soloLogin.enterText(2, "1234567890");
        // Click register button
        soloLogin.clickOnButton("Register");

        // Wait
        soloMain.waitForView(R.id.scan_now);
        // Click register button


//
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();
        CompletableFuture completeDelete2 = new CompletableFuture();

        db.collection("Players").document(user).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });

        db.collection("scannedBy").document(tempPath).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete2.complete(null);
                });

        completeDelete1.join();
        completeDelete2.join();
    }

    @Test
    public void testPlayerProfileFragment() throws Exception {
        soloMain.waitForView(R.id.imageView00);
        // Verify that the Player Profile Screen is displayed
        onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));
    }

    @Test
    public void testPlayerCodeListFragment() throws Exception {
        soloMain.waitForView(R.id.imageView00);
        // Verify that the Player Profile Screen is displayed
        // onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));

        soloMain.waitForView(R.id.more_button);
//        onView(withId(R.id.more_button)).check(matches(isClickable()));
        soloMain.clickOnView(soloMain.getView(R.id.more_button));

        onView(withId(R.id.qr_code_list_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testCodeDetailsFragment() throws Exception {
                // Create a reference to the document in the "qrCodeScanned" collection
        DocumentReference qrCodeRef = db.collection("QrCodes").document("9b9d33f11c6f932d1b209d6b82550f32f946e6f0382989f56e925cfbeca9e255");

// Create a reference to the document in the "Player" collection
        DocumentReference playerRef = db.collection("Players").document(user);

        // Create a new document with the specified fields
        Map<String, Object> tempData = new HashMap<>();
        tempData.put("qrCodeScanned", qrCodeRef);
        tempData.put("Player", playerRef);
        tempData.put("Comment", "For testing");
        db.collection("scannedBy").document(tempPath)
                .set(tempData);

        soloMain.waitForView(R.id.imageView00);
        // Verify that the Player Profile Screen is displayed
        // onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));

        soloMain.waitForView(R.id.more_button);
//        onView(withId(R.id.more_button)).check(matches(isClickable()));
        soloMain.clickOnView(soloMain.getView(R.id.more_button));


        soloMain.waitForView(R.id.qr_code_list_fragment);
        // Verify that the Code List is displayed
        // onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));

        soloMain.waitForView(R.id.qr_code_lister);
//        onView(withId(R.id.more_button)).check(matches(isClickable()));
//        soloMain.clickOnView(soloMain.getView(R.id.more_button));
//        ListView lv = soloMain.getCurrentViews(ListView.class).get(0);

        ListView codeList = (ListView) soloMain.getView(R.id.qr_code_lister);
        soloMain.clickInList(0);

        soloMain.waitForView(R.id.fragment_code_details);
        onView(withId(R.id.fragment_code_details)).check(matches(isDisplayed()));
    }

}
