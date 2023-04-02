package com.example.qr_code_hunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RunWith(AndroidJUnit4.class)
public class CodeDetailsFragmentTest {
    private Solo loginSolo;
    private Solo mainSolo;
    private String visualRep =
              "  _--------------_  " +
            "\n { ~~~)      (~~~ } " +
            "\n{| ( + ) || ( + ) |}" +
            "\n{|       ||       |}" +
            "\n |      {__}      | " +
            "\n |_              _| " +
            "\n |    [||||||]    | " +
            "\n |                | " +
            "\n ------------------ ";
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception if an error occurs during the test.
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
        String hashString = "000c285457fc971f862a79b786476c78812c8897063c6fa9c045f579a3b2d63f";

        // Mock relation in QrCodes collection
        Map<String, Object> mockQr = new HashMap<>();
        mockQr.put("binaryString", "0011000000110000001100000110001100110010");
        mockQr.put("codeName", "RedBayGasArtOwlJawOakIceMudSaw");
        mockQr.put("Score",418);
        mockQr.put("latitude",53.1478);
        mockQr.put("longitude",-113.2684);

        // Mock relation in scannedBy collection with comment
        Map<String, Object> mockRelation = new HashMap<>();
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testCodes");
        mockRelation.put("Player", playerRef);
        mockRelation.put("qrCodeScanned",qrRef);
        mockRelation.put("Comment", "This is a test on comment");
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
     * Test if it display correct information of the chosen QrCode in list
     * @throws InterruptedException if an error occurs during the test.
     */
    @Test
    public void testDisplayCodeDetailFragment() throws InterruptedException {
        // Let user login as always
        String username = "testCodes";
        String email = "testCodes@example.com";
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
        // Move to QrCode list of test user
        loginSolo.waitForView(R.id.firstQrCodeImage);
        loginSolo.clickOnView(mainSolo.getView(R.id.more_button));
        loginSolo.waitForView(R.id.qr_code_visualRep);
        // Move to QrCode profile and click to see comment section
        loginSolo.clickInList(0);
        loginSolo.waitForView(R.id.fragment_code_details);
        // Check all displayed information
        assertEquals("RedBayGasArtOwlJawOakIceMudSaw", ((TextView) mainSolo.getView(R.id.details_codename)).getText().toString());
        assertEquals(visualRep, ((TextView) mainSolo.getView(R.id.details_visual)).getText().toString());
        assertEquals("418 pts", ((TextView) mainSolo.getView(R.id.details_points)).getText().toString());
        // Check image view
        Drawable drawable = ((ImageView) mainSolo.getView(R.id.details_image)).getDrawable();
        int drawableID = ((BitmapDrawable) drawable).getBitmap().getGenerationId();
        // 38 is ID for R.id.no_image
        assertEquals(38, drawableID);
    }

    /**
     * Close activity after each test
     * @throws Exception if an error occurs during the test.
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

        String hashString = "000c285457fc971f862a79b786476c78812c8897063c6fa9c045f579a3b2d63f";
        DocumentReference qrRef = db.collection("QrCodes")
                .document(hashString);
        DocumentReference playerRef = db.collection("Players").document("testCodes");

        db.collection("scannedBy")
                .whereEqualTo("qrCodeScanned",qrRef)
                .whereEqualTo("Player",playerRef)
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


        db.collection("Players").document("testCodes").delete()
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
