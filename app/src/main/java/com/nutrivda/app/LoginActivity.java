package com.nutrivda.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SupabaseApi supabaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegistro = findViewById(R.id.btnRegistro);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            autenticarUsuario(username, password);
        });

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroUsuario.class);
            startActivity(intent);
        });
    }

    private void autenticarUsuario(String username, String password) {
        supabaseApi.verificarUsuario("eq." + username, "eq." + password)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Map<String, Object> user = response.body().get(0);
                            int userId = ((Double) user.get("id")).intValue();  // Obtener ID del usuario

                            saveLoginState(userId);
                            goToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "❌ Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "⚠️ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginState(int userId) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("userId", userId);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ActividadComida.class);
        startActivity(intent);
        finish();
    }
}