package com.example.paul.photogalleryapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestFilter() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText("31/01/18"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("01/01/18"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestCaptionSaving() {
        String testCaption = "test caption";
        // enter caption for photo
        onView(withId(R.id.etPhotoCaption)).perform(replaceText(testCaption), closeSoftKeyboard());

        // go to another photo then go back
        onView(withId(R.id.btnRight)).perform(click());
        onView(withId(R.id.btnLeft)).perform(click());

        // check if caption is saved
        onView(withId(R.id.etPhotoCaption)).check(matches(withText(testCaption)));
    }

    @Test
    public void TestCaptionSavingMultiplePhotos() {
        String testCaption1 = "my dream house";
        String testCaption2 = "test caption";

        // enter caption for photo
        onView(withId(R.id.etPhotoCaption)).perform(replaceText(testCaption1), closeSoftKeyboard());

        // go to another photo
        onView(withId(R.id.btnRight)).perform(click());

        // enter caption for other photo
        onView(withId(R.id.etPhotoCaption)).perform(replaceText(testCaption2), closeSoftKeyboard());

        // go back to first photo and check if caption is saved
        onView(withId(R.id.btnLeft)).perform(click());
        onView(withId(R.id.etPhotoCaption)).check(matches(withText(testCaption1)));

        // go to other photo and check if caption is saved
        onView(withId(R.id.btnRight)).perform(click());
        onView(withId(R.id.etPhotoCaption)).check(matches(withText(testCaption2)));
    }
}
