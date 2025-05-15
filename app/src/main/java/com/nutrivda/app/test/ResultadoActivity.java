package com.nutrivda.app.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.nutrivda.app.R;

public class ResultadoActivity extends AppCompatActivity {

    // Declaro mis elementos visuales: los TextView para mostrar resultados y el botón
    private TextView tvResultadoEmocional, tvResultadoFisico, tvResultadoProfesional;
    private TextView tvAdvertencia, tvRangoCalorico;
    private Button btnReiniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado); // Enlazo con el layout XML de resultados

        // Conecto los elementos del layout con las variables de Java
        tvResultadoEmocional = findViewById(R.id.tvResultadoEmocional);
        tvResultadoFisico = findViewById(R.id.tvResultadoFisico);
        tvResultadoProfesional = findViewById(R.id.tvResultadoProfesional);
        tvAdvertencia = findViewById(R.id.tvAdvertencia);
        tvRangoCalorico = findViewById(R.id.tvRangoCalorico);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        // Aquí voy a recibir los resultados desde otras actividades (simulados por ahora)
        String emocional = getIntent().getStringExtra("riesgoEmocional");
        String fisico = getIntent().getStringExtra("riesgoFisico");
        String profesional = getIntent().getStringExtra("riesgoProfesional");

        if (emocional == null) emocional = "Medio";       // Si no recibo nada, uso valor ejemplo
        if (fisico == null) fisico = "Alto";
        if (profesional == null) profesional = "Medio";

        // Muestro los niveles recibidos en pantalla
        tvResultadoEmocional.setText("Riesgo Emocional: " + emocional);
        tvResultadoFisico.setText("Riesgo Físico: " + fisico);
        tvResultadoProfesional.setText("Riesgo Profesional: " + profesional);

        // Calculo cuántos están en alto riesgo para mostrar advertencia y rango calórico
        int riesgosAltos = 0;
        if (emocional.equals("Alto")) riesgosAltos++;
        if (fisico.equals("Alto")) riesgosAltos++;
        if (profesional.equals("Alto")) riesgosAltos++;

        // Si hay 2 o más riesgos altos, muestro advertencia
        if (riesgosAltos >= 2) {
            tvAdvertencia.setText("⚠ Presentas múltiples factores de riesgo. Atención especial recomendada.");
        } else {
            tvAdvertencia.setText("✓ Perfil dentro de rangos manejables.");
        }

        // Defino el rango calórico recomendado según el nivel de riesgo global
        String rango;
        if (riesgosAltos == 0) {
            rango = "2000 - 2400 kcal/día";
        } else if (riesgosAltos == 1) {
            rango = "1800 - 2100 kcal/día";
        } else {
            rango = "1500 - 1800 kcal/día";
        }
        tvRangoCalorico.setText("Rango calórico recomendado: " + rango);

        // Yo guardo los resultados en las preferencias para poder consultarlos más tarde
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("riesgoEmocional", emocional);
        editor.putString("riesgoFisico", fisico);
        editor.putString("riesgoProfesional", profesional);
        editor.putString("rangoCalorico", rango);
        editor.apply(); // Guardo todo de forma inmediata

        // Botón para reiniciar el test (vuelve al inicio o primera Activity)
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opcionalmente, borro resultados guardados
                getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(ResultadoActivity.this, TestEmocionalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resultado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_resumen_grafico) {
        // Si el usuario pulsa “Resumen gráfico”, abro esa pantalla
            Intent intent = new Intent(ResultadoActivity.this, ResumenGraficoActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

