package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tester.notes.R;
import com.tester.notes.adapters.NotesAdapter;
import com.tester.notes.dao.NoteDjangoDao;
import com.tester.notes.entities.Note;
import com.tester.notes.listeners.NotesListener;
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
    private NotesAdapter notesAdapter;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private int noteClickedPosition = -1;
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

        ImageView imageAddNote = findViewById(R.id.imageAddNote);
        imageAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CreateNoteActivity.class);
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
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        addNoteLauncher.launch(intent);
    }

    private void getNotes(RequestType requestType) {
        class GetNotesTask implements Runnable {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                List<Note> notes = new ArrayList<>();
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                NoteDjangoDao client = retrofit.create(NoteDjangoDao.class);
                Call<List<Note>> call = client.getAllNotes();
                call.enqueue(new Callback<List<Note>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                        if (response.body() != null) {
                            notes.addAll(response.body());
                            Log.i("Got notes", "onResponse: " + notes);
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
                    public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
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
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                NoteDjangoDao client = retrofit.create(NoteDjangoDao.class);
                Call<List<Note>> call = client.getAllNotes();
                call.enqueue(new Callback<List<Note>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                        if (response.body() != null) {
                            notes.addAll(response.body());
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
                    public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
                        Log.e("Fail", "failed to get notes: ", t);
                        Toast.makeText(MainActivity.this, "Failed to retrieve notes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        executorService.execute(new GetNotesTask());
    }
}
