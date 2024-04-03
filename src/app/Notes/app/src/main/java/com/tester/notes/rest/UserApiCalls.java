package com.tester.notes.rest;

import com.tester.notes.entities.RegisteringUser;
import com.tester.notes.entities.Repository;
import com.tester.notes.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApiCalls {
    @FormUrlEncoded
    @POST("/api/login")
    Call<User> login(@Field("email") String email, @Field("password") String password);

    @POST("/api/register")
    Call<Void> register(@Body RegisteringUser user);
    @FormUrlEncoded
    @POST("/api/view")
    Call<List<List<Repository>>> view(@Field("switch") String s, @Field("repoName") String repoName);
    @FormUrlEncoded
    @POST("/api/collaborators")
    Call<List<String>> getCollaborators(@Field("repoName") String repoName, @Field("repoFullName") String repoFullName);
    @FormUrlEncoded
    @POST("/api/removeCollaborator")
    Call<Void> removeCollaborator(@Field("repoName") String repoName, @Field("repoFullName") String repoFullName, @Field("collaborator") String collaborator);
    @FormUrlEncoded
    @PUT("/api/addUserToRepo")
    Call<Void> addCollaborator(@Field("repo") String repo, @Field("repoFullName") String repoFullName, @Field("addedUser") String addedUser);
}
