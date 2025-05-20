package com.example.drawingapp;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void drawingView_isDisplayed() {
        onView(withId(R.id.drawing_view_id)).check(matches(isDisplayed()));
    }

    @Test
    public void testDrawingInteraction() {
        // Check if DrawingView is displayed
        onView(withId(R.id.drawing_view_id)).check(matches(isDisplayed()));

        // Perform a swipe gesture
        onView(withId(R.id.drawing_view_id)).perform(ViewActions.swipeRight());
        
        // At this point, a path should have been drawn.
        // We can't directly verify the canvas content with Espresso easily.
        // However, if the app doesn't crash and the view is still displayed,
        // it's a basic indication that the touch event was processed.
        onView(withId(R.id.drawing_view_id)).check(matches(isDisplayed()));
    }
}
