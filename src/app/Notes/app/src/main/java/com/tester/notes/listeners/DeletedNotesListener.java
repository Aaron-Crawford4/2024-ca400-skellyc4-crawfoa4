package com.tester.notes.listeners;

import com.tester.notes.entities.Note;

public interface DeletedNotesListener {
    void onDeletedNoteClicked(Note note, int position);
}
