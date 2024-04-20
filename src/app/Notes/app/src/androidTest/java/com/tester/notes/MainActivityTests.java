package com.tester.notes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tester.notes.activities.CreateNoteActivity;
import com.tester.notes.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Test: open a new note and press back without saving.
     */
    @Test
    public void closeWithoutSaving(){
        // open a new note
        Intents.init();
        onView(withId(R.id.imageAddNote)).perform(click());
        intended(hasComponent(CreateNoteActivity.class.getName()));
        Intents.release();

        // return to main page
        onView(withId(R.id.imageBack)).perform(click());

        // check we are back on the main page
        onView(withId(R.id.textMyNotes)).check(matches(isDisplayed()));
    }
    /**
     * Test: open a new note, input some text, input a title and then save and return to main activity.
     */
    @Test
    public void createNewNote(){
        String sampleNoteTitle = "Lorem ipsum";
        String sampleNoteText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

        // open a new note
        Intents.init();
        onView(withId(R.id.imageAddNote)).perform(click());
        intended(hasComponent(CreateNoteActivity.class.getName()));
        Intents.release();

        // input a note title and some text
        onView(withId(R.id.inputNoteTitle)).perform(typeText(sampleNoteTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.inputNoteText)).perform(typeText(sampleNoteText)).check(matches(isDisplayed()));

        // save note and return to main page
        onView(withId(R.id.imageDone)).perform(click());
        onView(withId(R.id.textMyNotes)).check(matches(isDisplayed()));

        // check new note is displayed
        onView(withText(sampleNoteTitle)).check(matches(isDisplayed()));

        // check note is clickable and displays the saved title and text
        onView(withId(R.id.notesRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText(sampleNoteText)).check(matches(isDisplayed()));
        onView(withText(sampleNoteTitle)).check(matches(isDisplayed()));
    }
}
