package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.adapters.RepoAdapter;
import com.tester.notes.entities.Repository;
import com.tester.notes.listeners.RepoListener;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CollectionsActivity extends AppCompatActivity implements RepoListener {
    private RecyclerView repoRecyclerView;
    private List<Repository> repoList, allRepos, ownedRepos, sharedRepos;
    private RepoAdapter repoAdapter;
    private ImageView imageAddCollection, imageNoFilter;
    private TextView textOwnedFilter, textSharedFilter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private Dialog createRepoDialog;

    private final ActivityResultLauncher<Intent> repoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d( "Activity Result Logging", "Successfully launched Activity");
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        imageAddCollection = findViewById(R.id.imageAddCollection);
        repoRecyclerView = findViewById(R.id.repoRecyclerView);
        repoRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        repoList = new ArrayList<>();
        repoAdapter = new RepoAdapter(repoList, this);
        repoRecyclerView.setAdapter(repoAdapter);

        getRepos();

        imageAddCollection.setOnClickListener(view -> createRepo());

        imageNoFilter = findViewById(R.id.imageNoFilter);
        textOwnedFilter = findViewById(R.id.textOwnedFilter);
        textSharedFilter = findViewById(R.id.textSharedFilter);

        setFilterListeners();

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                repoAdapter.cancelTimer();
            }
            @Override
            public void afterTextChanged(Editable searchTerm) {
                if (repoList.size() != 0) repoAdapter.searchRepos(searchTerm.toString());
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void setFilterListeners(){
        imageNoFilter.setOnClickListener(view -> {
            imageNoFilter.setVisibility(View.GONE);
            textOwnedFilter.setVisibility(View.VISIBLE);
            repoList.clear();
            repoList.addAll(ownedRepos);
            repoAdapter.notifyDataSetChanged();
        });
        textOwnedFilter.setOnClickListener(view -> {
            textOwnedFilter.setVisibility(View.GONE);
            textSharedFilter.setVisibility(View.VISIBLE);
            repoList.clear();
            repoList.addAll(sharedRepos);
            repoAdapter.notifyDataSetChanged();
        });
        textSharedFilter.setOnClickListener(view -> {
            textSharedFilter.setVisibility(View.GONE);
            imageNoFilter.setVisibility(View.VISIBLE);
            repoList.clear();
            repoList.addAll(allRepos);
            repoAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onRepoClicked(Repository repo, int position) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("repo", repo);
        repoLauncher.launch(intent);
    }

    private void getRepos(){
        class GetReposTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<List<List<Repository>>> call = client.view("repo", "");
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<List<Repository>>> call, @NonNull Response<List<List<Repository>>> response) {
                        if (response.body() != null) {
                            allRepos = response.body().get(0);
                            ownedRepos = response.body().get(1);
                            sharedRepos = response.body().get(2);
                            repoList.addAll(allRepos);
                            runOnUiThread(()-> repoAdapter.notifyDataSetChanged());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<List<Repository>>> call, @NonNull Throwable t) {
                        Log.e("Fail", "Failed to get Repos: ", t);
                    }
                });
            }
        }
        executorService.execute(new GetReposTask());
    }
    private void createRepo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_create_repo,
                findViewById(R.id.layoutCreateRepo)
        );
        builder.setView(view);
        createRepoDialog = builder.create();
        if (createRepoDialog.getWindow() != null){
            createRepoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        EditText inputCollectionName = view.findViewById(R.id.inputCollectionName);

        view.findViewById(R.id.buttonCreateCollection).setOnClickListener(confirmView -> {
            class CreateRepoTask implements Runnable{
                @Override
                public void run() {
                    Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                    NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                    Call<Void> call = client.createNote(inputCollectionName.getText().toString(), "Example", "Example");
                    call.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            Toast.makeText(CollectionsActivity.this, "Created new collection!", Toast.LENGTH_SHORT).show();
                            runOnUiThread(()-> createRepoDialog.dismiss());
                            repoList.clear();
                            getRepos();
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(CollectionsActivity.this, "Failed to create new collection!", Toast.LENGTH_SHORT).show();
                            Log.e("Fail", "Failed to create Repo", t);
                        }
                    });
                }
            }
            String stringCollectionName = inputCollectionName.getText().toString();
            if (stringCollectionName.contains(" ")) {
                inputCollectionName.setError("Collection Names may not contain spaces");
            } else if (stringCollectionName.isEmpty()) {
                inputCollectionName.setError("Enter a Collection Name");
            } else executorService.execute(new CreateRepoTask());
        });

        view.findViewById(R.id.buttonCancel).setOnClickListener(cancelView -> {
                createRepoDialog.dismiss();
                createRepoDialog = null;
        });

        createRepoDialog.show();
    }
}