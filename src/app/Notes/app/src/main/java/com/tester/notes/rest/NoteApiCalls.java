package com.tester.notes.rest;

import com.tester.notes.entities.NoteContent;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface NoteApiCalls {
    @FormUrlEncoded
    @POST("/api/view")
    Call<List<List<String>>> getAllNotes(@Field("switch") String s, @Field("repoName") String repoName);

    @GET("/api/{username}/{repo}/{file}")
    Call<NoteContent> getNoteContent(@Path("username") String username, @Path("repo") String repo, @Path("file") String file);

    @FormUrlEncoded
    @POST("/api/create")
    Call<Void> createNote(@Field("repoTitle") String repoTitle, @Field("title") String title, @Field("content") String content);

    @FormUrlEncoded
    @PUT("/api/edit")
    Call<Void> editNote(@Field("user") String user, @Field("file") String file, @Field("repo") String repo, @Field("sha") String sha, @Field("content") String content);

    @FormUrlEncoded
    @POST("/api/delete")
    Call<Void> deleteNote(@Field("file") String file, @Field("repo") String repo);

    @FormUrlEncoded
    @POST("/api/repoDelete")
    Call<Void> repoDelete(@Field("repo") String repo);
}
