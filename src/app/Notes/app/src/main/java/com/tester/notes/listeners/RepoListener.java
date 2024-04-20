package com.tester.notes.listeners;

import com.tester.notes.entities.Repository;

public interface RepoListener {
    void onRepoClicked(Repository repo, int position);
}
