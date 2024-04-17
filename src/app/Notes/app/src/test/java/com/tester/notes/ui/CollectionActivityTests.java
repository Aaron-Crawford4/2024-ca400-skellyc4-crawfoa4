package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.Dialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.navigation.NavigationView;
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
    public void NavbarTests(){
        ImageView imageDrawerToggle = activity.findViewById(R.id.imageDrawerToggle);
        NavigationView navMenu = activity.findViewById(R.id.navMenu);
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

        // Nav drawer is closed by default
        assertFalse(drawerLayout.isDrawerOpen(GravityCompat.START));

        // clicking the menu toggle opens the nav drawer
        imageDrawerToggle.performClick();
        assertTrue(drawerLayout.isDrawerOpen(GravityCompat.START));

        // The nav menu displays the correct items
        assertEquals(R.id.nav_Home, navMenu.getMenu().getItem(0).getItemId());
        assertEquals(R.id.nav_Owned, navMenu.getMenu().getItem(1).getItemId());
        assertEquals(R.id.nav_Shared, navMenu.getMenu().getItem(2).getItemId());
        assertEquals(R.id.nav_Logout, navMenu.getMenu().getItem(3).getItemId());

        MenuItem nav_Home = navMenu.getMenu().getItem(0);
        MenuItem nav_Owned = navMenu.getMenu().getItem(1);
        MenuItem nav_Shared = navMenu.getMenu().getItem(2);
        MenuItem nav_Logout = navMenu.getMenu().getItem(3);

        // Only home is checked by default, because we show all collections
        assertTrue(nav_Home.isChecked());
        assertFalse(nav_Owned.isChecked());
        assertFalse(nav_Shared.isChecked());
        assertFalse(nav_Logout.isChecked());

        // clicking on "Owned" changes the checked item to "Owned" and unchecks everything else
        activity.findViewById(R.id.nav_Owned).performClick();

        assertFalse(nav_Home.isChecked());
        assertTrue(nav_Owned.isChecked());
        assertFalse(nav_Shared.isChecked());
        assertFalse(nav_Logout.isChecked());

        // clicking Logout finishes the CollectionsActivity and returns to the login page
        activity.findViewById(R.id.nav_Logout).performClick();
        assertTrue(activity.isFinishing());
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
