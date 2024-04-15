package com.tester.notes.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.tester.notes.R;
import com.tester.notes.activities.ViewNote;
import com.tester.notes.entities.Note;
import com.tester.notes.entities.Repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.Base64;

@RunWith(RobolectricTestRunner.class)
public class ViewNoteTests {
    String testNoteContent = "some encoded text for test purposes";
    String testDate = "15/4/2024";
    String testTime = "01:21";
    ViewNote activity;
    @Before
    public void setUp(){
        byte[] encodedNoteContent = Base64.getEncoder().encode(testNoteContent.getBytes());
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewNote.class);
        intent.putExtra("repo", new Repository());
        intent.putExtra("note", new Note(new String(encodedNoteContent), "TesterNote", testDate, testTime));

        try (ActivityController<ViewNote> controller = Robolectric.buildActivity(ViewNote.class, intent)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void textNoteContentTests(){
        TextView textNoteContent = activity.findViewById(R.id.textNoteContent);
        assertThat(textNoteContent.getText().toString(), equalTo(testNoteContent));
    }
    @Test
    public void textDateTimeTests(){
        TextView textDateTime = activity.findViewById(R.id.textDateTime);
        assertThat(textDateTime.getText().toString(), equalTo(testDate + " " + testTime));
    }
    @Test
    public void imageBackTests(){
        ImageView imageBack = activity.findViewById(R.id.imageBack);
        assertThat(imageBack.getVisibility(), equalTo(View.VISIBLE));
        imageBack.performClick();
        assertTrue(activity.isFinishing());
    }
    @Test
    public void imageRestoreFileTests(){
        ImageView imageRestoreFile = activity.findViewById(R.id.imageRestoreFile);
        assertThat(imageRestoreFile.getVisibility(), equalTo(View.VISIBLE));
        assertTrue(imageRestoreFile.isClickable());
    }
}
