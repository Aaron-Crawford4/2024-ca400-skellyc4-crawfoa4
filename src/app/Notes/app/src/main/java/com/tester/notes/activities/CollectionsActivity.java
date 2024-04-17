package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.tester.notes.R;
import com.tester.notes.adapters.RepoAdapter;
import com.tester.notes.entities.Repository;
import com.tester.notes.listeners.RepoListener;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CollectionsActivity extends AppCompatActivity implements RepoListener {
    private List<Repository> repoList, allRepos, ownedRepos, sharedRepos;
    private RepoAdapter repoAdapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private Dialog createRepoDialog;
    public DrawerLayout drawerLayout;
    private Menu navMenu;
    private int menuState = R.id.nav_Home;
    private final ActivityResultLauncher<Intent> repoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    if(result.getData().getBooleanExtra("navigationPressed", false)) {
                        int menuItemId = result.getData().getIntExtra("menuItemId", -1);
                        if (menuItemId != -1) {
                            updateNavigation(menuItemId);
                            updateCheckedNavItem(menuItemId);
                        }
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        ImageView imageAddCollection = findViewById(R.id.imageAddCollection);
        RecyclerView repoRecyclerView = findViewById(R.id.repoRecyclerView);
        repoRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        repoList = new ArrayList<>();
        allRepos = new ArrayList<>();
        ownedRepos = new ArrayList<>();
        sharedRepos = new ArrayList<>();

        repoAdapter = new RepoAdapter(repoList, this);
        repoRecyclerView.setAdapter(repoAdapter);

        getRepos();

        imageAddCollection.setOnClickListener(view -> createRepo());

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
        setupDrawer();
    }
    private void logout() {
        class LogoutTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.logout();
                try {
                    call.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        executorService.execute(new LogoutTask());
    }
    private void updateCheckedNavItem(int menuItemId) {
        for (int i = 0; i < navMenu.size(); i++){
            if (menuItemId == navMenu.getItem(i).getItemId()){
                navMenu.getItem(i).setChecked(true);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private boolean updateNavigation(int itemId) {
        if (itemId == R.id.nav_Home) {
            repoList.clear();
            repoList.addAll(allRepos);
            repoAdapter.notifyDataSetChanged();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_Owned) {
            repoList.clear();
            repoList.addAll(ownedRepos);
            repoAdapter.notifyDataSetChanged();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }else if (itemId == R.id.nav_Shared) {
            repoList.clear();
            repoList.addAll(sharedRepos);
            repoAdapter.notifyDataSetChanged();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }else if (itemId == R.id.nav_Logout) {
            logout();
            finish();
            return true;
        }else return false;
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView imageDrawerToggle = findViewById(R.id.imageDrawerToggle);
        imageDrawerToggle.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navView = drawerLayout.findViewById(R.id.navMenu);
        navView.bringToFront();

        navMenu = navView.getMenu();
        navMenu.setGroupCheckable(R.id.groupCollectionFilters, true, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            navMenu.setGroupDividerEnabled(true);
        }

        navView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            menuState = item.getItemId();
            return updateNavigation(item.getItemId());
        });
    }
    @Override
    public void onRepoClicked(Repository repo, int position) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("repo", repo);
        intent.putExtra("menuState", menuState);
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
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<List<Repository>>> call, @NonNull Response<List<List<Repository>>> response) {
                        if (response.body() != null) {
                            allRepos.addAll(response.body().get(0));
                            ownedRepos.addAll(response.body().get(1));
                            sharedRepos.addAll(response.body().get(2));
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
                            if (response.isSuccessful()) {
                                Toast.makeText(CollectionsActivity.this, "Created new collection!", Toast.LENGTH_SHORT).show();
                                runOnUiThread(() -> createRepoDialog.dismiss());
                                repoList.clear();
                                getRepos();
                            }else Toast.makeText(CollectionsActivity.this, "Failed to create new collection!", Toast.LENGTH_SHORT).show();
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