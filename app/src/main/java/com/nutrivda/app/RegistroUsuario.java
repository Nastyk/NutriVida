package com.nutrivda.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.Usuario;
import com.nutrivda.app.test.TestEmocionalActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuario extends AppCompatActivity {

    private EditText etUsuario, etPassword, etNombre, etApellido1, etApellido2, etPeso, etAltura, etEdad;
    private SupabaseApi supabaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        // Inicializo la API
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Referencio los elementos del formulario
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        etEdad = findViewById(R.id.etEdad);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Capturo los datos del formulario
        String usuario = etUsuario.getText().toString();
        String password = etPassword.getText().toString();
        String nombre = etNombre.getText().toString();
        String apellido1 = etApellido1.getText().toString();
        String apellido2 = etApellido2.getText().toString();
        String pesoStr = etPeso.getText().toString();
        String alturaStr = etAltura.getText().toString();
        String edadStr = etEdad.getText().toString();

        // Validación simple
        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double peso = Double.parseDouble(pesoStr);
            double altura = Double.parseDouble(alturaStr);
            int edad = Integer.parseInt(edadStr);

            // Construyo el objeto DatosUsuario
            DatosUsuario datos = new DatosUsuario(
                    peso,
                    altura,
                    edad,
                    nombre,
                    apellido1,
                    apellido2,
                    usuario
            );

            // Construyo el objeto Usuario con los datos personales
            Usuario nuevoUsuario = new Usuario(0, password, usuario);

            // Llamo a la API de Supabase para registrar
            supabaseApi.registrarUsuario(nuevoUsuario).enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistroUsuario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        // Marco al usuario como nuevo en SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("nuevoUsuario", true);
                        editor.putBoolean("yaCompletoTest", false);
                        editor.apply();

                        // Redirijo al primer test: emocional
                        Intent intent = new Intent(RegistroUsuario.this, TestEmocionalActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("RegistroUsuario", "Código: " + response.code());

                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("RegistroUsuario", "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("RegistroUsuario", "No se pudo leer el error: " + e.getMessage());
                        }

                        Toast.makeText(RegistroUsuario.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    Toast.makeText(RegistroUsuario.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e("RegistroUsuario", "ERROR de conexión al registrar usuario", t);

                    Log.e("RegistroUsuario", "Mensaje: " + t.getMessage());
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingresa peso, altura y edad válidos", Toast.LENGTH_SHORT).show();
        }
    }
}
