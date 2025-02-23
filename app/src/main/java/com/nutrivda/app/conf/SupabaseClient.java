package com.nutrivda.app.conf;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://mmgsedtckfpecayazift.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getApiKey() {
        return SUPABASE_KEY;
    }
}
