package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;


import android.app.Dialog;
import android.content.Intent;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.test.core.app.ApplicationProvider;


import com.tester.notes.R;
import com.tester.notes.activities.CreateNoteActivity;
import com.tester.notes.adapters.PrevVerNotesAdapter;
import com.tester.notes.entities.Note;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class ViewNoteActivityTests {
    CreateNoteActivity activity;
    @Before
    public void setUp(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", new Note("TesterNote", "2011-12-03T10:15:30+01:00"));
        try (ActivityController<CreateNoteActivity> controller = Robolectric.buildActivity(CreateNoteActivity.class, intent)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void inputNoteTitleTests(){
        EditText inputNoteTitle = activity.findViewById(R.id.inputNoteTitle);
        String expectedResult = activity.getString(R.string.note_title);
        assertThat(inputNoteTitle.getHint().toString(), equalTo(expectedResult));
        assertThat(inputNoteTitle.getText().toString(), equalTo("TesterNote"));
        assertThat(inputNoteTitle.getInputType(), equalTo(InputType.TYPE_NULL));
        assertThat(View.VISIBLE, equalTo(inputNoteTitle.getVisibility()));
    }
    @Test
    public void textDateTimeTests(){
        TextView textDateTime = activity.findViewById(R.id.textDateTime);
        String expectedResult = "Saturday, 03 December 2011 10:15 AM";
        assertThat(textDateTime.getText().toString(), equalTo(expectedResult));
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
        assertThat(imageFileHistory.getVisibility(), equalTo(View.VISIBLE));
        imageFileHistory.performClick();

        Dialog dialog = ShadowDialog.getLatestDialog();
        TestCase.assertTrue(dialog.isShowing());
        assertNotNull(dialog.findViewById(R.id.layoutFileHistory));

        RecyclerView fileHistoryRecyclerView = dialog.findViewById(R.id.fileHistoryRecyclerView);
        assertNotNull(fileHistoryRecyclerView);
        assertNotNull(fileHistoryRecyclerView.getAdapter());
        assertEquals(PrevVerNotesAdapter.class, fileHistoryRecyclerView.getAdapter().getClass());
        assertNotNull(fileHistoryRecyclerView.getLayoutManager());
        assertEquals(StaggeredGridLayoutManager.class, fileHistoryRecyclerView.getLayoutManager().getClass());

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        assertEquals(activity.getString(R.string.close), buttonCancel.getText());
        buttonCancel.performClick();
        ShadowLooper.runUiThreadTasks();
        TestCase.assertFalse(dialog.isShowing());
    }
    @Test
    public void imageDeleteNoteTests(){
        ImageView imageDeleteNote = activity.findViewById(R.id.imageDeleteNote);
        assertThat(imageDeleteNote.getVisibility(), equalTo(View.VISIBLE));
        imageDeleteNote.performClick();

        Dialog dialog = ShadowDialog.getLatestDialog();
        TestCase.assertTrue(dialog.isShowing());
        assertNotNull(dialog.findViewById(R.id.layoutDeleteNote));

        TextView textDeleteNoteConfirm = dialog.findViewById(R.id.textDeleteNoteConfirm);
        assertEquals(activity.getString(R.string.delete_note), textDeleteNoteConfirm.getText());
        assertTrue(textDeleteNoteConfirm.isClickable());

        TextView textCancel = dialog.findViewById(R.id.textCancel);
        assertEquals(activity.getString(R.string.cancel), textCancel.getText());
        textCancel.performClick();
        ShadowLooper.runUiThreadTasks();
        TestCase.assertFalse(dialog.isShowing());
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
