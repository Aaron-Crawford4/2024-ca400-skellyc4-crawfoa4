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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tester.notes.R;
import com.tester.notes.adapters.NotesAdapter;

import com.tester.notes.adapters.UserAdapter;

import com.tester.notes.entities.Repository;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.entities.Note;
import com.tester.notes.listeners.NotesListener;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements NotesListener {
    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private List<String> userList;
    private NotesAdapter notesAdapter;
    private UserAdapter userAdapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private int noteClickedPosition = -1;
    private static Repository repo;
    private Dialog createUserDialog;
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
        repo = (Repository) getIntent().getSerializableExtra("repo");

        ImageView imageAddNote = findViewById(R.id.imageAddNote);
        imageAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CreateNoteActivity.class);
            intent.putExtra("repo", repo);
            addNoteLauncher.launch(intent);
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
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
                                    noteList.add(0, notes.get(notes.size()-1));
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
                        Log.e("Fail", "failed to get notes: ", t);
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
        view.findViewById(R.id.imageAddCollaborator).setOnClickListener(addView -> addUser(inputUser.getText().toString()));

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
