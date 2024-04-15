package com.tester.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tester.notes.R;
import com.tester.notes.entities.Note;
import com.tester.notes.listeners.DeletedNotesListener;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DeletedNotesAdapter extends RecyclerView.Adapter<DeletedNotesAdapter.NoteViewHolder>{
    private final List<Note> notes;
    private final DeletedNotesListener notesListener;

    public DeletedNotesAdapter(List<Note> notes, DeletedNotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public DeletedNotesAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeletedNotesAdapter.NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DeletedNotesAdapter.NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.imageRestoreFile.setOnClickListener(view -> notesListener.onDeletedNoteClicked(notes.get(position), position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDateTime;
        ConstraintLayout layoutNote;
        ImageView imageRestoreFile;

        NoteViewHolder(@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            imageRestoreFile = itemView.findViewById(R.id.imageRestoreFile);
            imageRestoreFile.setVisibility(View.VISIBLE);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }
        void setNote(Note note){
            textTitle.setText(note.getName());
            OffsetDateTime dateTime = OffsetDateTime.parse(note.getDateCreated());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
            textDateTime.setText(dateTime.format(formatter));
        }
    }
}

