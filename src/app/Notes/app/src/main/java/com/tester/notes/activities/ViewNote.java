package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;
import static com.tester.notes.utils.HideKeyboard.hideKeyboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tester.notes.R;
import com.tester.notes.entities.Note;
import com.tester.notes.entities.Repository;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.retrofit.RetrofitClient;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewNote extends AppCompatActivity {
    private Note note;
    private Repository repo;
    private TextView textDateTime, textNoteText;
    private String sha;
    private String decodedText;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        textNoteText = findViewById(R.id.textNoteText);
        textDateTime = findViewById(R.id.textDateTime);

        Intent previousIntent = getIntent();

        note = (Note) previousIntent.getSerializableExtra("note");
        repo = (Repository) previousIntent.getSerializableExtra("repo");
        sha = previousIntent.getStringExtra("sha");

        byte[] decodedBytes = Base64.getDecoder().decode(note.getContent());
        decodedText = new String(decodedBytes);
        textNoteText.setText(decodedText);

        textDateTime.setText(note.getDateCreated() + " " + note.getTimeCreated());

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> {
            hideKeyboard(this);
            finish();
        });
        ImageView imageRestoreFile = findViewById(R.id.imageRestoreFile);
        imageRestoreFile.setOnClickListener(view -> restoreFile());
    }

    private void restoreFile() {
        class RestoreFileTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.editNote(repo.getOwner().getUsername(), note.getName(), repo.getName(), sha, decodedText);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()){
                            Intent intent = new Intent();
                            intent.putExtra("restoredContent", decodedText);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(ViewNote.this, "Failed to save note edits!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        executorService.execute(new RestoreFileTask());
    }

}