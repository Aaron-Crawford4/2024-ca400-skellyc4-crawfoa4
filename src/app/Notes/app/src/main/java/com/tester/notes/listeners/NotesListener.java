package com.tester.notes.listeners;

import com.tester.notes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
