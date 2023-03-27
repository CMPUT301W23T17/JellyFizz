import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.qr_code_hunter.MainActivity;
import com.example.qr_code_hunter.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CodeDetailsFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testShowCommentButton() {
        Espresso.onView(ViewMatchers.withId(R.id.comment_dialog_btn)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.comment_dialog_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testBackButton() {
        Espresso.onView(ViewMatchers.withId(R.id.details_backBtn)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.player_list_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

}
