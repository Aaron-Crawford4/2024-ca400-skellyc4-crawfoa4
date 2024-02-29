package com.tester.notes.entities;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;

public class Note implements Serializable {
    private String id;
    private String title;

    private String content;
    private String date_created;

    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " : " + date_created;
    }
}
