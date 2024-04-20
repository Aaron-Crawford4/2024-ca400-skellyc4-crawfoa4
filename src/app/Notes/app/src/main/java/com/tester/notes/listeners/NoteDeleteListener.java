package com.tester.notes.listeners;

import com.tester.notes.entities.Note;

public interface NoteDeleteListener {
    void onNoteDeleteClicked(Note note, int position);
}
