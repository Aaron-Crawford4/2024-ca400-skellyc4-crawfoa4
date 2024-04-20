package com.tester.notes.activities;

import static com.tester.notes.utils.HideKeyboard.hideKeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tester.notes.R;
import com.tester.notes.utils.markdownBuilder;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class HelpActivity extends AppCompatActivity {
    private EditText inputNoteText;
    private Markwon markwon;
    private boolean showingMarkdown = false;
    private ImageView imagePreviewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        inputNoteText = findViewById(R.id.inputNoteText);
        ImageView imageBack = findViewById(R.id.imageBack);


        imageBack.setOnClickListener(view -> {
            hideKeyboard(this);
            finish();
        });
        markwon = markdownBuilder.getMarkwon(getApplicationContext());
        setupMarkdownHighlighting();

        imagePreviewNote = findViewById(R.id.imagePreview);
        imagePreviewNote.setOnClickListener(view -> showMarkdown());
    }
    private void setupMarkdownHighlighting() {
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        inputNoteText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                inputNoteText));
        editor.process(inputNoteText.getText());
    }
    private void showMarkdown() {
        hideKeyboard(this);
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
}