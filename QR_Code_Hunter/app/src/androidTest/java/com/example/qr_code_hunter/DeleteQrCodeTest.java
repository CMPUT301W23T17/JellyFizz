package com.example.qr_code_hunter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RunWith(AndroidJUnit4.class)
public class DeleteQrCodeTest {
    private Solo solo;
    private Solo solo2;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<loginActivity> rule =
            new ActivityTestRule<>(loginActivity.class, true, true);
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());
    }

    /**
     * Create mock qrCode for user to delete
     */
    @BeforeClass
    public static void setUpDependencies() {
        CompletableFuture complete1 = new CompletableFuture<>();
        CompletableFuture complete2 = new CompletableFuture<>();
        String hashString = "c6138abfa6a734269ef280d53f37d351d08408258322aa818f4cf9fe9fa4bb0d";

        // Mock relation in QrCodes collection
        Map<String, Object> mockQr = new HashMap<>();
        mockQr.put("binaryString", "0110001100110110001100010011001100111000");
        mockQr.put("codeName", "RedBayGasArtOwlJawLogIceMudSaw");
        mockQr.put("Score",23);

        // Mock relation in scannedBy collection
        Map<String, Object> mockRelation = new HashMap<>();
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testuser");
        mockRelation.put("Player", playerRef);
        mockRelation.put("qrCodeScanned",qrRef);

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

        complete1.allOf(complete1, complete2).join();
    }

    /**
     * Click on current user profile, assert True if move to correct fragment
     */
    @Test
    public void testDeleteCode() throws InterruptedException {
        // Let user login as always
        String username = "testuser";
        String email = "testuser@example.com";
        String phone = "1234567890";
        solo.enterText(0, username);
        solo.enterText(1, email);
        solo.enterText(2, phone);
        // Click register button
        solo.clickOnButton("Register");
        // Assert that user is redirected to homepage
        assertTrue(solo.waitForActivity(MainActivity.class));
        //Assert that the loginActivity Name is set
        assertTrue(loginActivity.getOwnerName().equals("testuser"));
        // Click the player profile screen item in the bottom navigation bar
        solo.clickOnView(solo2.getView(R.id.player_profile_screen));
        solo.waitForView(R.id.user_profile_fragment);
        // Assert if it goes to player profile
        FrameLayout frameLayout = solo2.getCurrentActivity().findViewById(R.id.user_profile_fragment);
        assertNotNull(frameLayout);
        // Move to QrCode list of testuser
        solo.waitForView(R.id.firstQrCodeImage);
        solo.clickOnView(solo2.getView(R.id.more_button));
        solo.waitForView(R.id.qr_code_list_fragment);
        solo.waitForView(R.id.qr_code_visualRep);
        // Assert if it goes to QrCode list
        ConstraintLayout constraintLayout = solo2.getCurrentActivity().findViewById(R.id.qr_code_list_fragment);
        assertNotNull(constraintLayout);
        // Perform delete action
        solo.clickOnView(solo2.getView(R.id.garbage_can_icon));
        solo.waitForView(R.id.delete_qrcode_list);
        solo.clickOnCheckBox(0);
        solo.clickOnButton("DELETE SELECTED CODES");
        solo.clickOnView(solo2.getView(R.id.qr_code_lister));
        // Check if successfully delete
        ListView listView = solo2.getCurrentActivity().findViewById(R.id.qr_code_lister);
        assertEquals(listView.getAdapter().getCount(),0);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();
        CompletableFuture completeDelete2 = new CompletableFuture();
        CompletableFuture completeDelete3 = new CompletableFuture();

        String hashString = "c6138abfa6a734269ef280d53f37d351d08408258322aa818f4cf9fe9fa4bb0d";
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testuser");

        db.collection("QrCodes").document(hashString).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });

        db.collection("scannedBy")
                .whereEqualTo("Player",playerRef)
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

        db.collection("Players").document("testuser").delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete3.complete(null);
                });

        completeDelete1.allOf(completeDelete1, completeDelete2, completeDelete3).join();
    }
}
