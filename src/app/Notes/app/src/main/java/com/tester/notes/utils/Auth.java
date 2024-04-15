package com.tester.notes.utils;

public class Auth {
    private static String AUTH_TOKEN;

    public static String getAuthToken() {
        return AUTH_TOKEN;
    }

    public static void setAuthToken(String authToken) {
        AUTH_TOKEN = authToken;
    }
}
