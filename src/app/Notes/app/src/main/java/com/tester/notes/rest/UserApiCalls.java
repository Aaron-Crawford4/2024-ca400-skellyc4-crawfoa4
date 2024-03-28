package com.tester.notes.rest;

import com.tester.notes.entities.RegisteringUser;
import com.tester.notes.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApiCalls {
    @FormUrlEncoded
    @POST("/api/login")
    Call<User> login(@Field("email") String email, @Field("password") String password);

    @POST("/api/register")
    Call<Void> register(@Body RegisteringUser user);

    @FormUrlEncoded
    @POST("/api/view")
    Call<List<List<String>>> view(@Field("switch") String s, @Field("repoName") String repoName);
}
