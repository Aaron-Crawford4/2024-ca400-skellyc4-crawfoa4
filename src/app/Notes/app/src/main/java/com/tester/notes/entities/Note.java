package com.tester.notes.entities;


import androidx.annotation.NonNull;
import java.io.Serializable;

public class Note implements Serializable{
    private String name;
    private final String dateCreated;
    private String content;
    private String timeCreated;

    public Note(String name, String dateCreated) {
        this.name = name;
        this.dateCreated = dateCreated;
    }

    public Note(String content, String name, String dateCreated, String timeCreated) {
        this.name = name;
        this.dateCreated = dateCreated;
        this.content = content;
        this.timeCreated = timeCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getContent() {
        return content;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " : " + dateCreated;
    }
}
