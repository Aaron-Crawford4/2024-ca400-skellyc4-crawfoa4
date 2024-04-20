package com.tester.notes.activities;

import static com.tester.notes.utils.Constants.API_BASE_URL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
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
        setContentView(R.layout.activity_login);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonSubmit.setOnClickListener(view -> login());

        buttonRegister.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            loginLauncher.launch(intent);
        });
        TextView textForgotPassword = findViewById(R.id.textForgotPassword);
        textForgotPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RequestResetTokenActivity.class)));
    }
    private void login(){

        class LoginTask implements Runnable {

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<User> call = client.login(inputEmail.getText().toString(), inputPassword.getText().toString());
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        runOnUiThread(() -> {
                            User user = response.body();
                            if (user != null && response.isSuccessful()) {
                                Auth.setAuthToken(user.getJwt());
                                Intent intent = new Intent(getApplicationContext(), CollectionsActivity.class);
                                intent.putExtra("username", user.getName());
                                loginLauncher.launch(intent);
                            } else  Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.e("Fail", "Failed Login: ", t);
                    }
                });
            }
        }
        executorService.execute(new LoginTask());
    }
}