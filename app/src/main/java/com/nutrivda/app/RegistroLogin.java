package com.nutrivda.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroLogin extends AppCompatActivity {
    private EditText etUsuario, etCorreo, etContrasena, etConfContrasena;
    private Button btnRegistrar;
    private TextView tvYaTienesCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_login);

        etUsuario = findViewById(R.id.inputUsername);
        etCorreo = findViewById(R.id.inputEmail);
        etContrasena = findViewById(R.id.inputPassword);
        etConfContrasena = findViewById(R.id.inputPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvYaTienesCuenta = findViewById(R.id.yaTienesCuenta);

        //Ir al Main
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroLogin.this, LoginActivity.class);
            startActivity(intent);
        });
        //Ir al login si ya tienes cuenta y entraste por error
        tvYaTienesCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroLogin.this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private void registrarUsu() {
        String usuario = etUsuario.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String ConfContrasena = etConfContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(ConfContrasena)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar datos en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Usuarios", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("usuario", usuario);
        editor.putString("correo", correo);
        editor.putString("contrasena", contrasena);
        editor.putString("ConfContrasena", ConfContrasena);
        editor.apply();

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

        // Redirigir al Login
        startActivity(new Intent(RegistroLogin.this, LoginActivity.class));
        finish();
    }
}
