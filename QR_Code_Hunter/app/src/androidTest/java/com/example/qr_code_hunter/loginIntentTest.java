package com.example.qr_code_hunter;

import android.app.Activity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Test class for logInActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class loginIntentTest {
    private Solo solo;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<loginActivity> rule = new ActivityTestRule<>(loginActivity.class, true, true);

    @BeforeClass
    public static void setUpDependencies() {
        CompletableFuture complete1 = new CompletableFuture<>();

        Map<String, Object> currentPlayer = new HashMap<>();
        currentPlayer.put("email", "...");

        // Check if player document exists
        db.collection("Players").document("...").set(currentPlayer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                complete1.complete(null);
            }
        });

        complete1.join();
    }
    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }




    //Runs after every Method
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    @Test
    public void testLoginActivityDisplayed() throws Exception {
        // Verify that the loginActivity is displayed
        solo.assertCurrentActivity("Expected loginActivity activity", loginActivity.class);
    }

    @After
    public void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();
        CompletableFuture completeDelete2 = new CompletableFuture();


        db.collection("Players").document("...").delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });

        db.collection("Players").document("testuser").delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete2.complete(null);
                });

        completeDelete1.allOf(completeDelete1, completeDelete2).join();
    }


    @Test
    public void testRegisterUser() throws Exception {
        // Enter valid user details
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


        // Verify that the username has been saved on the user's phone
        String accountCreatedKey = solo.getCurrentActivity().getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(solo.getCurrentActivity());
        String savedUsername = prefs.getString(accountCreatedKey, "");
        assertEquals(username, savedUsername);
    }

    @Test
    public void testExistingUsername() throws Exception {
        EditText editText = solo.getEditText(0);

        // Enter existing username
        String username = "...";
        String email = "testuser@example.com";
        String phone = "1234567890";
        solo.enterText(0, username);
        solo.enterText(1, email);
        solo.enterText(2, phone);

        // Assert that error message is displayed
        assertTrue(solo.searchText(solo.getString(R.string.userNameTaken)));

        solo.clearEditText(editText);

        solo.enterText(0, "ddddddddddddddddddddddddddddddd");
        solo.clickOnButton("Register");
        assertTrue(solo.searchText(solo.getString(R.string.userNameLength)));

        solo.clearEditText(editText);

        solo.enterText(0, "dasd../gh");
        solo.clickOnButton("Register");
        assertTrue(solo.searchText(solo.getString(R.string.userNameInvalidCharacters)));
    }

    @Test
    public void testInvalidEmail() throws Exception {
        // Enter invalid email
        solo.enterText(0, "testuser");
        solo.enterText(1, "invalidemail");

        // Click register button
        solo.clickOnButton("Register");

        // Assert that error message is displayed
        assertTrue(solo.searchText(solo.getString(R.string.invalidEmail)));
    }

    @Test
    public void testInvalidPhoneNumber() throws Exception {
        // Enter invalid phone number
        solo.enterText(0, "testuser");
        solo.enterText(2, "notanumber");

        // Click register button
        solo.clickOnButton("Register");

        // Assert that error message is displayed
        assertTrue(solo.searchText(solo.getString(R.string.invalidNumber)));
    }

}

