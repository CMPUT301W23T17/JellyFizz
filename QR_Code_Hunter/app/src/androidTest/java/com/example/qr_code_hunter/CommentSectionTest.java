package com.example.qr_code_hunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RunWith(AndroidJUnit4.class)
public class CommentSectionTest {
    private Solo loginSolo;
    private Solo mainSolo;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        loginSolo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        mainSolo = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());
    }

    /**
     * Create mock qrCode for user to delete
     */
    @BeforeClass
    public static void setUpDependencies() {
        CompletableFuture complete1 = new CompletableFuture<>();
        CompletableFuture complete2 = new CompletableFuture<>();
        CompletableFuture complete3 = new CompletableFuture<>();
        String hashString = "c6138abfa6a734269ef280d53f37d351d08408258322aa818f4cf9fe9fa4bb0d";

        // Mock relation in QrCodes collection
        Map<String, Object> mockQr = new HashMap<>();
        mockQr.put("binaryString", "0110001100110110001100010011001100111000");
        mockQr.put("codeName", "RedBayGasArtOwlJawLogIceMudSaw");
        mockQr.put("Score",27);

        // Mock relation in scannedBy collection with comment
        Map<String, Object> mockRelation = new HashMap<>();
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testComment");
        mockRelation.put("Player", playerRef);
        mockRelation.put("qrCodeScanned",qrRef);
        mockRelation.put("Comment", "This is a test on comment");

        Map<String, Object> mockRelation2 = new HashMap<>();
        DocumentReference playerRef2 = db.collection("Players").document("CodeHunter");
        mockRelation2.put("Player", playerRef2);
        mockRelation2.put("qrCodeScanned",qrRef);
        mockRelation2.put("Comment", "");

        // Add to database
        db.collection("QrCodes")
                .document(hashString)
                .set(mockQr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        complete1.complete(null);
                    }
                });

        db.collection("scannedBy")
                .document()
                .set(mockRelation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        complete2.complete(null);
                    }
                });

        db.collection("scannedBy")
                .document()
                .set(mockRelation2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        complete3.complete(null);
                    }
                });

        complete1.allOf(complete1, complete2, complete3).join();
    }

    /**
     * Click on current user profile, assert True if move to correct fragment
     */
    @Test
    public void testDisplayCommentDialog() throws InterruptedException {
        // Let user login as always
        String username = "testComment";
        String email = "testComment@example.com";
        String phone = "1234567890";
        loginSolo.enterText(0, username);
        loginSolo.enterText(1, email);
        loginSolo.enterText(2, phone);
        // Click register button
        loginSolo.clickOnButton("Register");
        loginSolo.waitForView(loginSolo.getView(R.id.scan_now));
        // Click the player profile screen item in the bottom navigation bar
        loginSolo.clickOnView(mainSolo.getView(R.id.player_profile_screen));
        loginSolo.waitForView(R.id.user_profile_fragment);
        // Move to QrCode list of testuser
        loginSolo.waitForView(R.id.firstQrCodeImage);
        loginSolo.clickOnView(mainSolo.getView(R.id.more_button));
        loginSolo.waitForView(R.id.qr_code_visualRep);
        // Move to QrCode profile and click to see comment section
        loginSolo.clickInList(0);
        loginSolo.waitForView(R.id.fragment_code_details);
        loginSolo.clickOnButton("Show comment section");
        loginSolo.waitForView(R.id.comment_dialog);
        // Check if successfully display comments by all players
        onView(withId(R.id.comment_recycler)).check(matches(isDisplayed()));
        onView(withId(R.id.comment_recycler)).check(matches(hasChildCount(1)));
        onView(withId(R.id.buttonPLayers)).perform(click());
        loginSolo.waitForView(R.id.comment_recycler);
        onView(withId(R.id.comment_recycler)).check(matches(isDisplayed()));
        onView(withId(R.id.comment_recycler)).check(matches(hasChildCount(2)));
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        loginSolo.finishOpenedActivities();
    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();
        CompletableFuture completeDelete2 = new CompletableFuture();
        CompletableFuture completeDelete4 = new CompletableFuture();

        String hashString = "c6138abfa6a734269ef280d53f37d351d08408258322aa818f4cf9fe9fa4bb0d";
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testComment");
        DocumentReference playerRef2 = db.collection("Players").document("CodeHunter");

        db.collection("scannedBy")
                .whereEqualTo("qrCodeScanned",qrRef)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                            Log.d("Working", "Document deleted!");
                        }
                    } else {
                        Log.e("Working", "Failed with: ", task.getException());
                    }
                    completeDelete2.complete(null);
                });

        db.collection("QrCodes").document(hashString).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });


        db.collection("Players").document("testComment").delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete4.complete(null);
                });

        completeDelete2.allOf(completeDelete2, completeDelete1, completeDelete4).join();
    }
}
