package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tester.notes.R;
import com.tester.notes.activities.CreateNoteActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;


@RunWith(RobolectricTestRunner.class)
public class CreateNoteActivityTests {
    CreateNoteActivity activity;
    @Before
    public void setUp(){
        try (ActivityController<CreateNoteActivity> controller = Robolectric.buildActivity(CreateNoteActivity.class)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void inputNoteTitleTests(){
        EditText inputNoteTitle = activity.findViewById(R.id.inputNoteTitle);
        String expectedResult = activity.getString(R.string.note_title);
        assertThat(inputNoteTitle.getHint().toString(), equalTo(expectedResult));
        assertThat(inputNoteTitle.getText().toString(), equalTo(""));
        assertThat(inputNoteTitle.getInputType(), equalTo(InputType.TYPE_CLASS_TEXT));
        assertThat(View.VISIBLE, equalTo(inputNoteTitle.getVisibility()));
    }
    @Test
    public void textDateTimeTests(){
        TextView textDateTime = activity.findViewById(R.id.textDateTime);
        assertNotNull(textDateTime.getText().toString());
        assertThat(View.VISIBLE, equalTo(textDateTime.getVisibility()));
    }
    @Test
    public void inputNoteTextTests(){
        EditText inputNoteText = activity.findViewById(R.id.inputNoteText);
        String expectedResult = activity.getString(R.string.type_note_here);
        assertThat(inputNoteText.getHint().toString(), equalTo(expectedResult));

        String testNoteText = "test note text";
        inputNoteText.setText(testNoteText);
        assertEquals(testNoteText, inputNoteText.getText().toString());
        assertThat(View.VISIBLE, equalTo(inputNoteText.getVisibility()));
    }
    @Test
    public void imageFileHistoryTests(){
        ImageView imageFileHistory = activity.findViewById(R.id.imageFileHistory);
        assertThat(imageFileHistory.getVisibility(), equalTo(View.GONE));
        assertFalse(imageFileHistory.isClickable());
    }
    @Test
    public void imageDeleteNoteTests(){
        ImageView imageDeleteNote = activity.findViewById(R.id.imageDeleteNote);
        assertThat(imageDeleteNote.getVisibility(), equalTo(View.GONE));
        assertFalse(imageDeleteNote.isClickable());
    }
    @Test
    public void imagePreviewTests(){
        EditText inputNoteText = activity.findViewById(R.id.inputNoteText);
        TextView markdownNoteContent = activity.findViewById(R.id.markdownNoteContent);
        ImageView imagePreview = activity.findViewById(R.id.imagePreview);

        assertEquals(View.VISIBLE, inputNoteText.getVisibility());
        assertEquals(View.GONE, markdownNoteContent.getVisibility());

        imagePreview.performClick();

        assertEquals(View.GONE, inputNoteText.getVisibility());
        assertEquals(View.VISIBLE, markdownNoteContent.getVisibility());

        imagePreview.performClick();

        assertEquals(View.VISIBLE, inputNoteText.getVisibility());
        assertEquals(View.GONE, markdownNoteContent.getVisibility());
    }
    @Test
    public void imageBackTests(){
        ImageView imageBack = activity.findViewById(R.id.imageBack);
        assertThat(imageBack.getVisibility(), equalTo(View.VISIBLE));
        imageBack.performClick();
        assertTrue(activity.isFinishing());
    }
}
