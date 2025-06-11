package com.nutrivda.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadPerfil extends AppCompatActivity {

    private EditText etNombre, etApellido1, etApellido2, etPeso, etAltura, etEdad;
    private TextView tvIMC;
    private ImageButton btnAtras;
    private int userId;
    private SupabaseApi supabaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_perfil);

        // Obtener el userId del Intent
        userId = getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Error: ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Vincular elementos del layout
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        etEdad = findViewById(R.id.etEdad);
        tvIMC = findViewById(R.id.tvIMCPerfil);
        btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> {
            setResult(DetalleComidaActivity.RESULT_CANCELED);
            finish();
        });

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        // Obtener datos desde la tabla usuario
        supabaseApi.obtenerUsuario("eq." + userId).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Usuario usuario = response.body().get(0);
                } else {
                    Toast.makeText(ActividadPerfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(ActividadPerfil.this, "Error al obtener usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener datos desde la tabla datos_usuario
        supabaseApi.obtenerDatosUsuario("eq." + userId, "no-cache").enqueue(new Callback<List<DatosUsuario>>() {
            @Override
            public void onResponse(Call<List<DatosUsuario>> call, Response<List<DatosUsuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DatosUsuario datos = response.body().get(0);
                    etNombre.setText(datos.getNombre());
                    etApellido1.setText(datos.getApellido1());
                    etApellido2.setText(datos.getApellido2());
                    etPeso.setText(String.valueOf(datos.getPeso()));
                    etAltura.setText(String.valueOf(datos.getAltura()));
                    etEdad.setText(String.valueOf(datos.getEdad()));
//                    etActividad.setText(datos.getActividad_fisica());
                    calcularIMC(datos.getPeso(), datos.getAltura());
                } else {
                    Toast.makeText(ActividadPerfil.this, "No se encontraron datos adicionales del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DatosUsuario>> call, Throwable t) {
                Toast.makeText(ActividadPerfil.this, "Error al obtener datos del usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularIMC(double peso, double altura) {
        if (peso > 0 && altura > 0) {
            double imc = peso / ((altura / 100) * (altura / 100));
            tvIMC.setText("IMC: " + String.format("%.2f", imc));
        } else {
            tvIMC.setText("IMC: No disponible");
        }
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }
}