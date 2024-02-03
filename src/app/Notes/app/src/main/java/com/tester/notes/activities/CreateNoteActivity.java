package com.tester.notes.activities;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.database.NotesDatabase;
import com.tester.notes.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Note existingNote;
    private AlertDialog deleteNoteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> finish());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        textDateTime = findViewById(R.id.textDateTime);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageDone);
        imageSave.setOnClickListener(view -> saveNote());

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            existingNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        if(existingNote != null){
            findViewById(R.id.imageDeleteNote).setVisibility(View.VISIBLE);
            findViewById(R.id.imageDeleteNote).setOnClickListener(view -> createDeleteDialog());
        }
    }

    private void createDeleteDialog(){
        if (deleteNoteDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    findViewById(R.id.layoutDeleteNote)
            );
            builder.setView(view);
            deleteNoteDialog = builder.create();
            if (deleteNoteDialog.getWindow() != null){
                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.textDeleteNoteConfirm).setOnClickListener(confirmView -> {
                class DeleteNoteTask implements Runnable {
                    @Override
                    public void run() {
                        NotesDatabase.getDatabase(getApplicationContext())
                                .noteDao().deleteNote(existingNote);

                        runOnUiThread(() -> {
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            intent.putExtra("isViewOrUpdate", true);
                            setResult(RESULT_OK, intent);

                            deleteNoteDialog.dismiss();
                            finish();
                        });
                    }
                }
                executorService.execute(new DeleteNoteTask());
            });

            view.findViewById(R.id.textCancel).setOnClickListener(cancelView ->
                    deleteNoteDialog.dismiss());
        }

        deleteNoteDialog.show();
    }

    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(existingNote.getTitle());
        inputNoteText.setText(existingNote.getNoteText());
        textDateTime.setText(existingNote.getDateTime());
    }
    private void saveNote(){
        if (inputNoteTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Note title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }else if (inputNoteText.getText().toString().isEmpty()){
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());

        if (existingNote != null){
            note.setId(existingNote.getId());
        }

        class SaveNoteTask implements Runnable {
            @Override
            public void run() {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);

                runOnUiThread(() -> {
                    Intent intent = new Intent();

                    if (existingNote != null) intent.putExtra("isViewOrUpdate", true);

                    setResult(RESULT_OK, intent);
                    finish();
                });
            }
        }
        executorService.execute(new SaveNoteTask());
    }
}