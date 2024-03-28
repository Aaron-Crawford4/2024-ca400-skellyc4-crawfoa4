package com.tester.notes.retrofit;

import com.tester.notes.utils.Auth;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit loginRetrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (loginRetrofit == null) {
            loginRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return loginRetrofit;
    }

    public static Retrofit getAuthClient(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.addInterceptor(chain -> {
                Request request = chain.request();
                Request.Builder newRequest = request.newBuilder().addHeader("COOKIE", "jwt=" + Auth.getAuthToken());
                return chain.proceed(newRequest.build());
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

