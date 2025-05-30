package com.nutrivda.app.data;

import com.nutrivda.app.model.Comida;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.model.Usuario;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {

    /**
     * Obtiene todas las moidas de la tabla comidas
     * @return
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/comidas?select=*") // Obtiene todos los registros sin filtrar
    Call<List<Comida>> obtenerTodasLasComidas();

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/comidas")
    Call<List<Comida>> obtenerComidaPorId(
            @Query("id") String idFilter,  // debe ser "eq.123" por ejemplo
            @Query("select") String selectFields // usar "*" o campos específicos
    );

    /**
     *
     * @param comidaData
     * @return
     *
     * inserta un dia completado en la tabla dias_completados
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @POST("rest/v1/dias_completados")
    Call<Void> insertarComida(@Body Map<String, Object> comidaData);

    /**
     *
     * @param userId
     * @param fecha
     * @param comidaData
     * @return
     *
     * Actualiza un registro de la tabla dias_completados filtrando por id_usuario_fk y fecha
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @PATCH("rest/v1/dias_completados")
    Call<Void> actualizarComida(@Query("id_usuario_fk") String userId, @Query("fecha") String fecha, @Body Map<String, Object> comidaData);

    /**
     *
     * @param fecha
     * @param userId
     * @return
     *
     * Recupera un registro de la tabla dias_completados filtrando por fecha y id_usuario_fk
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/dias_completados")
    Call<List<DiaCompletado>> obtenerDiaCompletado(@Query("fecha") String fecha, @Query("id_usuario_fk") String userId);

    /**
     *
     * @param completado
     * @param userId
     * @return
     *
     * Recupera todos los registros de la tabla dias_completados
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/dias_completados")
    Call<List<DiaCompletado>> obtenerDiasCompletados(@Query("completado") String completado, @Query("id_usuario_fk") String userId);

    /**
     *
     * @param usuario
     * @param contrasena
     * @return
     *
     * verifica si un ususario existe en la tabla usuario filtrando por campo usuario y contraseña
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Map<String, Object>>> verificarUsuario(@Query("usuario") String usuario, @Query("contraseña") String contrasena);

    /**
     *
     * @param usuario
     * @return
     *
     * Verifica si un usuario existe en la tabla usuario filtrando por campo usuario
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Map<String, Object>>> verificarUsuarioPorUsername(@Query("usuario") String usuario);

    /**
     *
     * @param userId
     * @return
     *
     * Recupera un usuario de la tabla usuario fiultrando por id
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Usuario>> obtenerUsuario(@Query("id") String userId);

    /**
     *
     * @param userId
     * @return
     *
     * Recupera el registor de la tabla datos_usuario filtradno por campo id_usuario_fk
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/datos_usuario")
    Call<List<DatosUsuario>> obtenerDatosUsuario(@Query("id_usuario_fk") String userId);

    /**
     *
     * @param userId
     * @param datosUsuario
     * @return
     *
     * Actualiza el registro de la tabla datos_usuario filtrando por id_usuario_fk
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("rest/v1/datos_usuario")
    Call<Response<Void>> actualizarDatosUsuario(
            @Query("id_usuario_fk") String userId,
            @Body DatosUsuario datosUsuario
    );

    /**
     *
     * @param usuario
     * @return
     *
     * Crea un usuario en la tabla usuario
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("rest/v1/usuario")
    Call<List<Usuario>> registrarUsuario(@Body Usuario usuario);

    /**
     *
     * @param datosUsuario
     * @return
     *
     * Registra datos de usuario en la tabla datos_usuario, 1 registrto por usuario
     */
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @POST("rest/v1/datos_usuario")
    Call<ResponseBody> registrarDatosUsuario(@Body DatosUsuario datosUsuario);


    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @GET("rest/v1/dias_completados")
    Call<List<Map<String, Object>>> obtenerDiaComida(
            @Query("id_usuario_fk") String userId,
            @Query("fecha") String fecha,
            @Query("select") String campos
    );

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("rest/v1/dias_completados")
    Call<Void> actualizarDiaComida(
            @Query("id_usuario_fk") String userId,
            @Query("fecha") String fecha,
            @Body Map<String, Object> body
    );

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("rest/v1/dias_completados")
    Call<Void> eliminarComidaDeDia(
            @Query("id_usuario_fk") String userId,
            @Query("fecha") String fecha,
            @Body Map<String, Object> updateBody
    );
}
