package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.entities.User;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;
import com.tester.notes.utils.Auth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PasswordResetActivity extends AppCompatActivity {
    private EditText inputPassword, inputResetToken;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        Intent previousIntent = getIntent();
        String email = previousIntent.getStringExtra("email");

        inputPassword = findViewById(R.id.inputPassword);
        inputResetToken = findViewById(R.id.inputResetToken);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(view -> resetPassword(email));
    }

    private void resetPassword(String email) {
        String password = inputPassword.getText().toString();
        class GetResetToken implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.resetPassword(email,  "reset", inputResetToken.getText().toString(), password);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Call<User> loginCall = client.login(email, password);
                                loginCall.enqueue(new Callback<>() {
                                    @Override
                                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                        if (response.isSuccessful()) {
                                            runOnUiThread(() -> {
                                                User user = response.body();
                                                if (user != null) {
                                                    Auth.setAuthToken(user.getJwt());
                                                    Intent intent = new Intent(getApplicationContext(), CollectionsActivity.class);
                                                    intent.putExtra("username", user.getName());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                        Log.e("Fail", "Failed Login: ", t);
                                    }
                                });
                            } else  Toast.makeText(PasswordResetActivity.this, "Failed Request, Ensure reset token is correct", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Fail", "Reset Password Failed: ", t);
                    }
                });
            }
        }
        executorService.execute(new GetResetToken());
    }
}