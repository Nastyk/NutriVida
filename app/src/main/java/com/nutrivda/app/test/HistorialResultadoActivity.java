package com.nutrivda.app.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;

public class HistorialResultadoActivity extends AppCompatActivity {

    // Declaro los TextView que mostrarán los resultados guardados
    private TextView tvHistorialEmocional, tvHistorialFisico, tvHistorialProfesional, tvHistorialRango;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_resultado); // Enlazo con el layout que mostraré

        // Enlazo cada TextView con su ID desde el layout
        tvHistorialEmocional = findViewById(R.id.tvHistorialEmocional);
        tvHistorialFisico = findViewById(R.id.tvHistorialFisico);
        tvHistorialProfesional = findViewById(R.id.tvHistorialProfesional);
        tvHistorialRango = findViewById(R.id.tvHistorialRango);

        // Recupero los datos guardados desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
        String emocional = prefs.getString("riesgoEmocional", "No disponible");
        String fisico = prefs.getString("riesgoFisico", "No disponible");
        String profesional = prefs.getString("riesgoProfesional", "No disponible");
        String rango = prefs.getString("rangoCalorico", "No disponible");

        // Muestro los datos en los TextView
        tvHistorialEmocional.setText("Riesgo emocional guardado: " + emocional);
        tvHistorialFisico.setText("Riesgo físico guardado: " + fisico);
        tvHistorialProfesional.setText("Riesgo profesional guardado: " + profesional);
        tvHistorialRango.setText("Rango calórico guardado: " + rango);
    }
}
