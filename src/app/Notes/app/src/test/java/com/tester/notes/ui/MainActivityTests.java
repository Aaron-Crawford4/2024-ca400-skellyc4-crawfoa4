package com.tester.notes.ui;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

import android.app.Dialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.tester.notes.R;
import com.tester.notes.activities.CreateNoteActivity;
import com.tester.notes.activities.LoginActivity;
import com.tester.notes.activities.MainActivity;
import com.tester.notes.adapters.NotesAdapter;
import com.tester.notes.adapters.UserAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTests {
    MainActivity activity;
    String testUsername = "TestUser";
    @Before
    public void setUp(){
        Intent intent = new Intent();
        intent.putExtra("username", testUsername);
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class, intent)){
            controller.setup();
            activity = controller.get();
        }
    }
    @Test
    public void notesRecyclerViewTests(){
        RecyclerView notesRecyclerView = activity.findViewById(R.id.notesRecyclerView);
        assertNotNull(notesRecyclerView);
        assertNotNull(notesRecyclerView.getAdapter());
        assertEquals(NotesAdapter.class, notesRecyclerView.getAdapter().getClass());
        assertNotNull(notesRecyclerView.getLayoutManager());
        assertEquals(StaggeredGridLayoutManager.class, notesRecyclerView.getLayoutManager().getClass());
        assertEquals(View.VISIBLE, notesRecyclerView.getVisibility());
    }
    @Test
    public void deletedNotesRecyclerViewTests(){
        RecyclerView deletedNotesRecyclerView = activity.findViewById(R.id.deletedNotesRecyclerView);
        assertNotNull(deletedNotesRecyclerView);
        assertNotNull(deletedNotesRecyclerView.getLayoutManager());
        assertEquals(StaggeredGridLayoutManager.class, deletedNotesRecyclerView.getLayoutManager().getClass());
        assertEquals(View.GONE, deletedNotesRecyclerView.getVisibility());
    }
    @Test
    public void switchShowDeletedTests(){
        MaterialSwitch switchShowDeleted = activity.findViewById(R.id.switchShowDeleted);
        RecyclerView notesRecyclerView = activity.findViewById(R.id.notesRecyclerView);
        RecyclerView deletedNotesRecyclerView = activity.findViewById(R.id.deletedNotesRecyclerView);

        assertFalse(switchShowDeleted.isChecked());
        assertEquals(View.VISIBLE, notesRecyclerView.getVisibility());
        assertEquals(View.GONE, deletedNotesRecyclerView.getVisibility());

        switchShowDeleted.performClick();
        assertTrue(switchShowDeleted.isChecked());
        assertEquals(View.GONE, notesRecyclerView.getVisibility());
        assertEquals(View.VISIBLE, deletedNotesRecyclerView.getVisibility());

        switchShowDeleted.performClick();
        assertFalse(switchShowDeleted.isChecked());
        assertEquals(View.VISIBLE, notesRecyclerView.getVisibility());
        assertEquals(View.GONE, deletedNotesRecyclerView.getVisibility());
    }
    @Test
    public void inputSearchTests(){
        EditText inputSearchTerm = activity.findViewById(R.id.inputSearch);
        String expectedResult = activity.getString(R.string.search_notes);
        assertThat(inputSearchTerm.getHint().toString(), equalTo(expectedResult));

        String testSearchTerm = "test Search term";
        inputSearchTerm.setText(testSearchTerm);
        assertEquals(testSearchTerm, inputSearchTerm.getText().toString());

        assertThat(View.VISIBLE, equalTo(inputSearchTerm.getVisibility()));
    }
    @Test
    public void imageAddNoteTests(){
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)){
            controller.setup();
            activity = controller.get();

            activity.findViewById(R.id.imageAddNote).performClick();
            Intent expectedIntent = new Intent(activity, CreateNoteActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
    @Test
    public void createUserDialogTests(){
        activity.findViewById(R.id.imageCollaborators).performClick();
        Dialog dialog = ShadowDialog.getLatestDialog();
        assertTrue(dialog.isShowing());
        assertNotNull(dialog.findViewById(R.id.layoutUserList));

        EditText inputUser = dialog.findViewById(R.id.inputUser);
        String expectedResult = activity.getString(R.string.username);
        assertThat(inputUser.getHint().toString(), equalTo(expectedResult));
        String testUsername = "TestUsername";
        inputUser.setText(testUsername);
        assertEquals(testUsername, inputUser.getText().toString());
        assertThat(View.VISIBLE, equalTo(inputUser.getVisibility()));

        ImageView imageAddCollaborator = dialog.findViewById(R.id.imageAddCollaborator);
        assertTrue(imageAddCollaborator.isClickable());
        assertEquals(View.VISIBLE, imageAddCollaborator.getVisibility());

        RecyclerView userRecyclerView = dialog.findViewById(R.id.userRecyclerView);
        assertNotNull(userRecyclerView);
        assertNotNull(userRecyclerView.getAdapter());
        assertEquals(UserAdapter.class, userRecyclerView.getAdapter().getClass());
        assertNotNull(userRecyclerView.getLayoutManager());
        assertEquals(StaggeredGridLayoutManager.class, userRecyclerView.getLayoutManager().getClass());

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        assertEquals(activity.getString(R.string.close), buttonCancel.getText());
        buttonCancel.performClick();
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
        assertEquals(R.id.nav_Welcome, navMenu.getMenu().getItem(0).getItemId());
        assertEquals(R.id.nav_Home, navMenu.getMenu().getItem(1).getItemId());
        assertEquals(R.id.nav_Owned, navMenu.getMenu().getItem(2).getItemId());
        assertEquals(R.id.nav_Shared, navMenu.getMenu().getItem(3).getItemId());
        assertEquals(R.id.nav_Logout, navMenu.getMenu().getItem(4).getItemId());

        // Welcome displays the logged in users username
        assertEquals(activity.getString(R.string.welcome) + " " + testUsername, navMenu.getMenu().getItem(0).getTitle());

        MenuItem nav_Home = navMenu.getMenu().getItem(1);
        MenuItem nav_Owned = navMenu.getMenu().getItem(2);
        MenuItem nav_Shared = navMenu.getMenu().getItem(3);
        MenuItem nav_Logout = navMenu.getMenu().getItem(4);

        // Only home is checked by default
        assertTrue(nav_Home.isChecked());
        assertFalse(nav_Owned.isChecked());
        assertFalse(nav_Shared.isChecked());
        assertFalse(nav_Logout.isChecked());

        // clicking on "Owned" changes the checked item to "Owned", unchecks everything else
        // And finishes the MainActivity
        activity.findViewById(R.id.nav_Owned).performClick();

        assertFalse(nav_Home.isChecked());
        assertTrue(nav_Owned.isChecked());
        assertFalse(nav_Shared.isChecked());
        assertFalse(nav_Logout.isChecked());
        assertTrue(activity.isFinishing());

        // clicking Logout finishes the MainActivity and returns to the login page
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)){
            controller.setup();
            activity = controller.get();

            activity.findViewById(R.id.nav_Logout).performClick();
            Intent expectedIntent = new Intent(activity, LoginActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
