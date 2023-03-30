package com.example.qr_code_hunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class qrCodeListProfileTests {

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
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());

        // Let user login as always
        String username = "testusee";
        String email = "testusee@example.com";
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

        // Move to QrCode list of testusee
        assertTrue(solo.waitForView(R.id.more_button));
        solo.clickOnView(solo2.getView(R.id.more_button));

        assertTrue(solo.waitForView(R.id.qr_code_list_fragment));
    }

    /**
     * Close activity after each test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    //Enter 3 hashCodes to test here:
    static String hashString3 = "2cf24dba5fb0a30000003b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
    static String hashString2 = "2cf24dba5fb0a31110003b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
    static String hashString1 = "2cf24dba5fb0a31111003b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
    static String[] testHashStrings = {hashString3, hashString2, hashString1};

    /**
     * Create mock qrCode for user to delete
     */
    @BeforeClass
    public static void setUpDependencies() {
        //Enter username of testusee here:
        String testusee = "testusee";


        for (int i = 0; i < testHashStrings.length; i++) {

            //Futures
            CompletableFuture complete1 = new CompletableFuture<>();
            CompletableFuture complete2 = new CompletableFuture<>();

            QrCode fillerCode = new QrCode();

            // Mock relation in QrCodes collection
            Map<String, Object> mockQr = new HashMap<>();
            mockQr.put("binaryString", "hashString" + i);
            mockQr.put("codeName", "hashString" + i);
            mockQr.put("Score", fillerCode.setScore(testHashStrings[i]));

            // Mock relation in scannedBy collection
            Map<String, Object> mockRelation = new HashMap<>();
            DocumentReference qrRef = db.collection("QrCodes")
                    .document(testHashStrings[i]);
            DocumentReference playerRef = db.collection("Players").document(testusee);
            mockRelation.put("Player", playerRef);
            mockRelation.put("qrCodeScanned", qrRef);

            // Add to database
            db.collection("QrCodes")
                    .document(testHashStrings[i])
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

            complete2.join();
            complete1.join();
        }

    }

    @After
    public void cleanup() throws InterruptedException {
        ArrayList<CompletableFuture> features = new ArrayList<>();


        for (int i = 0; i < testHashStrings.length; i++) {

            CompletableFuture completeDelete1 = new CompletableFuture();

            DocumentReference qrRef = db.collection("QrCodes")
                    .document(testHashStrings[i]);
            DocumentReference playerRef = db.collection("Players").document("testusee");

            db.collection("QrCodes").document(testHashStrings[i]).delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Docs", "Document successfully deleted");
                        } else {
                            Log.d("Docs", "Error deleting document: " + task.getException());
                        }
                        completeDelete1.complete(null);
                    });


            completeDelete1.join();
        }

        DocumentReference playerRef = db.collection("Players").document("testusee");

        CompletableFuture completeDelete2 = new CompletableFuture();
        db.collection("scannedBy")
                .whereEqualTo("Player", playerRef)
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

        completeDelete2.join();

        CompletableFuture completeDelete3 = new CompletableFuture();

        db.collection("Players").document("testusee").delete()
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


    @Test
    public void testCodesDisplayed() {
        assertTrue(solo.waitForView(R.id.qr_code_lister));
        ListView listView = (ListView) solo.getView(R.id.qr_code_lister);

        // Store the initial count of items in the list
        int initialItemCount = listView.getAdapter().getCount();

        // Wait for the ListView to be displayed
        Espresso.onView(ViewMatchers.withId(R.id.qr_code_lister)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Wait for the individual items to be displayed
        for (int i = 0; i < initialItemCount; i++) {
            Espresso.onData(Matchers.anything())
                    .inAdapterView(ViewMatchers.withId(R.id.qr_code_lister))
                    .atPosition(i)
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }

        // Iterate through the list items
        for (int i = 0; i < initialItemCount; i++) {
            QrCode filler = new QrCode();

            View listItemView = listView.getChildAt(i);
            TextView binaryStringTextView = listItemView.findViewById(R.id.qr_code_visualRep);
            TextView scoreTextView = listItemView.findViewById(R.id.points_text_view);


            String scoreText = scoreTextView.getText().toString();
            int score = Integer.parseInt(scoreText.replaceAll("[^\\d]", ""));


            //Verify Tag
            qrCodeTag currentTag = (qrCodeTag) listItemView.getTag();

            assertEquals(score, currentTag.score);
            assertEquals(testHashStrings[i], currentTag.hashString);
            if (i != initialItemCount-1) {
                assertEquals(filler.setScore(testHashStrings[i+1]), currentTag.nextScore);
            }

            // Verify the correct visual rep
            assertEquals(binaryStringTextView.getText().toString(), filler.getVisualRep("hashString" + i));

            //Verify the correct score
            assertEquals(filler.setScore(testHashStrings[i]), score);

            //Verify the highest or lowest score text is being displayed correctly
            if (i == 0 || i == initialItemCount - 1) {
                TextView highestTextView1 = listItemView.findViewById(R.id.highest_lowest_code);
                assertTrue(highestTextView1.getVisibility() == View.VISIBLE);
            } else {
                TextView highestTextView1 = listItemView.findViewById(R.id.highest_lowest_code);
                assertTrue(highestTextView1.getVisibility() == View.INVISIBLE);
            }
        }

        // Delete first item
        solo.clickOnView(solo2.getView(R.id.garbage_can_icon));
        assertTrue(solo.waitForView(R.id.delete_qrcode_list));
        solo.clickOnCheckBox(0);
        solo.clickOnButton("DELETE SELECTED CODES");

        // Wait for the list to update
        assertTrue(solo.waitForView(R.id.qr_code_lister));

        // Check that the item has been removed from the list
        assertEquals(initialItemCount - 1, listView.getAdapter().getCount());

        // Wait for the individual items to be displayed
        for (int i = 0; i < initialItemCount - 1; i++) {
            Espresso.onData(Matchers.anything())
                    .inAdapterView(ViewMatchers.withId(R.id.qr_code_lister))
                    .atPosition(i)
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }


        listView = (ListView) solo.getView(R.id.qr_code_lister);
        // Iterate through the remaining list items
        for (int i = 1; i <= 2; i++) {
            QrCode filler = new QrCode();

            View listItemView = listView.getChildAt(i-1);
            TextView binaryStringTextView = listItemView.findViewById(R.id.qr_code_visualRep);
            TextView scoreTextView = listItemView.findViewById(R.id.points_text_view);

            String scoreText = scoreTextView.getText().toString();
            int score = Integer.parseInt(scoreText.replaceAll("[^\\d]", ""));

            // Verify the correct visual rep
            assertEquals(binaryStringTextView.getText().toString(), filler.getVisualRep("hashString" + i));

            // Verify the correct score
            assertEquals(filler.setScore(testHashStrings[i]), score);

            // Verify the highest or lowest score text is being displayed correctly
            TextView highestTextView1 = listItemView.findViewById(R.id.highest_lowest_code);
            assertTrue(highestTextView1.getVisibility() == View.VISIBLE);
        }
    }

}
