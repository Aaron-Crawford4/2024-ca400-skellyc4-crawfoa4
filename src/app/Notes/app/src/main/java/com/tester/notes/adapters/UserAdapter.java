package com.tester.notes.adapters;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tester.notes.R;
import com.tester.notes.activities.MainActivity;
import com.tester.notes.entities.Repository;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<String> users;
    public UserAdapter(List<String> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (position == 0) holder.imageRemoveUser.setVisibility(View.GONE);
        holder.setUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView imageRemoveUser;
        UserViewHolder(@NonNull View itemView){
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            imageRemoveUser = itemView.findViewById(R.id.imageRemoveUser);

            imageRemoveUser.setOnClickListener(view -> {
                Repository repo = MainActivity.getRepoDetails();
                Retrofit retrofit = RetrofitClient.getAuthClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.removeCollaborator(repo.getName(), repo.getFull_name(), textName.getText().toString());
                Log.i("Testing", repo.getName() + " " + repo.getFull_name() + " " + textName.getText().toString());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Removed Collaborator", Toast.LENGTH_SHORT).show();
                            removePosition(getAbsoluteAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(view.getContext(), "Failed to remove Collaborator!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
        void setUser(String user){
            textName.setText(user);
        }
        void removePosition(int position) {
            users.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, users.size());
        }
    }
}