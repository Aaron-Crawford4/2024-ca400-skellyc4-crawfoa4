package com.tester.notes.entities;


import androidx.annotation.NonNull;
import java.io.Serializable;

public class Note implements Serializable{
    private String name;
    private String dateCreated;

    public Note(String name, String dateCreated) {
        this.name = name;
        this.dateCreated = dateCreated;
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

    @NonNull
    @Override
    public String toString() {
        return name + " : " + dateCreated;
    }
}
