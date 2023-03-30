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

    @AfterClass
    public static void cleanup() throws InterruptedException {
        CompletableFuture completeDelete1 = new CompletableFuture();

        String testPlayer = "testuser";

        db.collection("Players").document(testPlayer).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Document successfully deleted");
                    } else {
                        System.out.println("Error deleting document: " + task.getException());
                    }
                    completeDelete1.complete(null);
                });

        completeDelete1.join();
    }


    @Test
    public void testLoginActivityDisplayed() throws Exception {
        // Verify that the loginActivity is displayed
        solo.assertCurrentActivity("Expected loginActivity activity", loginActivity.class);
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

        //Make sure MainActivity is being displayed
        assertTrue(solo.waitForView(R.id.MainActivityHolder));
    }

    @Test
    public void testUsernameVerification() throws Exception {
        EditText editText = solo.getEditText(0);

        // Enter existing username
        String username = "testuser";
        String email = "testuser@example.com";
        String phone = "1234567890";
        solo.enterText(0, username);
        solo.enterText(1, email);
        solo.enterText(2, phone);

        String[] naughtyStrings = {
                "drop table users", // attempts SQL injection
                "<script>alert('xss')</script>", // attempts cross-site scripting
                "admin'--", // attempts SQL injection via comment
                "testuser@test.com\ncc: spammer@example.com", // attempts email header injection
                "testuser@[127.0.0.1]", // attempts email header injection
                "testuser@example.com<b>", // attempts HTML injection
                "testuser\"", // attempts SQL injection via double quotes
                "testuser\\", // attempts SQL injection via backslash
                "téstüsér", // uses non-ASCII characters
                "", // empty string
                "testuser", // already taken usernamel, test duplicate username
                "ddddddddddddddddddddddddddddddd", // too long username
                "dasd../gh" // invalid characters in username
        };

        for (String naughtyString : naughtyStrings) {
            solo.clearEditText(editText);
            solo.enterText(0, naughtyString);
            solo.clickOnButton("Register");

            if (naughtyString.equals("testuser")) {
                assertTrue(solo.searchText(solo.getString(R.string.userNameTaken)));
            } else if (naughtyString.length() > 13 || naughtyString.length() < 1 ) {
                assertTrue(solo.searchText(solo.getString(R.string.userNameLength)));
            } else if (naughtyString.matches("^[a-zA-Z0-9]*$")) {
                assertFalse(solo.searchText(solo.getString(R.string.userNameInvalidCharacters)));
            } else {
                assertTrue(solo.searchText(solo.getString(R.string.userNameInvalidCharacters)));
            }
        }
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
        solo.enterText(2, "1234");

        // Click register button
        solo.clickOnButton("Register");

        // Assert that error message is displayed
        assertTrue(solo.searchText(solo.getString(R.string.invalidNumber)));
    }

}

