package com.nutrivda.app.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;

public class TestFisicoActivity extends AppCompatActivity {

    // Declaro las variables para los grupos de radio y el botón
    private RadioGroup rgEjercicio, rgSedentarismo, rgLimitacion;
    private Button btnEvaluarFisico;
    private String riesgoEmocional; // Variable para guardar el resultado anterior

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fisico); // Enlazo con el layout de la pantalla física

        // Conecto los elementos del layout con el código Java
        rgEjercicio = findViewById(R.id.rgEjercicio);
        rgSedentarismo = findViewById(R.id.rgSedentarismo);
        rgLimitacion = findViewById(R.id.rgLimitacion);
        btnEvaluarFisico = findViewById(R.id.btnEvaluarFisico);

        // Recupero el dato de la pantalla anterior (emocional)
        riesgoEmocional = getIntent().getStringExtra("riesgoEmocional");

        // Configuro qué pasa cuando el usuario pulsa el botón "Siguiente"
        btnEvaluarFisico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluarFisico(); // Llamo a mi función que calcula el nivel de riesgo físico
            }
        });
    }

    // Esta función recoge las respuestas del usuario, las convierte a valores y calcula el resultado
    private void evaluarFisico() {
        int ejercicio = obtenerValorEjercicio();
        int sedentarismo = obtenerValorDesdeRadioGroup(rgSedentarismo);
        int limitacion = obtenerValorDesdeRadioGroup(rgLimitacion);

        int totalFisico = ejercicio + sedentarismo + limitacion;
        String resultado = clasificarRiesgo(totalFisico);

        // Paso los dos resultados acumulados a la siguiente pantalla (profesional)
        Intent intent = new Intent(TestFisicoActivity.this, TestProfesionalActivity.class);
        intent.putExtra("riesgoEmocional", riesgoEmocional);
        intent.putExtra("riesgoFisico", resultado);
        startActivity(intent);
        finish();
    }

    // Esta función específica traduce los valores del ejercicio físico según el número de días
    private int obtenerValorEjercicio() {
        int id = rgEjercicio.getCheckedRadioButtonId();
        if (id == -1) return 0;
        RadioButton seleccionado = findViewById(id);
        String texto = seleccionado.getText().toString();

        // Asigno valores según la frecuencia de ejercicio
        if (texto.contains("6")) return 0;            // 6+ días → saludable
        else if (texto.contains("3-5")) return 1;      // regular
        else return 2;                                  // poco o nada de ejercicio
    }

    // Esta función traduce las respuestas en valores de riesgo
    private int obtenerValorDesdeRadioGroup(RadioGroup grupo) {
        int id = grupo.getCheckedRadioButtonId();
        if (id == -1) return 0;
        RadioButton seleccionado = findViewById(id);
        String texto = seleccionado.getText().toString();

        if (texto.equalsIgnoreCase("No") || texto.contains("0-4")) return 0;
        else if (texto.equalsIgnoreCase("Leve") || texto.contains("5-8")) return 1;
        else return 2;
    }

    // Esta función evalúa el total de puntos y devuelve una etiqueta de riesgo
    private String clasificarRiesgo(int valor) {
        if (valor <= 1) return "Bajo";
        if (valor <= 3) return "Medio";
        return "Alto";
    }
}

