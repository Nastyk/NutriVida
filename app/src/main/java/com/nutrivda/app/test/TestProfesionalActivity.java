package com.nutrivda.app.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;

public class TestProfesionalActivity extends AppCompatActivity {

    // Declaro los elementos que voy a usar en esta pantalla: 3 RadioGroups y 1 botón
    private RadioGroup rgJornada, rgImpactoAlimentacion, rgDesorganizacion;
    private Button btnEvaluarProfesional;
    private String riesgoEmocional, riesgoFisico; // Variables para traer los datos anteriores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_profesional); // Enlazo con el layout XML correspondiente

        // Enlazo mis elementos visuales con las variables en Java
        rgJornada = findViewById(R.id.rgJornada);
        rgImpactoAlimentacion = findViewById(R.id.rgImpactoAlimentacion);
        rgDesorganizacion = findViewById(R.id.rgDesorganizacion);
        btnEvaluarProfesional = findViewById(R.id.btnEvaluarProfesional);

        // Recupero los valores de riesgo emocional y físico
        riesgoEmocional = getIntent().getStringExtra("riesgoEmocional");
        riesgoFisico = getIntent().getStringExtra("riesgoFisico");

        // Configuro qué sucede al pulsar el botón "Siguiente"
        btnEvaluarProfesional.setOnClickListener(v -> {
            String r1 = obtenerTextoSeleccionado(findViewById(R.id.rgJornada));
            String r2 = obtenerTextoSeleccionado(findViewById(R.id.rgImpactoAlimentacion));
            String r3 = obtenerTextoSeleccionado(findViewById(R.id.rgDesorganizacion));

            int score = puntuacionProfesional(r1) + puntuacionProfesional(r2) + puntuacionProfesional(r3);

            String resultado;
            if (score >= 5) resultado = "Estable 🟢";
            else if (score >= 3) resultado = "Inestable 🟡";
            else resultado = "Riesgo 🟥";

            String resumen = "• Jornada: " + r1 +
                    "\n• Alimentación afectada: " + r2 +
                    "\n• Rutina diaria: " + r3;

            Intent intent = new Intent(TestProfesionalActivity.this, ResultadoActivity.class);
            intent.putExtra("tipoTest", "profesional");
            intent.putExtra("resultado", resultado);
            intent.putExtra("resumen", resumen);
            startActivity(intent);
            finish();
        });
    }

    // Esta función procesa las respuestas y calcula el nivel de riesgo profesional
    // Esta función procesa las respuestas y calcula el nivel de riesgo profesional
    private void evaluarProfesional() {
        int jornada = obtenerValorDesdeRadioGroup(rgJornada);
        int impacto = obtenerValorDesdeRadioGroup(rgImpactoAlimentacion);
        int desorganizacion = obtenerValorDesdeRadioGroup(rgDesorganizacion);

        int totalProfesional = jornada + impacto + desorganizacion;
        String resultado = clasificarRiesgo(totalProfesional);

        // Marco que ya completó los tests para que no vuelvan a aparecer automáticamente
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("yaCompletoTest", true);
        editor.apply();

        // Paso los tres resultados acumulados a la pantalla de resultados finales
        Intent intent = new Intent(TestProfesionalActivity.this, ResultadoActivity.class);
        intent.putExtra("riesgoEmocional", riesgoEmocional);
        intent.putExtra("riesgoFisico", riesgoFisico);
        intent.putExtra("riesgoProfesional", resultado);
        startActivity(intent);
        finish();
    }


    // Esta función traduce cada selección de un RadioGroup a un valor de riesgo: 0, 1 o 2
    private int obtenerValorDesdeRadioGroup(RadioGroup grupo) {
        int id = grupo.getCheckedRadioButtonId();
        if (id == -1) return 0; // Por si el usuario no marca nada

        RadioButton seleccionado = findViewById(id);
        String texto = seleccionado.getText().toString();

        if (texto.equalsIgnoreCase("No") || texto.contains("0-4")) return 0;
        else if (texto.equalsIgnoreCase("Un poco") || texto.contains("5-8")) return 1;
        else return 2; // "Sí", "Mucho", o "9h+"
    }

    // Esta función me ayuda a interpretar el resultado: Bajo, Medio o Alto
    private String clasificarRiesgo(int valor) {
        if (valor <= 1) return "Bajo";
        if (valor <= 3) return "Medio";
        return "Alto";
    }
    private int puntuacionProfesional(String respuesta) {
        switch (respuesta) {
            case "0-4h":
            case "No":
                return 2;
            case "5-8h":
            case "Un poco":
            case "A veces":
                return 1;
            default:
                return 0;
        }
    }
    private String obtenerTextoSeleccionado(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        if (id != -1) {
            RadioButton rb = findViewById(id);
            return rb.getText().toString();
        }
        return "";
    }
}

