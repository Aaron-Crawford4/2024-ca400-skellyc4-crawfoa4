package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.tester.notes.R;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CollectionsActivity extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        getRepos();
    }

    private void getRepos(){
        class GetReposTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<List<List<String>>> call = client.view("repo", "");
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<String>>> call, @NonNull Response<List<List<String>>> response) {
                        if (response.body() != null) {
                            Log.i("Test Logging", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<String>>> call, @NonNull Throwable t) {
                        Log.e("Fail", "Failed to get Repos: ", t);
                    }
                });
            }
        }
        executorService.execute(new GetReposTask());
    }
}