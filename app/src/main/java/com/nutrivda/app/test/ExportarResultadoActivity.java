package com.nutrivda.app.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.FragmentConfiguracion;
import com.nutrivda.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportarResultadoActivity extends AppCompatActivity {

    private Button btnGenerarPDF;
    private ImageButton btnIrAtras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_resultado); // Aquí uso mi layout con el botón

        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);
        btnIrAtras = findViewById(R.id.btnIrAtras);




        // Configuro el botón para que cuando lo pulse, se genere el PDF
        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarPDF();
            }
        });

        // Ir atrás
        btnIrAtras.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ExportarResultadoActivity.this, FragmentConfiguracion.class);
            startActivity(intentVolver);
        });
    }

    // Esta función se encarga de generar el PDF con los datos guardados
    private void generarPDF() {
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);

        // Datos guardados previamente
        String emocional = prefs.getString("riesgoEmocional", "Sin datos");
        String fisico = prefs.getString("riesgoFisico", "Sin datos");
        String profesional = prefs.getString("riesgoProfesional", "Sin datos");
        String planIA = prefs.getString("planNutricionalTexto", "Plan personalizado no disponible");
        String resultadoSemanal = prefs.getString("resultadoSemanal", "No registrado");

        // Crear PDF
        PdfDocument documento = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page pagina = documento.startPage(pageInfo);

        Canvas canvas = pagina.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(16);
        int x = 50;
        int y = 80;

        // Título
        paint.setFakeBoldText(true);
        canvas.drawText("📄 Informe de Resultados - NutriVida", x, y, paint);
        paint.setFakeBoldText(false);
        y += 40;

        // Sección 1: Tests
        canvas.drawText("🧪 Resultados de tests:", x, y, paint);
        y += 30;
        canvas.drawText("🧠 Riesgo emocional: " + emocional, x, y, paint);
        y += 25;
        canvas.drawText("💪 Riesgo físico: " + fisico, x, y, paint);
        y += 25;
        canvas.drawText("🧑‍💼 Riesgo profesional: " + profesional, x, y, paint);
        y += 40;

        // Sección 2: Resumen Semanal
        canvas.drawText("📊 Resumen semanal:", x, y, paint);
        y += 30;
        String[] resumenLines = resultadoSemanal.split("\n");
        for (String line : resumenLines) {
            canvas.drawText(line, x, y, paint);
            y += 25;
        }
        y += 20;

        // Sección 3: Plan IA
        canvas.drawText("🧠 Plan nutricional IA:", x, y, paint);
        y += 30;
        String[] planLines = planIA.split("\n");
        for (String line : planLines) {
            canvas.drawText(line, x, y, paint);
            y += 25;
            if (y > 800) break; // corta si se sale de la hoja
        }

        documento.finishPage(pagina);

        File directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String nombreArchivo = "nutrivida_resultado.pdf";
        File archivo = new File(directorioDescargas, nombreArchivo);

        try {
            FileOutputStream fos = new FileOutputStream(archivo);
            documento.writeTo(fos);
            documento.close();
            fos.close();
            Toast.makeText(this, "✅ PDF generado: " + nombreArchivo, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
