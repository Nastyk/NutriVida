package com.nutrivda.app.callback;

/**
 * Clase Callback usado para manejar respuesta asincrona de las llamadas a Supabase
 */
public interface CaloriasCallback {
    void onResult(String calorias);
    void onError(String errorMessage);
}

