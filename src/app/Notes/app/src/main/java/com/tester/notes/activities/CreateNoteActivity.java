package com.tester.notes.activities;


import static com.tester.notes.utils.Constants.API_BASE_URL;
import static com.tester.notes.utils.HideKeyboard.hideKeyboard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.dao.NoteDjangoDao;
import com.tester.notes.entities.Note;
import com.tester.notes.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Note existingNote;
    private AlertDialog deleteNoteDialog;

    private final ActivityResultLauncher<Intent> previewNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.println(Log.INFO, "Activity Result Logging", "Note changes saved to db successfully");
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> {
            hideKeyboard(this);
            finish();
        });

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        textDateTime = findViewById(R.id.textDateTime);

        ImageView imagePreviewNote = findViewById(R.id.imagePreview);
        imagePreviewNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MarkdownPreviewActivity.class);
            intent.putExtra("noteText", inputNoteText.getText().toString());
            previewNoteLauncher.launch(intent);
        });

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageDone);
        imageSave.setOnClickListener(view -> {
            hideKeyboard(this);
            saveNote();
        });

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
                        Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                        NoteDjangoDao client = retrofit.create(NoteDjangoDao.class);
                        Call<Void> call = client.deleteNote(existingNote);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                Toast.makeText(CreateNoteActivity.this, "Deleted note!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(CreateNoteActivity.this, "Failed to delete note!", Toast.LENGTH_SHORT).show();
                            }
                        });

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
        inputNoteText.setText(existingNote.getContent());
        textDateTime.setText(existingNote.getDate_created());
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
        note.setContent(inputNoteText.getText().toString());
        note.setDate_created(textDateTime.getText().toString());

        class SaveNoteTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                NoteDjangoDao client = retrofit.create(NoteDjangoDao.class);
                Call<Void> call = client.editNote(note);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Toast.makeText(CreateNoteActivity.this, "Saved note edits!", Toast.LENGTH_SHORT).show();

                        runOnUiThread(() -> {
                            Intent intent = new Intent();
                            intent.putExtra("isViewOrUpdate", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(CreateNoteActivity.this, "Failed to save note edits!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        class CreateNoteTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                NoteDjangoDao client = retrofit.create(NoteDjangoDao.class);
                Call<Void> call = client.createNote(note);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Toast.makeText(CreateNoteActivity.this, "Created new note!", Toast.LENGTH_SHORT).show();

                        runOnUiThread(() -> {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(CreateNoteActivity.this, "Failed to create new note!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (existingNote != null){
            note.setCode(existingNote.getCode());
            executorService.execute(new SaveNoteTask());
        } else executorService.execute(new CreateNoteTask());
    }
}