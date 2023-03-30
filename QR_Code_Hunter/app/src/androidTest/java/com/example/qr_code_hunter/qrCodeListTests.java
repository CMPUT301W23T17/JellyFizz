package com.example.qr_code_hunter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Test class for logInActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class qrCodeListTests {
    private Solo solo;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<loginActivity> rule = new ActivityTestRule<>(loginActivity.class, true, true);

    @BeforeClass
    public static void setUpDependencies() {
        CompletableFuture complete1 = new CompletableFuture<>();

        //This will be the test user for the tests
        String testUser = "testUser";

        Map<String, Object> currentPlayer = new HashMap<>();
        currentPlayer.put("email", "...");

        // Check if player document exists
        db.collection("Players").document(testUser).set(currentPlayer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                complete1.complete(null);
            }
        });

        complete1.join();
    }


}
