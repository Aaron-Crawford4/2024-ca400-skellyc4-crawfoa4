package com.tester.notes.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RegisteringUser implements Serializable {
    private String name;
    private String email;
    private String password;

    public RegisteringUser(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @NonNull
    @Override
    public String toString() {
        return name +" : "+ email;
    }
}
