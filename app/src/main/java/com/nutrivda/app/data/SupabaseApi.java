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
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json"
    })
    @POST("rest/v1/usuarios")
    Call<Void> crearUsuario(@Body Map<String, Object> usuario);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuarios")
    Call<List<Usuario>> obtenerUsuarios();

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
    @POST("rest/v1/dias_completados")
    Call<Void> insertarComida(@Body Map<String, Object> comidaData);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @PATCH("rest/v1/dias_completados")
    Call<Void> actualizarComida(@Query("id_usuario_fk") String userId, @Query("fecha") String fecha, @Body Map<String, Object> comidaData);
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/dias_completados")
    Call<List<DiaCompletado>> obtenerDiaCompletado(@Query("fecha") String fecha, @Query("id_usuario_fk") String userId);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/dias_completados")
    Call<List<DiaCompletado>> obtenerDiasCompletados(@Query("completado") String completado, @Query("id_usuario_fk") String userId);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Map<String, Object>>> verificarUsuario(@Query("usuario") String usuario, @Query("contraseña") String contrasena);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Map<String, Object>>> verificarUsuarioPorUsername(@Query("usuario") String usuario);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/usuario")
    Call<List<Usuario>> obtenerUsuario(@Query("id") String userId);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @GET("rest/v1/datos_usuario")
    Call<List<DatosUsuario>> obtenerDatosUsuario(@Query("id_usuario_fk") String userId);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60"
    })
    @PATCH("rest/v1/usuario?id=eq.{userId}")
    Call<Void> actualizarUsuario(@Query("userId") String userId, @Body Usuario usuario);

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

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("rest/v1/usuario")
    Call<List<Usuario>> registrarUsuario(@Body Usuario usuario);

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1tZ3NlZHRja2ZwZWNheWF6aWZ0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyOTYwMjQsImV4cCI6MjA1NTg3MjAyNH0.tcJGKWsDY5pdBdJ7nyfbRVgqxQK7XH4ueON86XoaK60",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @POST("rest/v1/datos_usuario")
    Call<ResponseBody> registrarDatosUsuario(@Body DatosUsuario datosUsuario);
}
