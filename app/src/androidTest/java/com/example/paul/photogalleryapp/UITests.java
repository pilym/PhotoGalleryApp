package com.example.paul.photogalleryapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
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
    private String testCaption1;
    private String testCaption2;
    private String testCaption3;
    private String testCaption4;

    private String testDate1;
    private String testDate2;
    private String testDate3;
    private String testDate4;
    private String testDateDisplay1;
    private String testDateDisplay2;
    private String testDateDisplay3;
    private String testDateDisplay4;

    private String testLat1;
    private String testLat2;
    private String testLat3;
    private String testLat4;
    private String testLong1;
    private String testLong2;
    private String testLong3;
    private String testLong4;
    private String testLatDisplay1;
    private String testLongDisplay1;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initValidTestCaption() {
        testCaption1 = "my dream house";
        testCaption2 = "test caption";
        testCaption3 = "tv broke...";
        testCaption4 = "asdfg";
    }

    @Before
    public void initValidTestDate() {
        testDate1 = "27/09/2018";
        testDate2 = "01/01/2018";
        testDate3 = "25/01/2018";
        testDate4 = "30/09/2018";
        testDateDisplay1 = "Sep 25";
        testDateDisplay2 = "Sep 26";
        testDateDisplay3 = "Sep 30";
    }

    @Before
    public void initValidTestLocation() {
        // latitude: from top to bottom
        testLat1 = "50.0";
        testLat2 = "20.0";
        testLat3 = "-30.0";
        testLat4 = "-40.0";

        // longitude: from left to right
        testLong1 = "-160.0";
        testLong2 = "-150.0";
        testLong3 = "20.0";
        testLong4 = "60.0";
        testLatDisplay1 = "30.4";
        testLongDisplay1 = "-152.08";

    }

    @Test
    public void TestDateFilter() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText(testDate1), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText(testDate1), closeSoftKeyboard());
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
        // 2 pics from Sept 25, 2 from 26, 1 from 30
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
    public void TestLocationFilter() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_topLeftLat)).perform(typeText(testLat1), closeSoftKeyboard());
        onView(withId(R.id.search_topLeftLong)).perform(typeText(testLong1), closeSoftKeyboard());
        onView(withId(R.id.search_topLeftLong)).perform(typeText(testLat3), closeSoftKeyboard());
        onView(withId(R.id.search_bottomRightLong)).perform(typeText(testLong4), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        onView(withId(R.id.photoLocation)).check(matches(withText(containsString(testLatDisplay1))));
        onView(withId(R.id.photoLocation)).check(matches(withText(containsString(testLongDisplay1))));
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
