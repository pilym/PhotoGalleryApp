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
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestDateFilter() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText("27/09/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("27/09/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 2; i++) {
            onView(withId(R.id.photoTimestamp)).check(matches(withText(containsString("Thu Sep 27"))));
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestDateFilterNoPhotosFound() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText("01/01/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("25/01/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 2; i++) {
            onView(withId(R.id.photoTimestamp)).check(matches(withText("")));
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestDateFilterOverManyDays() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText("25/09/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("30/09/2018"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 2; i++) {
            onView(withId(R.id.photoTimestamp)).check(matches(withText(containsString("Sep 25"))));
            onView(withId(R.id.btnRight)).perform(click());
        }
        for (int i = 0; i <= 2; i++) {
            onView(withId(R.id.photoTimestamp)).check(matches(withText(containsString("Sep 26"))));
            onView(withId(R.id.btnRight)).perform(click());
        }
        for (int i = 0; i <= 1; i++) {
            onView(withId(R.id.photoTimestamp)).check(matches(withText(containsString("Sep 30"))));
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
        String testCaption3 = "tv broke...";
        String testCaption4 = "asdfg";

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

        // enter caption for photo
        onView(withId(R.id.btnLeft)).perform(click());
        onView(withId(R.id.etPhotoCaption)).perform(replaceText(testCaption3), closeSoftKeyboard());

        // go to another photo
        onView(withId(R.id.btnRight)).perform(click());

        // enter caption for other photo
        onView(withId(R.id.etPhotoCaption)).perform(replaceText(testCaption4), closeSoftKeyboard());

        // go back to first photo and check if caption is saved
        onView(withId(R.id.btnLeft)).perform(click());
        onView(withId(R.id.etPhotoCaption)).check(matches(withText(testCaption3)));

        // go to other photo and check if caption is saved
        onView(withId(R.id.btnRight)).perform(click());
        onView(withId(R.id.etPhotoCaption)).check(matches(withText(testCaption4)));
    }
}
