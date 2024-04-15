package com.tester.notes.entities;


import java.io.Serializable;

@SuppressWarnings("unused")
public class User implements Serializable {
    private String jwt;
    private String name;
    private String email;

    public String getJwt() {
        return jwt;
    }
}

