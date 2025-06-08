package com.nutrivda.app.cache;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Clase que usa OkHttp para cachear la respuesta de las peticiones GET de {@link com.nutrivda.app.data.SupabaseApi}
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://mmgsedtckfpecayazift.supabase.co/";
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Instancia la caché en disco (10MB)
            File cacheDir = new File(context.getCacheDir(), "http_cache");
            Cache cache = new Cache(cacheDir, CACHE_SIZE);

            // Cliente OkHttp con cache habilitada
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache) // Solo se activa si el header Cache-Control lo permite
                    .build();

            // Retrofit con ese cliente
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
