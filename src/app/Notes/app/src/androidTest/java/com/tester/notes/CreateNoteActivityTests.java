package com.tester.notes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tester.notes.activities.CreateNoteActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class CreateNoteActivityTests {
    private final String testText = "Testing testing 123";

    @Rule
    public ActivityScenarioRule<CreateNoteActivity> activityRule =
            new ActivityScenarioRule<>(CreateNoteActivity.class);
    @Test
    public void noteTitleInput(){
        onView(withId(R.id.inputNoteTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.inputNoteTitle)).check(matches(isClickable()));
        onView(withId(R.id.inputNoteTitle)).perform(click()).check(matches(withHint("Note Title")));
        onView(withId(R.id.inputNoteTitle)).perform(typeText(testText)).check(matches(withText(testText)));
    }

    @Test
    public void noteTextInput() {
        onView(withId(R.id.inputNoteText)).check(matches(isDisplayed()));
        onView(withId(R.id.inputNoteText)).check(matches(isClickable()));
        onView(withId(R.id.inputNoteText)).perform(click()).check(matches(withHint("Type Note Here…")));
        onView(withId(R.id.inputNoteText)).perform(typeText(testText)).check(matches(withText(testText)));
    }
}
