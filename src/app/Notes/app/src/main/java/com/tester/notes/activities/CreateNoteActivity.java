package com.tester.notes.activities;


import static com.tester.notes.utils.Constants.API_BASE_URL;
import static com.tester.notes.utils.HideKeyboard.hideKeyboard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.adapters.PrevVerNotesAdapter;
import com.tester.notes.entities.NoteContent;
import com.tester.notes.entities.Repository;
import com.tester.notes.listeners.NotesListener;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.entities.Note;
import com.tester.notes.retrofit.RetrofitClient;
import com.tester.notes.utils.markdownBuilder;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateNoteActivity extends AppCompatActivity implements NotesListener {

    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private Note existingNote;
    private String noteSha;
    private AlertDialog deleteNoteDialog;
    private Dialog fileHistoryDialog;
    private Repository repo;
    private List<Note> prevFileVersions;
    private PrevVerNotesAdapter notesAdapter;
    private boolean showingMarkdown = false;
    private ImageView imagePreviewNote;
    private Markwon markwon;

    private final ActivityResultLauncher<Intent> noteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Log.println(Log.INFO, "Activity Result Logging", "Previewed Note");
                    fileHistoryDialog.dismiss();
                    inputNoteText.setText(result.getData().getStringExtra("restoredContent"));
                    getNoteContent(true);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent previousIntent = getIntent();
        repo = (Repository) previousIntent.getSerializableExtra("repo");

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> {
            hideKeyboard(this);
            finish();
        });

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        textDateTime = findViewById(R.id.textDateTime);

        markwon = markdownBuilder.getMarkwon(getApplicationContext());
        setupMarkdownHighlighting();

        imagePreviewNote = findViewById(R.id.imagePreview);

        imagePreviewNote.setOnClickListener(view -> showMarkdown());


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
            existingNote = (Note) previousIntent.getSerializableExtra("note");
            getNoteContent(false);
        }
        if(existingNote != null){
            setExistingNote();
        }
    }

    private void setExistingNote() {
        inputNoteTitle.setInputType(InputType.TYPE_NULL);
        findViewById(R.id.imageDeleteNote).setVisibility(View.VISIBLE);
        findViewById(R.id.imageDeleteNote).setOnClickListener(view -> createDeleteDialog());
        findViewById(R.id.imageFileHistory).setVisibility(View.VISIBLE);
        findViewById(R.id.imageFileHistory).setOnClickListener(view -> createHistoryDialog());
    }

    private void showMarkdown() {
        TextView markdownNoteContent = findViewById(R.id.markdownNoteContent);
        if (showingMarkdown){
            showingMarkdown = false;
            imagePreviewNote.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorIcons)));

            markdownNoteContent.setVisibility(View.GONE);
            inputNoteText.setVisibility(View.VISIBLE);
        } else {
            showingMarkdown = true;
            imagePreviewNote.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));

            markwon.setMarkdown(markdownNoteContent, inputNoteText.getText().toString());
            inputNoteText.setVisibility(View.GONE);
            markdownNoteContent.setVisibility(View.VISIBLE);
        }
    }

    private void setupMarkdownHighlighting() {
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        inputNoteText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                inputNoteText));
    }

    private void createHistoryDialog() {
        prevFileVersions = new ArrayList<>();
        notesAdapter = new PrevVerNotesAdapter(prevFileVersions, CreateNoteActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_file_history,
                findViewById(R.id.layoutFileHistory)
        );
        builder.setView(view);
        fileHistoryDialog = builder.create();
        if (fileHistoryDialog.getWindow() != null){
            fileHistoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        RecyclerView fileHistoryRecyclerView = view.findViewById(R.id.fileHistoryRecyclerView);
        fileHistoryRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        fileHistoryRecyclerView.setAdapter(notesAdapter);

        getPrevNoteVersions();

        view.findViewById(R.id.buttonCancel).setOnClickListener(cancelView -> {
            fileHistoryDialog.dismiss();
            fileHistoryDialog = null;
        });

        fileHistoryDialog.show();
    }

    private void getPrevNoteVersions() {
        class GetPreviousVersionsTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<List<List<String>>> call = client.getPreviousVersions(existingNote.getName(), repo.getName(), repo.getOwner().getUsername());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<String>>> call, @NonNull Response<List<List<String>>> response) {
                        if (response.body() != null) {
                            for (List<String> noteDetails : response.body()) {
                                prevFileVersions.add(new Note(noteDetails.get(0), existingNote.getName(), noteDetails.get(2), noteDetails.get(3)));
                                notesAdapter.notifyItemInserted(0);
                            }
                            Log.i("Test Log", "GetPreviousVersionsTask task successful");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<String>>> call, @NonNull Throwable t) {
                        Log.e("Request Error", "Failed GetPreviousVersionsTask", t);
                    }
                });
            }
        }
        executorService.execute(new GetPreviousVersionsTask());
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        Log.i("Test Log", "onNoteClicked: " + note);
        Intent intent = new Intent(getApplicationContext(), ViewNote.class);
        intent.putExtra("note", note);
        intent.putExtra("repo", repo);
        intent.putExtra("sha", noteSha);
        noteLauncher.launch(intent);
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
                        Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                        NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                        Call<Void> call = client.deleteNote(existingNote.getName(), repo.getName());
                        call.enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                Toast.makeText(CreateNoteActivity.this, "Deleted note!", Toast.LENGTH_SHORT).show();
                                runOnUiThread(() -> {
                                    Intent intent = new Intent();
                                    intent.putExtra("isNoteDeleted", true);
                                    intent.putExtra("isViewOrUpdate", true);
                                    setResult(RESULT_OK, intent);

                                    deleteNoteDialog.dismiss();
                                    finish();
                                });
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(CreateNoteActivity.this, "Failed to delete note!", Toast.LENGTH_SHORT).show();
                            }
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

    private void getNoteContent(Boolean gettingNewSha){
        if (!gettingNewSha) {
            inputNoteTitle.setText(existingNote.getName());

            OffsetDateTime dateTime = OffsetDateTime.parse(existingNote.getDateCreated());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
            textDateTime.setText(dateTime.format(formatter));
        }

        class GetNoteContentTask implements Runnable{
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<NoteContent> call = client.getNoteContent(repo.getOwner().getUsername(), repo.getName(), existingNote.getName());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<NoteContent> call, @NonNull Response<NoteContent> response) {
                        if (response.isSuccessful() && response.body()!= null){
                            noteSha = response.body().getSha();
                            if (!gettingNewSha) {
                                byte[] decodedBytes = Base64.getDecoder().decode(response.body().getContent());
                                String decodedText = new String(decodedBytes);
                                inputNoteText.setText(decodedText);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NoteContent> call, @NonNull Throwable t) {
                        Log.e("Request Error", "Failed GetNoteContentTask", t);
                    }
                });
            }
        }
        executorService.execute(new GetNoteContentTask());
    }
    private void saveNote(){
        if (inputNoteTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Note title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }else if (inputNoteText.getText().toString().isEmpty()){
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note(inputNoteTitle.getText().toString(), textDateTime.getText().toString()) ;
        String content = inputNoteText.getText().toString();

        class SaveNoteTask implements Runnable {
            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.editNote(repo.getOwner().getUsername(), existingNote.getName(), repo.getName(), noteSha, content);
                call.enqueue(new Callback<>() {
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
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.createNote(repo.getName(), note.getName(), content);
                call.enqueue(new Callback<>() {
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
            executorService.execute(new SaveNoteTask());
        } else executorService.execute(new CreateNoteTask());
    }
}