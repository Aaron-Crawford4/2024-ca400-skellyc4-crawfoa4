package com.tester.notes.adapters;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
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
import com.tester.notes.listeners.NoteDeleteListener;
import com.tester.notes.listeners.NotesListener;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{

    private List<Note> notes;
    private final List<Note> notesSource;
    private final NotesListener notesListener;
    private final NoteDeleteListener deleteListener;
    private Timer timer;

    public NotesAdapter(List<Note> notes, NotesListener notesListener, NoteDeleteListener deleteListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        this.deleteListener = deleteListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(view -> notesListener.onNoteClicked(notes.get(position), position));
        holder.imageDeleteFile.setOnClickListener(view -> deleteListener.onNoteDeleteClicked(notes.get(position), position));
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
        ImageView imageDeleteFile;
        NoteViewHolder(@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            imageDeleteFile = itemView.findViewById(R.id.imageDeleteFile);
            imageDeleteFile.setVisibility(View.VISIBLE);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }
        void setNote(Note note){
            textTitle.setText(note.getName());
            OffsetDateTime dateTime = OffsetDateTime.parse(note.getDateCreated());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
            textDateTime.setText(dateTime.format(formatter));
        }
    }
    public void searchNotes(final String searchTerm){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if (searchTerm.trim().isEmpty()){
                    notes = notesSource;
                }else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource){
                        if (note.getName().toLowerCase(Locale.ENGLISH).contains(searchTerm.toLowerCase(Locale.ENGLISH))){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 300);
    }
    public void cancelTimer(){
        if (timer != null) timer.cancel();
    }
}
