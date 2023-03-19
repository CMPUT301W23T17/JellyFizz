package com.example.qr_code_hunter;
import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class loginIntentTest {
    private Solo solo;


    @Rule
    public ActivityTestRule<loginActivity> rule = new ActivityTestRule<>(loginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testLoginActivityDisplayed() throws Exception {
        // Verify that the loginActivity is displayed
        solo.assertCurrentActivity("Expected loginActivity activity", loginActivity.class);
    }


    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

    @Test
    public void testRegisterUser() throws Exception {
        // Enter valid user details
        solo.enterText(0, "testuser");
        solo.enterText(1, "testuser@example.com");
        solo.enterText(2, "1234567890");

        // Click register button
        solo.clickOnButton("Register");

        // Assert that user is redirected to homepage
        assertTrue(solo.waitForActivity(MainActivity.class));

        FirebaseFirestore tester = FirebaseFirestore.getInstance();
        CollectionReference playerRefs = tester.collection("Players");
    }

    @Test
    public void testExistingUsername() throws Exception {
        // Enter existing username
        solo.enterText(0, "KyleQuach");

        // Click register button
        solo.clickOnButton("Register");

        // Assert that error message is displayed
        assertTrue(solo.searchText(solo.getString(R.string.userNameTaken)));
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

