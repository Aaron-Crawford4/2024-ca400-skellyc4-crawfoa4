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
import android.widget.Toast;

import com.tester.notes.R;
import com.tester.notes.entities.User;
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.entities.RegisteringUser;
import com.tester.notes.retrofit.RetrofitClient;
import com.tester.notes.utils.Auth;
import com.tester.notes.utils.PasswordValidator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputUsername, inputPassword, inputEmail;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        inputEmail = findViewById(R.id.inputEmail);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(view -> register());
    }
    private void register(){
        class RegisterTask implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> registerCall = client.register(new RegisteringUser(inputUsername.getText().toString(), inputPassword.getText().toString(), inputEmail.getText().toString()));
                registerCall.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Register success, Logging you in", Toast.LENGTH_SHORT).show();

                                Call<User> loginCall = client.login(inputEmail.getText().toString(), inputPassword.getText().toString());
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
                                                    loginLauncher.launch(intent);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                        Log.e("Fail", "Failed Login: ", t);
                                    }
                                });
                            } else Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Fail", "Failed Registration: ", t);
                    }
                });
            }
        }
        if (PasswordValidator.isValidPassword(inputPassword.getText().toString())){
            executorService.execute(new RegisterTask());
        } else inputPassword.setError("Password must have a lower and upper case letter, a number and a symbol (min length 8)");
    }
    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d( "Activity Result Logging", "Successfully launched Activity");
                }
            }
    );
}