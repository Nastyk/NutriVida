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
import com.nutrivda.app.model.pojo.PlanNutricional;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvIMC, tvClasificacionIMC, tvCalorias, tvMacros,
            tvNivelActividad, tvTiempo, tvAnalisis;
    private Button btnComenzarTests;
    private PlanNutricional planNutricional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvIMC = findViewById(R.id.tvIMC);
        tvClasificacionIMC = findViewById(R.id.tvClasificacionIMC);
        tvCalorias = findViewById(R.id.tvCalorias);
        tvMacros = findViewById(R.id.tvMacros);
        tvNivelActividad = findViewById(R.id.tvNivelActividad);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvAnalisis = findViewById(R.id.tvAnalisis);
        btnComenzarTests = findViewById(R.id.btnComenzarTests);

        // Obtengo los datos que llegan con el Intent
        Intent recibido = getIntent();
        planNutricional = (PlanNutricional) getIntent().getSerializableExtra("planNutricional");

        String tipo = recibido.getStringExtra("tipoTest");
        String resultado = recibido.getStringExtra("resultado");
        String resumen = recibido.getStringExtra("resumen");

        if (planNutricional != null) {
            tvIMC.setText("IMC: " + planNutricional.getImc());
            tvClasificacionIMC.setText("Clasificación: " + planNutricional.getClasificacion_imc());
            tvCalorias.setText("Calorías recomendadas: " + planNutricional.getCalorias_recomendadas());

            PlanNutricional.MacroRecomendado macro = planNutricional.getMacro_recomendado();
            if (macro != null) {
                String macrosText = "Proteínas: " + macro.getProteinas() + "g\n" +
                        "Grasas: " + macro.getGrasas() + "g\n" +
                        "Carbohidratos: " + macro.getCarbohidratos() + "g";
                tvMacros.setText("Macros (g/día):\n" + macrosText);
            }

            tvNivelActividad.setText("Nivel de actividad recomendado: " + planNutricional.getNivel_actividad_recomendado());
            tvTiempo.setText("Tiempo estimado: " + planNutricional.getTiempo_estimado_para_lograr_objetivo());
            tvAnalisis.setText(planNutricional.getAnalisis_personalizado());
        }
        /*if (planNutricional != null) {
        } else {
            // Si viene desde un test (emocional, físico o profesional)
            tvResultado.setText(resultado != null ? resultado : "Resultado no disponible");

            // Guardo en historial solo si es resultado de test
            guardarEnHistorial(tipo, resultado, resumen);
        }*/

        // Botón para comenzar los tests
        btnComenzarTests.setOnClickListener(v -> {
            // Lanzo el test emocional como inicio de la cadena
            Intent comenzar = new Intent(ResultadoActivity.this, TestEmocionalActivity.class);
            startActivity(comenzar);
            finish();
        });
    }
}
