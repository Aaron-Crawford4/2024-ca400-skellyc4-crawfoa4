package com.tester.notes.dao;

import com.tester.notes.entities.Note;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface NoteDjangoDao {
    @GET("/api/view")
    Call<List<Note>> getAllNotes();

    @POST("/api/create")
    Call<Void> createNote(@Body Note note);

    @PUT("/api/edit")
    Call<Void> editNote(@Body Note note);

    @POST("/api/delete")
    Call<Void> deleteNote(@Body Note note);
}
