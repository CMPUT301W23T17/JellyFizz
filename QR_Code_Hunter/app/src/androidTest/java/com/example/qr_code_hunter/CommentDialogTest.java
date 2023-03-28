package com.example.qr_code_hunter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.*;
import static org.hamcrest.Matchers.*;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class CommentDialogFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testCommentList() {
        // Set up the fragment with some comments
        ArrayList<CommentSection> comments = new ArrayList<>();
        comments.add(new CommentSection("User 1", "Comment 1"));
        comments.add(new CommentSection("User 2", "Comment 2"));
        CommentDialogFragment fragment = CommentDialogFragment.newInstance(comments, new ArrayList<>());
        activityRule.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commit();

        // Wait for the fragment to become visible
        onView(isRoot()).perform(waitFor(500));

        // Check that the comments are displayed
        onView(withText("User 1")).check(matches(isDisplayed()));
        onView(withText("Comment 1")).check(matches(isDisplayed()));
        onView(withText("User 2")).check(matches(isDisplayed()));
        onView(withText("Comment 2")).check(matches(isDisplayed()));
    }

    @Test
    public void testPlayerList() {
        // Set up the fragment with some players
        ArrayList<CommentSection> players = new ArrayList<>();
        players.add(new CommentSection("Player 1", null));
        players.add(new CommentSection("Player 2", null));
        CommentDialogFragment fragment = CommentDialogFragment.newInstance(new ArrayList<>(), players);
        activityRule.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commit();

        // Wait for the fragment to become visible
        onView(isRoot()).perform(waitFor(500));

        // Check that the players are displayed
        onView(withText("Player 1")).check(matches(isDisplayed()));
        onView(withText("Player 2")).check(matches(isDisplayed()));
        onView(withText("Comment 1")).check(doesNotExist());
        onView(withText("Comment 2")).check(doesNotExist());
    }

}
