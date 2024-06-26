package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.tester.notes.R;
import com.tester.notes.adapters.DeletedNotesAdapter;
import com.tester.notes.adapters.NotesAdapter;

import com.tester.notes.adapters.UserAdapter;

import com.tester.notes.entities.Repository;
import com.tester.notes.listeners.DeletedNotesListener;
import com.tester.notes.listeners.NoteDeleteListener;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.entities.Note;
import com.tester.notes.listeners.NotesListener;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements NotesListener, DeletedNotesListener, NoteDeleteListener {
    private RecyclerView notesRecyclerView, deletedNotesRecyclerView;
    private List<Note> noteList, deletedNoteList;
    private List<String> userList;
    private NotesAdapter notesAdapter;
    private DeletedNotesAdapter deletedNotesAdapter;
    private UserAdapter userAdapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private int noteClickedPosition = -1;
    private static Repository repo;
    private Dialog createUserDialog;
    public DrawerLayout drawerLayout;
    private String username;

    enum RequestType {
        ADD,
        UPDATE,
        VIEW
    }

    private final ActivityResultLauncher<Intent> addNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null && result.getData().getBooleanExtra("isViewOrUpdate", false)){
                        getNotes(result.getData().getBooleanExtra("isNoteDeleted", false));
                    }else {
                        getNotes(RequestType.ADD);
                    }
                    Log.println(Log.INFO, "Activity Result Logging", "Note changes saved to db successfully");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent prevIntent = getIntent();
        repo = (Repository) prevIntent.getSerializableExtra("repo");
        username = prevIntent.getStringExtra("username");

        ImageView imageAddNote = findViewById(R.id.imageAddNote);
        imageAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CreateNoteActivity.class);
            intent.putExtra("repo", repo);
            addNoteLauncher.launch(intent);
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        deletedNotesRecyclerView = findViewById(R.id.deletedNotesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        deletedNotesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );


        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(RequestType.VIEW);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable searchTerm) {
                if (noteList.size() != 0) notesAdapter.searchNotes(searchTerm.toString());
            }
        });
        ImageView imageCollaborators = findViewById(R.id.imageCollaborators);
        imageCollaborators.setOnClickListener(view -> createUserDialog());

        MaterialSwitch switchShowDeleted = findViewById(R.id.switchShowDeleted);
        TextView textMyNotes = findViewById(R.id.textMyNotes);
        switchShowDeleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                if (deletedNoteList == null) getDeletedNotes();
                textMyNotes.setText(R.string.deleted_files);
                notesRecyclerView.setVisibility(View.GONE);
                deletedNotesRecyclerView.setVisibility(View.VISIBLE);

            } else{
                textMyNotes.setText(R.string.notes);
                notesRecyclerView.setVisibility(View.VISIBLE);
                deletedNotesRecyclerView.setVisibility(View.GONE);
            }
        });
        setupDrawer();
    }
    @Override
    public void onNoteDeleteClicked(Note note, int position) {
        class DeleteNoteTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.deleteNote(note.getName(), repo.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Deleted note!", Toast.LENGTH_SHORT).show();
                            if (deletedNoteList != null) {
                                deletedNoteList.add(note);
                                deletedNotesAdapter.notifyItemInserted(0);
                            }
                            noteList.remove(position);
                            notesAdapter.notifyItemRemoved(position);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(MainActivity.this, "Failed to delete note!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        executorService.execute(new DeleteNoteTask());
    }
    private void updateNavigation(int itemId) {
        if (itemId == R.id.nav_Logout) {
            logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (itemId == R.id.nav_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra("navigationPressed", true);
            intent.putExtra("menuItemId", itemId);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void logout() {
        class LogoutTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.logout();
                try {
                    call.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        executorService.execute(new LogoutTask());
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView imageDrawerToggle = findViewById(R.id.imageDrawerToggle);
        imageDrawerToggle.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navView = drawerLayout.findViewById(R.id.navMenu);
        navView.bringToFront();

        Menu navMenu = navView.getMenu();
        navMenu.findItem(R.id.nav_Welcome).setTitle(getString(R.string.welcome) + " " + username);
        navMenu.setGroupCheckable(R.id.groupCollectionFilters, true, true);
        navMenu.setGroupDividerEnabled(true);

        navView.setCheckedItem(getIntent().getIntExtra("menuState", R.id.nav_Home));

        navView.setNavigationItemSelectedListener(item -> {
            if (item.isCheckable()) item.setChecked(true);
            updateNavigation(item.getItemId());
            return true;
        });
    }
    public static Repository getRepoDetails(){
        return repo;
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        intent.putExtra("repo", repo);
        addNoteLauncher.launch(intent);
    }

    @Override
    public void onDeletedNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        restoreFile();
    }
    private void restoreFile(){
        Note restoredFile = deletedNoteList.get(noteClickedPosition);
        class RestoreFileTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.restoreDeletedFile(restoredFile.getName(), repo.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()){
                            noteList.add(restoredFile);
                            deletedNoteList.remove(noteClickedPosition);
                            runOnUiThread(() -> {
                                notesAdapter.notifyItemInserted(0);
                                deletedNotesAdapter.notifyItemRemoved(noteClickedPosition);
                                Toast.makeText(MainActivity.this, "Restored " + restoredFile.getName(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to restore " + restoredFile.getName(), t);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to restore " + restoredFile.getName(), Toast.LENGTH_SHORT).show());
                    }
                });
            }
        }
        executorService.execute(new RestoreFileTask());
    }

    private void getDeletedNotes(){
        class GetDeletedNotesTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<List<List<String>>> call = client.getAllNotes("deletedFiles", repo.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<String>>> call, @NonNull Response<List<List<String>>> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            deletedNoteList = new ArrayList<>();
                            List<List<String>> responseData = response.body();
                            for (List<String> noteDetails : responseData) {
                                if (!noteDetails.isEmpty()) {
                                    Note note = new Note(noteDetails.get(0), noteDetails.get(1));
                                    deletedNoteList.add(note);
                                }
                            }
                            deletedNotesAdapter = new DeletedNotesAdapter(deletedNoteList, MainActivity.this);
                            deletedNotesRecyclerView.setAdapter(deletedNotesAdapter);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<String>>> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to retrieve notes: ", t);
                        Toast.makeText(MainActivity.this, "Failed to retrieve notes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        executorService.execute(new GetDeletedNotesTask());
    }

    private void getNotes(RequestType requestType) {
        class GetNotesTask implements Runnable {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                List<Note> notes = new ArrayList<>();
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<List<List<String>>> call = client.getAllNotes("files", repo.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<String>>> call, @NonNull Response<List<List<String>>> response) {
                        if (response.body() != null) {
                            List<List<String>> responseData =  response.body();
                            for (List<String> noteDetails : responseData) {
                                Note note = new Note(noteDetails.get(0), noteDetails.get(1));
                                notes.add(note);
                            }

                            Log.i("Got notes", "onResponse: " + responseData);
                            runOnUiThread(() -> {
                                if (requestType == RequestType.VIEW){
                                    noteList.addAll(notes);
                                    notesAdapter.notifyDataSetChanged();
                                }else if (requestType == RequestType.ADD){
                                    noteList.clear();
                                    noteList.addAll(notes);
                                    notesAdapter.notifyItemInserted(0);
                                    notesRecyclerView.smoothScrollToPosition(0);
                                }else if (requestType == RequestType.UPDATE) {
                                    noteList.remove(noteClickedPosition);
                                    noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                                    notesAdapter.notifyItemChanged(noteClickedPosition);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<String>>> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to retrieve notes: ", t);
                        Toast.makeText(MainActivity.this, "Failed to retrieve notes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        executorService.execute(new GetNotesTask());
    }
    private void getNotes(Boolean isDeleteNote) {
        class GetNotesTask implements Runnable {
            @Override
            public void run() {
                List<Note> notes = new ArrayList<>();
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<List<List<String>>> call = client.getAllNotes("files", repo.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<String>>> call, @NonNull Response<List<List<String>>> response) {
                        if (response.body() != null) {
                            List<List<String>> responseData =  response.body();
                            for (List<String> noteDetails : responseData){
                                Note note = new Note(noteDetails.get(0), noteDetails.get(1));
                                notes.add(note);
                            }
                        }
                        runOnUiThread(() -> {

                            if (deletedNoteList != null && isDeleteNote){
                                deletedNoteList.add(noteList.get(noteClickedPosition));
                                deletedNotesAdapter.notifyItemInserted(0);
                            }
                            noteList.remove(noteClickedPosition);

                            if (isDeleteNote){
                                notesAdapter.notifyItemRemoved(noteClickedPosition);
                            }else {
                                noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                                notesAdapter.notifyItemChanged(noteClickedPosition);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<String>>> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to get notes: ", t);
                        Toast.makeText(MainActivity.this, "Failed to retrieve notes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        executorService.execute(new GetNotesTask());
    }
    private void createUserDialog(){
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_user_list,
                findViewById(R.id.layoutUserList)
        );
        builder.setView(view);
        createUserDialog = builder.create();
        if (createUserDialog.getWindow() != null){
            createUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        RecyclerView userRecyclerView = view.findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        EditText inputUser = view.findViewById(R.id.inputUser);
        userRecyclerView.setAdapter(userAdapter);

        getUsers();
        view.findViewById(R.id.imageAddCollaborator).setOnClickListener(addView -> {
            if (inputUser.getText().toString().isEmpty()){
                inputUser.setError("Enter a User to add");
            }else addUser(inputUser.getText().toString());
        });

        view.findViewById(R.id.buttonCancel).setOnClickListener(cancelView -> {
            createUserDialog.dismiss();
            createUserDialog = null;
        });

        createUserDialog.show();
    }
    private void getUsers(){
        class GetUsersTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<List<String>> call = client.getCollaborators(repo.getName(), repo.getFull_name());
                call.enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                        if (response.body() != null) {
                            userList.addAll(response.body());
                            runOnUiThread(() -> userAdapter.notifyDataSetChanged());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to get notes: ", t);
                    }
                });
            }
        }
        executorService.execute(new GetUsersTask());
    }
    private void addUser(String user){
        class AddUserTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.addCollaborator(repo.getName(), repo.getFull_name(), user);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()){
                            userList.add(user);
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Granted access to " + user, Toast.LENGTH_SHORT).show();
                                userAdapter.notifyItemInserted(0);
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to remove " + user, t);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to grant access to " + user, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        }
        executorService.execute(new AddUserTask());
    }
}
