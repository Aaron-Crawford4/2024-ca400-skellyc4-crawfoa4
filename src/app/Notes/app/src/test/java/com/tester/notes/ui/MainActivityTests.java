package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.tester.notes.R;
import com.tester.notes.activities.CreateNoteActivity;
import com.tester.notes.activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTests {
    @Test
    public void buttonLaunchesCreateNoteActivity(){
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)){
            controller.setup();
            MainActivity activity = controller.get();

            activity.findViewById(R.id.imageAddNote).performClick();
            Intent expectedIntent = new Intent(activity, CreateNoteActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
