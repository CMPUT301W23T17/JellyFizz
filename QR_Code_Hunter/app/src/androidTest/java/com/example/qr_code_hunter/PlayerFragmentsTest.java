package com.example.qr_code_hunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.util.Log;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    // Create MockUser
    String user = "UserFragTest";
    String userEmail = "nic@ualberta.ca";
    String userPhone = "1234567890";
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
        soloMain.waitForView(R.id.scan_now);

    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete5 = new CompletableFuture();

        db.collection("Players").document(user).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete5.complete(null);
                });

        completeDelete5.join();

        CompletableFuture completeDelete1 = new CompletableFuture();
        CompletableFuture completeDelete2 = new CompletableFuture();

        String hashString = "9b9d33f11c6f932d1b209d6b82550f32f946e6f0382989f56e925cfbeca9e255";
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document(user);

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

        completeDelete1.allOf(completeDelete1, completeDelete2).join();

        CompletableFuture completeDelete3 = new CompletableFuture();
        CompletableFuture completeDelete4 = new CompletableFuture();

        String hashString2 = "e96d5910b6154ac647d44d5c2a7f0477b5e7a66abc8cdc3f75386d69cde1f605";
        DocumentReference qrRef2 = db.collection("QrCodes")
                .document(hashString2);
        DocumentReference playerRef2 = db.collection("Players").document(user);

        db.collection("QrCodes").document(hashString2).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete3.complete(null);
                });

        db.collection("scannedBy")
                .whereEqualTo("Player",playerRef2)
                .whereEqualTo("qrCodeScanned",qrRef2)
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
                    completeDelete4.complete(null);
                });

        completeDelete3.allOf(completeDelete3, completeDelete4).join();

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
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_points)).getText().toString());
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_code)).getText().toString());
        assertEquals("0", ((TextView) soloMain.getView(R.id.number_rank)).getText().toString());
    }

    @Test
    public void testUpdatedPlayerInfo() throws Exception {
        CompletableFuture complete1 = new CompletableFuture<>();
        CompletableFuture complete2 = new CompletableFuture<>();
        CompletableFuture complete3 = new CompletableFuture<>();
        CompletableFuture complete4 = new CompletableFuture<>();

        String hashString = "9b9d33f11c6f932d1b209d6b82550f32f946e6f0382989f56e925cfbeca9e255";
        String hashString2 = "e96d5910b6154ac647d44d5c2a7f0477b5e7a66abc8cdc3f75386d69cde1f605";

        // Mock relation in QrCodes collection
        Map<String, Object> mockQr = new HashMap<>();
        mockQr.put("binaryString", "0011100101100010001110010110010000110011");
        mockQr.put("codeName", "RedSeaGasEraBatJawOakIceMudAxe");
        mockQr.put("Score", 12);

        Map<String, Object> mockQr2 = new HashMap<>();
        mockQr2.put("binaryString", "0110010100111001001101100110010000110101");
        mockQr2.put("codeName", "RedBayGasArtOwlEyeOakIceMudSaw");
        mockQr2.put("Score", 20);

        // Mock relation in scannedBy collection
        Map<String, Object> mockRelation = new HashMap<>();
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document(user);
        mockRelation.put("Player", playerRef);
        mockRelation.put("qrCodeScanned",qrRef);

        Map<String, Object> mockRelation2 = new HashMap<>();
        DocumentReference qrRef2 = db.collection("QrCodes")
                .document(hashString2);
        mockRelation2.put("Player", playerRef);
        mockRelation2.put("qrCodeScanned",qrRef2);

        // Add to database
        db.collection("QrCodes")
                .document(hashString)
                .set(mockQr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        complete1.complete(null);
                    }
                });

        db.collection("QrCodes")
                .document(hashString2)
                .set(mockQr2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        complete3.complete(null);
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
                        complete4.complete(null);
                    }
                });

        complete1.allOf(complete1, complete2, complete3, complete4).join();

        playerRef.update("rank", 1);
        playerRef.update("score", 32);
        playerRef.update("totalCodeScanned", 2);

        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        soloMain.waitForView(R.id.imageView00);

        QrCode mockCode1 = new QrCode("9b9d33f11c6f932d1b209d6b82550f32f946e6f0382989f56e925cfbeca9e255");
        QrCode mockCode2 = new QrCode("e96d5910b6154ac647d44d5c2a7f0477b5e7a66abc8cdc3f75386d69cde1f605");

        assertEquals("32", ((TextView) soloMain.getView(R.id.number_points)).getText().toString());
        assertEquals("2", ((TextView) soloMain.getView(R.id.number_code)).getText().toString());
        assertEquals("1", ((TextView) soloMain.getView(R.id.number_rank)).getText().toString());

        soloMain.waitForView(R.id.firstQrCodeImage);
        assertEquals(mockCode1.getVisualRep("0011100101100010001110010110010000110011"), ((TextView) soloMain.getView(R.id.firstQrCodeImage)).getText().toString());

        soloMain.waitForView(R.id.secondQrCodeImage);
        assertEquals(mockCode2.getVisualRep("0110010100111001001101100110010000110101"), ((TextView) soloMain.getView(R.id.secondQrCodeImage)).getText().toString());

    }

    @Test
    public void testSwitchPrivacyButton() throws Exception {
        soloMain.clickOnView(soloMain.getView(R.id.player_profile_screen));
        soloMain.waitForView(R.id.player_profile_screen);

        onView(withId(R.id.switch_privacy)).check(matches(isClickable()));
        soloMain.clickOnView(soloMain.getView(R.id.switch_privacy));

        soloMain.clickOnView(soloMain.getView(R.id.ranking_screen));
        soloMain.waitForView(R.id.ranking_screen);

        soloMain.clickOnText(user);
        soloMain.waitForView(R.id.other_player_profile_frag);

        assertEquals("Your profile is set to private. \nYou can change your privacy from your Profile screen.", ((TextView) soloMain.getView(R.id.email)).getText().toString());

    }
}
