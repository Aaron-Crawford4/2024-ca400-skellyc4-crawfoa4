package com.tester.notes.adapters;


import static com.tester.notes.utils.Constants.API_BASE_URL;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.tester.notes.R;

import com.tester.notes.entities.Repository;
import com.tester.notes.listeners.RepoListener;
import com.tester.notes.rest.NoteApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoViewHolder>{

    private List<Repository> repos;
    private final List<Repository> repoSource;
    private final RepoListener repoListener;
    private Timer timer;

    public RepoAdapter(List<Repository> repos, RepoListener repoListener) {
        this.repos = repos;
        this.repoListener = repoListener;
        repoSource = repos;
    }

    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RepoViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_collection,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RepoViewHolder holder, int position) {
        holder.setRepo(repos.get(position));
        holder.layoutCollection.setOnClickListener(view -> repoListener.onRepoClicked(repos.get(position), position));
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class RepoViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDateTime;
        ConstraintLayout layoutCollection;
        ImageView imageDeleteRepo;
        RepoViewHolder(@NonNull View itemView){
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            layoutCollection = itemView.findViewById(R.id.layoutCollection);

            textDateTime = itemView.findViewById(R.id.textDateTime);

            imageDeleteRepo = itemView.findViewById(R.id.imageDeleteRepo);

            imageDeleteRepo.setOnClickListener(view -> {
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                NoteApiCalls client = retrofit.create(NoteApiCalls.class);
                Call<Void> call = client.repoDelete(textName.getText().toString());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Deleted Collection", Toast.LENGTH_SHORT).show();
                            removePosition(getAbsoluteAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Request Error", "onFailure: ", t);
                        Toast.makeText(view.getContext(), "Failed to Delete Collection!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
        void setRepo(Repository repo){
            textName.setText(repo.getName());
            OffsetDateTime dateTime = OffsetDateTime.parse(repo.getUpdated_at());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
            textDateTime.setText(dateTime.format(formatter));
        }
        void removePosition(int position) {
            repos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, repos.size());
        }
    }
    public void searchRepos(final String searchTerm){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if (searchTerm.trim().isEmpty()){
                    repos = repoSource;
                }else {
                    ArrayList<Repository> temp = new ArrayList<>();
                    for (Repository repo : repoSource){
                        if (repo.getName().toLowerCase(Locale.ENGLISH).contains(searchTerm.toLowerCase(Locale.ENGLISH))){
                            temp.add(repo);
                        }
                    }
                    repos = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 300);
    }
    public void cancelTimer(){
        if (timer != null) timer.cancel();
    }
}
