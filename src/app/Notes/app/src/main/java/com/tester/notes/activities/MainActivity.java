package com.tester.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tester.notes.R;
import com.tester.notes.adapters.NotesAdapter;
import com.tester.notes.database.NotesDatabase;
import com.tester.notes.entities.Note;
import com.tester.notes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
                        getNotes(RequestType.UPDATE);
                    }else {
                        getNotes(RequestType.ADD);
                    }
                    Log.println(Log.INFO, "Activity Result Logging", "Note saved to db successfully");
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
            @Override
            public void run() {
                List<Note> notes = NotesDatabase.getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();

                runOnUiThread(() -> {
                    if (requestType == RequestType.VIEW){
                        noteList.addAll(notes);
                        notesAdapter.notifyDataSetChanged();
                    }else if (requestType == RequestType.ADD){
                        noteList.add(0, notes.get(0));
                        notesAdapter.notifyDataSetChanged();
                        // notesAdapter.notifyItemInserted(0); should be used because it is more
                        // efficient to use specific change events but it is not working at the moment.
                        notesRecyclerView.smoothScrollToPosition(0);
                    }else if (requestType == RequestType.UPDATE) {
                        noteList.remove(noteClickedPosition);
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                });
            }
        }
        executorService.execute(new GetNotesTask());
    }
}
