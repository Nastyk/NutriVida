package com.nutrivda.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheControlUtil {

    private static final String PREF_NAME = "cache_flags";

    public static void marcarParaRefrescar(Context context, String recurso) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(recurso, true).apply();
    }

    public static boolean debeRefrescar(Context context, String recurso) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean resultado = prefs.getBoolean(recurso, false);
        if (resultado) {
            prefs.edit().remove(recurso).apply(); // se borra después de usarse
        }
        return resultado;
    }

    public static void limpiarTodos(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
