package com.nutrivda.app.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nutrivda.app.R;

import java.util.ArrayList;

public class ResumenGraficoActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_grafico); // Enlazo con el XML que contiene el gráfico

        barChart = findViewById(R.id.barChart); // Conecto el BarChart con la vista

        // Recupero los datos guardados en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
        String emocional = prefs.getString("riesgoEmocional", "Bajo");
        String fisico = prefs.getString("riesgoFisico", "Bajo");
        String profesional = prefs.getString("riesgoProfesional", "Bajo");

        // Convierto cada nivel textual a un valor numérico para graficar
        int valEmocional = convertirRiesgoANumero(emocional);
        int valFisico = convertirRiesgoANumero(fisico);
        int valProfesional = convertirRiesgoANumero(profesional);

        // Creo las entradas del gráfico con los valores numéricos
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, valEmocional));
        entries.add(new BarEntry(1, valFisico));
        entries.add(new BarEntry(2, valProfesional));

        // Etiquetas del eje X
        ArrayList<String> etiquetas = new ArrayList<>();
        etiquetas.add("Emocional");
        etiquetas.add("Físico");
        etiquetas.add("Profesional");

        // Configuro el conjunto de datos para el gráfico
        BarDataSet dataSet = new BarDataSet(entries, "Nivel de Riesgo");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Verde suave
        dataSet.setValueTextSize(14f);

        // Combino los datos con las etiquetas
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Configuración del eje X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Configuración del eje Y
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(3f);
        barChart.getAxisRight().setEnabled(false);

        // Otros ajustes visuales
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000); // Animación al cargar
    }

    // Esta función me ayuda a convertir un nivel de riesgo a un valor numérico
    private int convertirRiesgoANumero(String riesgo) {
        switch (riesgo) {
            case "Medio": return 1;
            case "Alto": return 2;
            default: return 0; // Bajo
        }
    }
}
