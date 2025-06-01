package com.nutrivda.app.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrivda.app.BaseActivity;
import com.nutrivda.app.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvResultado;
    private Button btnComenzarTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvResultado = findViewById(R.id.tvResultado);
        btnComenzarTests = findViewById(R.id.btnComenzarTests);

        // Obtengo los datos que llegan con el Intent
        Intent recibido = getIntent();
        String respuestaIA = recibido.getStringExtra("respuestaIA");
        String tipo = recibido.getStringExtra("tipoTest");
        String resultado = recibido.getStringExtra("resultado");
        String resumen = recibido.getStringExtra("resumen");

        if (respuestaIA != null) {
            // Si viene desde la IA (onboarding)
            tvResultado.setText(respuestaIA);
        } else {
            // Si viene desde un test (emocional, físico o profesional)
            tvResultado.setText(resultado != null ? resultado : "Resultado no disponible");

            // Guardo en historial solo si es resultado de test
            guardarEnHistorial(tipo, resultado, resumen);
        }

        // Botón para comenzar los tests
        btnComenzarTests.setOnClickListener(v -> {
            // Lanzo el test emocional como inicio de la cadena
            Intent comenzar = new Intent(ResultadoActivity.this, TestEmocionalActivity.class);
            startActivity(comenzar);
            finish();
        });
    }

    private void guardarEnHistorial(String tipo, String resultado, String resumen) {
        if (tipo == null || resultado == null || resumen == null) return;

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("historial_resultados", "[]");
        Type type = new TypeToken<ArrayList<ResultadoTest>>() {}.getType();
        ArrayList<ResultadoTest> historial = gson.fromJson(json, type);

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        ResultadoTest nuevo = new ResultadoTest(tipo, resultado, resumen, fecha);
        historial.add(nuevo);

        String actualizado = gson.toJson(historial);
        prefs.edit().putString("historial_resultados", actualizado).apply();
    }
}
