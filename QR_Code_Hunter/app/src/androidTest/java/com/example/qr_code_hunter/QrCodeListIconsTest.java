package com.example.qr_code_hunter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.widget.ImageView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class QrCodeListIconsTest {
    private Solo solo;
    private Solo solo2;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception if an error occurs during the test.
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());

            // Let user login as always
            String username = "testUser";
            String email = "testuser@example.com";
            String phone = "1234567890";
            solo.enterText(0, username);
            solo.enterText(1, email);
            solo.enterText(2, phone);

            // Click register button
            solo.clickOnButton("Register");
            // Assert that user is redirected to homepage
            TestCase.assertTrue(solo.waitForActivity(MainActivity.class));
            solo.clickOnView(solo2.getView(R.id.player_profile_screen));
            assertTrue(solo.waitForView(R.id.user_profile_fragment));

            // Move to QrCode list of testUser
            assertTrue(solo.waitForView(R.id.more_button));
            solo.clickOnView(solo2.getView(R.id.more_button));

            assertTrue(solo.waitForView(R.id.qr_code_list_fragment));

    }

    /**
     * Close activity after each test
     *
     * @throws Exception if an error occurs during the test.
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    /**
     * cleans up all database dependecies that were created by the setUpDepencies method. Cleans the database
     *
     * @throws InterruptedException if an error occurs during the test
     */
    @AfterClass
    public static void cleanup() throws InterruptedException {
        ArrayList<CompletableFuture> features = new ArrayList<>();

        DocumentReference playerRef = db.collection("Players").document("testUser");

        CompletableFuture completeDelete3 = new CompletableFuture();

        db.collection("Players").document("testUser").delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete3.complete(null);
                });

        completeDelete3.join();
    }


    /**
     * This method tests that the back button is working correctly and the garbageButton functionality for the qrCodeList Class
     */
    @Test
    public void testIcons() {
        //Get the garbage button
        assertTrue(solo.waitForView(R.id.garbage_can_icon));
        ImageView garbageButton = (ImageView) solo.getView(R.id.garbage_can_icon);

        solo.clickOnView(garbageButton);

        //Wait for changes to occur
        assertTrue(solo.waitForView(R.id.garbage_can_icon));
        garbageButton = (ImageView) solo.getView(R.id.garbage_can_icon);

        //Make sure garbage mode was activated
        assertFalse(QrCodeList.goToGarbage);

        solo.clickOnView(garbageButton);

        //Make sure garbage mode was deactivated
        assertTrue(solo.waitForView(R.id.garbage_can_icon));
        assertTrue(QrCodeList.goToGarbage);


        //Make sure the back button is working
        solo.waitForView(R.id.return_button);
        ImageView returnButton = (ImageView) solo.getView(R.id.return_button);
        solo.clickOnView(returnButton);
        assertTrue(solo.waitForView(R.id.user_profile_fragment));
    }
}
