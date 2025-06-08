// file: util/ApiHelper.java
package com.nutrivda.app.utils;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

/**
 * Ayuda a ejecutar llamadas Retrofit con control de caché opcional
 * Se usa en los Fragments y Actvities con las peticiones de
 * {@link com.nutrivda.app.data.SupabaseApi}
 */
public class ApiHelper {

    /**
     * Ejecuta una llamada Retrofit con o sin caché según el recurso marcado.
     * @param  context contexto de el fragment o el propio Activity
     * @param recurso nombre lógico ("comidas", "dias_completados", etc.)
     * @param callConCache llamada que incluye header "public, max-age=300"
     * @param callSinCache llamada que incluye header "no-cache"
     * @param callback lo que hacer al recibir la respuesta
     * @param <T> tipo de respuesta esperada
     */
    public static <T> void ejecutarGetConControlCache(
            Context context,
            String recurso,
            Call<T> callConCache,
            Call<T> callSinCache,
            Callback<T> callback
    ) {
        boolean forzar = CacheControlUtil.debeRefrescar(context, recurso);
        Call<T> llamada = forzar ? callSinCache : callConCache;
        llamada.enqueue(callback);
    }
}
