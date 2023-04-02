package com.example.qr_code_hunter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

/**
 * Test class for logInActivity. All the UI tests are written here. Robotium test framework is used
 * * CHATGPT was referenced
 */
@RunWith(AndroidJUnit4.class)
public class loginIntentTest {
    private Solo solo;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception if error occurs during testgit
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }


    /**
     * Runs after every test method to clean up any remaining resources and finish any open activities.
     * This method calls {@link Solo#finishOpenedActivities()} to finish any open activities.
     *
     * @throws Exception if an error occurs while cleaning up the resources.
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    /**
     * This method deletes the test user data from the Firestore database after all tests have completed.
     *
     * @throws InterruptedException if the CompletableFuture is interrupted while waiting for completion
     */
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


    /**
     * This method tests if the LoginActivity is displayed on app launch.
     *
     * @throws Exception if there is an error during testing
     */
    @Test
    public void testLoginActivityDisplayed() throws Exception {
        // Verify that the loginActivity is displayed
        solo.assertCurrentActivity("Expected loginActivity activity", LoginActivity.class);
    }

    /**
     * This method tests the user registration functionality.
     *
     * @throws Exception if there is an error during testing
     */
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


    /**
     * Tests that the entered username is verified properly.
     * <p>
     * This method enters various invalid username strings and asserts that the corresponding error messages are displayed.
     *
     * @throws Exception if an error occurs during the test
     */
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
            } else if (naughtyString.length() > 13 || naughtyString.length() < 1) {
                assertTrue(solo.searchText(solo.getString(R.string.userNameLength)));
            } else if (naughtyString.matches("^[a-zA-Z0-9]*$")) {
                assertFalse(solo.searchText(solo.getString(R.string.userNameInvalidCharacters)));
            } else {
                assertTrue(solo.searchText(solo.getString(R.string.userNameInvalidCharacters)));
            }
        }
    }

    /**
     * Tests the scenario where the user enters an invalid email during registration
     * <p>
     * The test enters an invalid email address, clicks the Register button, and asserts that the expected error message is displayed.
     *
     * @throws Exception if an error occurs during the test.
     */
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

    /**
     * Tests the scenario where the user enters an invalid phone number during registration
     * <p>
     * The test enters an invalid phone number, clicks the Register button, and asserts that the expected error message is displayed.
     *
     * @throws Exception if an error occurs during the test.
     */
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

