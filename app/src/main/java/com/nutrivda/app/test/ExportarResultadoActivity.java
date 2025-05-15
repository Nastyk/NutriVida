package com.nutrivda.app.test;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutrivda.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportarResultadoActivity extends AppCompatActivity {

    private Button btnGenerarPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_resultado); // Aquí uso mi layout con el botón

        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);

        // Configuro el botón para que cuando lo pulse, se genere el PDF
        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarPDF();
            }
        });
    }

    // Esta función se encarga de generar el PDF con los datos guardados
    private void generarPDF() {
        // Obtengo los datos desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);
        String emocional = prefs.getString("riesgoEmocional", "Sin datos");
        String fisico = prefs.getString("riesgoFisico", "Sin datos");
        String profesional = prefs.getString("riesgoProfesional", "Sin datos");
        String calorias = prefs.getString("rangoCalorico", "Sin datos");

        // Creo el documento PDF y una página con tamaño A4
        PdfDocument documento = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page pagina = documento.startPage(pageInfo);

        // Dibujo el contenido con Canvas
        Canvas canvas = pagina.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(16);

        int x = 50;
        int y = 100;

        canvas.drawText("Resumen NutriVida", x, y, paint);
        y += 40;
        canvas.drawText("Riesgo emocional: " + emocional, x, y, paint);
        y += 30;
        canvas.drawText("Riesgo físico: " + fisico, x, y, paint);
        y += 30;
        canvas.drawText("Riesgo profesional: " + profesional, x, y, paint);
        y += 30;
        canvas.drawText("Rango calórico recomendado: " + calorias, x, y, paint);

        documento.finishPage(pagina);

        // Guardo el archivo en la carpeta Descargas del dispositivo
        File directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String nombreArchivo = "nutrivida_resultado.pdf";
        File archivo = new File(directorioDescargas, nombreArchivo);

        try {
            FileOutputStream fos = new FileOutputStream(archivo);
            documento.writeTo(fos);
            documento.close();
            fos.close();

            Toast.makeText(this, "PDF generado en Descargas como " + nombreArchivo, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
