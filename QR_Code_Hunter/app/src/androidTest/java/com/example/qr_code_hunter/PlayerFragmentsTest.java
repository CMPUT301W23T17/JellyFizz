package com.example.qr_code_hunter;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.widget.ListView;
import android.widget.TextView;

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
//    Owner mockOwner = new Owner();
    String user = "UserFragTest";
    String userEmail = "nic@ualberta.ca";
    String userPhone = "1234567890";
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
        soloLogin.enterText(1, userEmail);
        soloLogin.enterText(2, userPhone);
        // Click register button
        soloLogin.clickOnButton("Register");

        // Wait
        soloMain.waitForView(R.id.scan_now);
        // Click register button


//
//        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
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
    public void testUserInfo() throws Exception {
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        soloMain.waitForView(R.id.player_profile_screen);

        assertEquals(user, ((TextView) soloMain.getView(R.id.user_name)).getText().toString());
        assertEquals(userEmail, ((TextView) soloMain.getView(R.id.email)).getText().toString().substring(7));
        assertEquals(userPhone, ((TextView) soloMain.getView(R.id.mobile_phone)).getText().toString().substring(14));
    }

    @Test
    public void testInitialPlayerInfo() throws Exception {
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        // Navigation of Player Profile has been done during the NavBar test
        soloMain.waitForView(R.id.player_profile_screen);
        // Verify that the Player Profile Screen is displayed
        //onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_points)).getText().toString());
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_code)).getText().toString());
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_rank)).getText().toString());
    }

//    @Test
//    public void testUpdatedPlayerFragmentInfo() throws Exception {
//        // If the user has at least a code scanned already
//        QrCode mockCode = new QrCode("c6138abfa6a734269ef280d53f37d351d08408258322aa818f4cf9fe9fa4bb0d");
//        mockCode.getHashString();
////        mockQr.put("binaryString", "0110001100110110001100010011001100111000");
////        mockQr.put("codeName", "RedBayGasArtOwlJawLogIceMudSaw");
////        mockQr.put("Score",23);
//
//        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
//        soloMain.waitForView(R.id.player_profile_screen);
//
//
//
//
//        //String mockVisRep = mockCode.getHashString();
//
//
//
//
//        //assertEquals(mockVisRep, ((TextView) soloMain.getView(R.id.firstQrCodeImage)).getText().toString());
//
//
//
////        assertEquals("0", ((TextView) soloMain.getView(R.id.number_points)).getText().toString());
//        assertEquals("1", ((TextView) soloMain.getView(R.id.number_code)).getText().toString());
////        assertEquals("0", ((TextView) soloMain.getView(R.id.number_rank)).getText().toString());
//
//    }

    @Test
    public void testNavigateToPlayerCodeListFragment() throws Exception {
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        soloMain.waitForView(R.id.imageView00);
        // Verify that the Player Profile Screen is displayed
        // onView(withId(R.id.player_profile_screen)).check(matches(isDisplayed()));

        soloMain.waitForView(R.id.more_button);
//        onView(withId(R.id.more_button)).check(matches(isClickable()));
        soloMain.clickOnView(soloMain.getView(R.id.more_button));

        onView(withId(R.id.qr_code_list_fragment)).check(matches(isDisplayed()));
    }
//
    @Test
    public void testNavigateToCodeDetailsFragment() throws Exception {
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

        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));

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
