package com.nutrivda.app.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;

public class TestEmocionalActivity extends AppCompatActivity {

    // Aquí voy a declarar las variables para acceder a los RadioGroups y al botón
    private RadioGroup rgMotivacion, rgHumor, rgSueno;
    private Button btnSiguiente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_emocional); // Enlazo con el layout XML

        // Enlazo cada componente visual con su ID en el layout
        rgMotivacion = findViewById(R.id.rgMotivacion);
        rgHumor = findViewById(R.id.rgHumor);
        rgSueno = findViewById(R.id.rgSueno);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Ahora defino qué pasa cuando el usuario pulsa el botón "Siguiente"
        btnSiguiente.setOnClickListener(v -> {
        // Yo obtengo las respuestas seleccionadas de cada RadioGroup
            String r1 = obtenerTextoSeleccionado(findViewById(R.id.rgMotivacion));
            String r2 = obtenerTextoSeleccionado(findViewById(R.id.rgHumor));
            String r3 = obtenerTextoSeleccionado(findViewById(R.id.rgSueno));

            // Yo calculo la puntuación
            int score = puntuacionEmocional(r1) + puntuacionEmocional(r2) + puntuacionEmocional(r3);

            // Yo interpreto el resultado
            String resultado;
            if (score >= 5) resultado = "Saludable 🟢";
            else if (score >= 3) resultado = "Inestable 🟡";
            else resultado = "Riesgo emocional 🟥";

            // Yo genero el resumen visual
            String resumen = "• Motivación: " + r1 +
                    "\n• Humor: " + r2 +
                    "\n• Sueño: " + r3;

            // Yo paso los datos a la pantalla de resultado
            Intent intent = new Intent(TestEmocionalActivity.this, TestFisicoActivity.class);
            intent.putExtra("tipoTest", "emocional");
            intent.putExtra("resultado", resultado);
            intent.putExtra("resumen", resumen);
            startActivity(intent);
            finish();
        });
    }

    // Esta función evalúa las respuestas del test emocional y muestra un resultado
    private void evaluarEmocional() {
        // Declaro variables para guardar los puntajes según la respuesta del usuario
        int motivacion = obtenerValorDesdeRadioGroup(rgMotivacion);
        int humor = obtenerValorDesdeRadioGroup(rgHumor);
        int sueno = obtenerValorDesdeRadioGroup(rgSueno);

        // Sumo los valores para obtener el riesgo emocional total
        int totalEmocional = motivacion + humor + sueno;

        // Uso mi propia función para clasificar el riesgo (igual que en consola)
        String resultado = clasificarRiesgo(totalEmocional);

        // En lugar de usar un Toast, paso el resultado a la siguiente pantalla con un Intent
        Intent intent = new Intent(TestEmocionalActivity.this, TestFisicoActivity.class);
        intent.putExtra("riesgoEmocional", resultado);
        startActivity(intent);
        finish(); // Cierro esta pantalla para que no se acumule en la pila
    }

    // Esta función convierte la selección del usuario en un valor de riesgo (0, 1 o 2)
    private int obtenerValorDesdeRadioGroup(RadioGroup grupo) {
        int id = grupo.getCheckedRadioButtonId();

        // Si el usuario no ha respondido nada, devuelvo 0 por defecto (o puedes validar)
        if (id == -1) return 0;

        RadioButton seleccionado = findViewById(id);
        String texto = seleccionado.getText().toString();

        // Asigno valores en función del texto (esto se adapta a tus opciones exactas)
        if (texto.equalsIgnoreCase("Sí")) return 0;
        else if (texto.equalsIgnoreCase("A veces")) return 1;
        else return 2;
    }

    // Esta función la reutilizo para interpretar el total como "bajo", "medio" o "alto"
    private String clasificarRiesgo(int valor) {
        if (valor <= 1) return "Bajo";
        if (valor <= 3) return "Medio";
        return "Alto";
    }
    private String obtenerTextoSeleccionado(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        if (id != -1) {
            RadioButton rb = findViewById(id);
            return rb.getText().toString();
        }
        return "";
    }
    private int puntuacionEmocional(String respuesta) {
        switch (respuesta) {
            case "Sí": return 2;
            case "A veces": return 1;
            case "No": return 0;
            default: return 0;
        }
    }
}
