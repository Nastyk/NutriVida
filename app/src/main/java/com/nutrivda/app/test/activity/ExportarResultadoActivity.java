package com.nutrivda.app.test.activity;

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

import com.nutrivda.app.main.fragment.FragmentMasOpciones;
import com.nutrivda.app.R;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.ResultadoTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExportarResultadoActivity extends AppCompatActivity {

    private Button btnGenerarPDF;
    private ImageButton btnIrAtras;
    private SupabaseApi supabaseApi;
    private ResultadoTest resultadoTest;
    private DatosUsuario datosUsuario;
    private int userId;
    private double IMC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_resultado); // Aquí uso mi layout con el botón

        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);
        btnIrAtras = findViewById(R.id.btnIrAtras);

        userId = getUserId();

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        recuperarRespuestasTest();
        recuperarDatosUsuario();


        // Configuro el botón para que cuando lo pulse, se genere el PDF
        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarPDF();
            }
        });

        // Ir atrás
        btnIrAtras.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ExportarResultadoActivity.this, FragmentMasOpciones.class);
            startActivity(intentVolver);
        });
    }

    // Esta función se encarga de generar el PDF con los datos guardados
    private void generarPDF() {
        try {
            SharedPreferences prefs = getSharedPreferences("NutriVidaPrefs", MODE_PRIVATE);

            // Datos personales
            String nombre = datosUsuario.getNombre();
            String apellido1 = datosUsuario.getApellido1();
            String apellido2 = datosUsuario.getApellido2();
            String edad = String.valueOf(datosUsuario.getEdad());
            String peso =String.valueOf(datosUsuario.getPeso());
            String altura = String.valueOf(datosUsuario.getAltura());
            calcularIMC(datosUsuario.getPeso(), datosUsuario.getAltura());
            String imc = String.format("%.2f", IMC);

            // Respuestas de tests
            String actividad = resultadoTest.getActividad_fisica_respuesta();
            String objetivo = resultadoTest.getObjetivo_respuesta();
            String restricciones = resultadoTest.getRestricciones_respuesta();
            String organizacion = resultadoTest.getOrganizacion_respuesta();

            PdfDocument documento = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page pagina = documento.startPage(pageInfo);

            Canvas canvas = pagina.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(16);
            int x = 50;
            int y = 80;

            paint.setFakeBoldText(true);
            canvas.drawText("📄 Informe de Resultados - NutriVida", x, y, paint);
            paint.setFakeBoldText(false);
            y += 40;

            // Datos personales
            canvas.drawText("👤 Información personal:", x, y, paint);
            y += 30;
            canvas.drawText("Nombre: " + nombre, x, y, paint);
            y += 25;
            canvas.drawText("Apellido 1: " + apellido1, x, y, paint);
            y += 25;
            canvas.drawText("Apellido 2: " + apellido2, x, y, paint);
            y += 25;
            canvas.drawText("Edad: " + edad, x, y, paint);
            y += 25;
            canvas.drawText("Peso: " + peso + " kg", x, y, paint);
            y += 25;
            canvas.drawText("Altura: " + altura + " cm", x, y, paint);
            y += 25;
            canvas.drawText("IMC: " + imc, x, y, paint);
            y += 40;

            // Resultados de tests
            canvas.drawText("🧪 Resultados de tests:", x, y, paint);
            y += 30;
            canvas.drawText("Actividad física: " + actividad, x, y, paint);
            y += 25;
            canvas.drawText("Objetivo actual: " + objetivo, x, y, paint);
            y += 25;
            canvas.drawText("Restricciones alimentarias: " + restricciones, x, y, paint);
            y += 25;
            canvas.drawText("Organización de comidas: " + organizacion, x, y, paint);

            documento.finishPage(pagina);

            File directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String nombreArchivo = "nutrivida_resultado.pdf";
            File archivo = new File(directorioDescargas, nombreArchivo);


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

    private void recuperarRespuestasTest() {
        supabaseApi.obtenerResultadosTest("eq." + userId, "+").enqueue(new Callback<List<ResultadoTest>>() {
            @Override
            public void onResponse(Call<List<ResultadoTest>> call, Response<List<ResultadoTest>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    ResultadoTest responseTest = response.body().get(0);
                    if (responseTest != null) {
                        resultadoTest =responseTest;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ResultadoTest>> call, Throwable t) {

            }
        });
    }

    private void calcularIMC(double peso, double altura) {
        if (peso > 0 && altura > 0) {
            IMC = peso / ((altura / 100) * (altura / 100));
            //tvIMC.setText("IMC: " + String.format("%.2f", imc));
        } else {
            IMC = 0.0;
        }
    }

    private void recuperarDatosUsuario() {
        // Obtener datos desde la tabla datos_usuario
        supabaseApi.obtenerDatosUsuario("eq." + userId, "no-cache").enqueue(new Callback<List<DatosUsuario>>() {
            @Override
            public void onResponse(Call<List<DatosUsuario>> call, Response<List<DatosUsuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DatosUsuario datos = response.body().get(0);
                    if (datos != null) {
                        datosUsuario = datos;
                    }
                } else {
                    Toast.makeText(ExportarResultadoActivity.this, "No se encontraron datos adicionales del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DatosUsuario>> call, Throwable t) {
                Toast.makeText(ExportarResultadoActivity.this, "Error al obtener datos del usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }

}
