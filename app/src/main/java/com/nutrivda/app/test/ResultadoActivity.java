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
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.nutrivda.app.BaseActivity;
import com.nutrivda.app.R;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvTipoTest, tvResultado, tvResumen;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvTipoTest = findViewById(R.id.tvTipoTest);
        tvResultado = findViewById(R.id.tvResultado);
        tvResumen = findViewById(R.id.tvResumen);
        btnVolver = findViewById(R.id.btnVolverResultado);

        // Recibo los datos del intent
        Intent intent = getIntent();
        String tipo = intent.getStringExtra("tipoTest");
        String resultado = intent.getStringExtra("resultado");
        String resumen = intent.getStringExtra("resumen");

        // Los muestro en pantalla
        tvTipoTest.setText("Evaluación: " + tipo);
        tvResultado.setText(resultado);
        tvResumen.setText(resumen);
        guardarEnHistorial(tipo, resultado, resumen);

        // Botón para volver al menú (BaseActivity)
        btnVolver.setOnClickListener(v -> {
            Intent volver = new Intent(ResultadoActivity.this, BaseActivity.class);
            startActivity(volver);
            finish();
        });

        String respuestaIA = getIntent().getStringExtra("respuestaIA");
        TextView tvResultado = findViewById(R.id.tvResultado);
        tvResultado.setText(respuestaIA); // Puedes formatearlo si lo deseas

    }
    private void guardarEnHistorial(String tipo, String resultado, String resumen) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        // Recupero historial actual
        String json = prefs.getString("historial_resultados", "[]");
        Type type = new TypeToken<ArrayList<ResultadoTest>>() {}.getType();
        ArrayList<ResultadoTest> historial = gson.fromJson(json, type);

        // Creo nuevo resultado
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        ResultadoTest nuevo = new ResultadoTest(tipo, resultado, resumen, fecha);
        historial.add(nuevo);

        // Guardo actualizado
        String actualizado = gson.toJson(historial);
        prefs.edit().putString("historial_resultados", actualizado).apply();
    }


}

