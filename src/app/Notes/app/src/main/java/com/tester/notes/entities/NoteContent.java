package com.tester.notes.entities;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("unused")
public class NoteContent implements Serializable {
    private String name;
    private String path;
    private String sha;
    private String last_commit_sha;
    private String type;
    private int size;
    private String encoding;
    private String content;
    private Object target;
    private String url;
    private String html_url;
    private String git_url;
    private String download_url;
    private Object submodule_git_url;
    private Map<String, String> _links;

    public String getName() {
        return name;
    }

    public String getSha() {
        return sha;
    }

    public String getContent() {
        return content;
    }
}
