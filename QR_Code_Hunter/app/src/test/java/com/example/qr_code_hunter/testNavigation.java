import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NavigationBarTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testNavigation() {
        // Click on Map screen
        onView(withId(R.id.map_screen)).perform(click());
        onView(isAssignableFrom(MapFragment.class)).check(matches(isDisplayed()));

        // Click on Search screen
        onView(withId(R.id.search_screen)).perform(click());
        onView(isAssignableFrom(SearchFragment.class)).check(matches(isDisplayed()));

        // Click on Home screen
        onView(withId(R.id.home_screen)).perform(click());
        onView(isAssignableFrom(HomepageFragment.class)).check(matches(isDisplayed()));

        // Click on Ranking screen
        onView(withId(R.id.ranking_screen)).perform(click());
        onView(isAssignableFrom(RankingFragment.class)).check(matches(isDisplayed()));

        // Click on Player Profile screen
        onView(withId(R.id.player_profile_screen)).perform(click());
        onView(isAssignableFrom(PlayerProfileFragment.class)).check(matches(isDisplayed()));
    }
}
