package com.nutrivda.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.callback.UserIdCallback;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.Usuario;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
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

        // Inicializar API
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Referencias a los elementos
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        etEdad = findViewById(R.id.etEdad);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> {
            goToLogin();
        });

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Obtener valores
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellido1 = etApellido1.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();

        // Validar que todos los campos estén llenos
        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() ||
                pesoStr.isEmpty() || alturaStr.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "⚠️ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        double altura = Double.parseDouble(alturaStr);
        int edad = Integer.parseInt(edadStr);

        obtenerUserId(usuario, password, new UserIdCallback() {
            @Override
            public void onUserIdReceived(int userId) {
                if (userId != 0) {
                    Toast.makeText(RegistroUsuario.this, "⚠️ El usuario ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                } else {
                    // Registrar usuario en la tabla usuario
                    Usuario nuevoUsuario = new Usuario(usuario, password);
                    supabaseApi.registrarUsuario(nuevoUsuario).enqueue(new Callback<List<Usuario>>() {
                        @Override
                        public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                int userId = response.body().get(0).getId();  // ✅ Obtenemos el primer usuario de la lista

                                // Ahora registrar en la tabla datos_usuario con el ID recuperado
                                registrarDatosUsuario(userId, nombre, apellido1, apellido2, peso, altura, edad);
                            } else {
                                Toast.makeText(RegistroUsuario.this, "Error al registrar usuario: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Usuario>> call, Throwable t) {
                            Toast.makeText(RegistroUsuario.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void registrarDatosUsuario(int userId, String nombre, String apellido1, String apellido2, double peso, double altura, int edad) {
        //int userId = obtenerUSerId(usuario, contraseña);

        DatosUsuario datosUsuario = new DatosUsuario(userId, peso, altura, edad, nombre, apellido1, apellido2, "ACTIVO", 0, false);

        supabaseApi.registrarDatosUsuario(datosUsuario).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201 || response.code() == 204) {
                    Toast.makeText(RegistroUsuario.this, "✅ Registro exitoso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegistroUsuario.this, "⚠️ Error al registrar datos: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistroUsuario.this, "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerUserId(String username, String password, UserIdCallback callback) {
        supabaseApi.verificarUsuarioPorUsername("eq." + username)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Map<String, Object> user = response.body().get(0);
                            int userId = ((Double) user.get("id")).intValue();  // ✅ Obtener ID del usuario
                            callback.onUserIdReceived(userId);  // ✅ Pasamos el resultado al callback
                        } else {
                            callback.onUserIdReceived(0);  // Usuario no encontrado
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                        callback.onUserIdReceived(0);  // Error de conexión
                    }
                });
    }


    private void goToLogin() {
        Intent intent = new Intent(RegistroUsuario.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
