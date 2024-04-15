package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tester.notes.R;
import com.tester.notes.activities.CollectionsActivity;
import com.tester.notes.adapters.RepoAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)

public class CollectionActivityTests {
    private CollectionsActivity activity;

    @Before
    public void setUp() {
        try (ActivityController<CollectionsActivity> controller = Robolectric.buildActivity(CollectionsActivity.class)) {
            controller.setup();
            activity = controller.get();
        }

    }
    @Test
    public void inputSearchTests(){
        EditText inputSearchTerm = activity.findViewById(R.id.inputSearch);
        String expectedResult = activity.getString(R.string.search_collections);
        assertThat(inputSearchTerm.getHint().toString(), equalTo(expectedResult));

        String testSearchTerm = "test Search term";
        inputSearchTerm.setText(testSearchTerm);
        assertEquals(testSearchTerm, inputSearchTerm.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputSearchTerm.getVisibility()));
    }
    @Test
    public void createRepoDialogTests(){
        activity.findViewById(R.id.imageAddCollection).performClick();
        Dialog dialog = ShadowDialog.getLatestDialog();
        assertTrue(dialog.isShowing());
        assertNotNull(dialog.findViewById(R.id.layoutCreateRepo));

        EditText inputCollectionName = dialog.findViewById(R.id.inputCollectionName);
        String expectedResult = activity.getString(R.string.collection_name);
        assertThat(inputCollectionName.getHint().toString(), equalTo(expectedResult));
        String testCollectionName = "Test_Collection";
        inputCollectionName.setText(testCollectionName);
        assertEquals(testCollectionName, inputCollectionName.getText().toString());
        assertThat(View.VISIBLE, equalTo(inputCollectionName.getVisibility()));


        Button buttonCreateCollection = dialog.findViewById(R.id.buttonCreateCollection);
        assertTrue(buttonCreateCollection.isClickable());
        String buttonExpectedText = activity.getString(R.string.create_collection);
        assertEquals(buttonExpectedText, buttonCreateCollection.getText().toString());
        assertEquals(View.VISIBLE, buttonCreateCollection.getVisibility());


        dialog.findViewById(R.id.buttonCancel).performClick();
        ShadowLooper.runUiThreadTasks();
        assertFalse(dialog.isShowing());
    }
    @Test
    public void filterButtonTests(){
        ImageView imageNoFilter = activity.findViewById(R.id.imageNoFilter);
        TextView textSharedFilter = activity.findViewById(R.id.textSharedFilter);
        TextView textOwnedFilter = activity.findViewById(R.id.textOwnedFilter);
        assertEquals(View.VISIBLE, imageNoFilter.getVisibility());
        assertEquals(View.GONE, textOwnedFilter.getVisibility());
        assertEquals(View.GONE, textSharedFilter.getVisibility());

        imageNoFilter.performClick();
        assertEquals(View.GONE, imageNoFilter.getVisibility());
        assertEquals(View.VISIBLE, textOwnedFilter.getVisibility());
        assertEquals(View.GONE, textSharedFilter.getVisibility());

        textOwnedFilter.performClick();
        assertEquals(View.GONE, imageNoFilter.getVisibility());
        assertEquals(View.GONE, textOwnedFilter.getVisibility());
        assertEquals(View.VISIBLE, textSharedFilter.getVisibility());

        textSharedFilter.performClick();
        assertEquals(View.VISIBLE, imageNoFilter.getVisibility());
        assertEquals(View.GONE, textOwnedFilter.getVisibility());
        assertEquals(View.GONE, textSharedFilter.getVisibility());
    }
    @Test
    public void repoRecyclerViewTests(){
        RecyclerView repoRecyclerView = activity.findViewById(R.id.repoRecyclerView);
        assertNotNull(repoRecyclerView);
        assertNotNull(repoRecyclerView.getAdapter());
        assertEquals(RepoAdapter.class, repoRecyclerView.getAdapter().getClass());
        assertNotNull(repoRecyclerView.getLayoutManager());
        assertEquals(StaggeredGridLayoutManager.class, repoRecyclerView.getLayoutManager().getClass());

    }
}
