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
import com.tester.notes.rest.UserApiCalls;
import com.tester.notes.retrofit.RetrofitClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RequestResetTokenActivity extends AppCompatActivity {
    private EditText inputEmail;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset_token);
        inputEmail = findViewById(R.id.inputEmail);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(view -> getResetToken());
    }

    private void getResetToken() {
        class GetResetToken implements Runnable{

            @Override
            public void run() {
                Retrofit retrofit = RetrofitClient.getClient(API_BASE_URL);
                UserApiCalls client = retrofit.create(UserApiCalls.class);
                Call<Void> call = client.getResetToken(inputEmail.getText().toString(), "getToken");
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);
                                intent.putExtra("email", inputEmail.getText().toString());
                                startActivity(intent);
                            } else  Toast.makeText(RequestResetTokenActivity.this, "Request to get Reset Token Failed", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Fail", "Get Reset Token Failed: ", t);
                    }
                });
            }
        }
        executorService.execute(new GetResetToken());
    }
}