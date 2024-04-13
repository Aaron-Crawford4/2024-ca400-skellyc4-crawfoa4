package com.tester.notes.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class RegisteringUser implements Serializable {
    private final String name;
    private final String email;
    private final String password;

    public RegisteringUser(String name, String password, String email) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    @NonNull
    @Override
    public String toString() {
        return name +" : "+ email;
    }
}
