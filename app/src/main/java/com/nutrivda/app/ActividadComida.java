package com.nutrivda.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Spinner;
import android.app.DatePickerDialog;
import android.widget.Toast;

import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActividadComida extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private TextView tvFechaComida, tvTotalKcal;
    private TextView tvDesayunoSeleccionado, tvComidaSeleccionada, tvCenaSeleccionada;
    private TextView tvCaloriasDesayuno, tvCaloriasComida, tvCaloriasCena; // Campos de calorías
    private CheckBox cbDesayuno, cbComida, cbCena;
    private Button btnGuardarComida, btnAnadirDesayuno, btnAnadirComida, btnAnadirCena;
    private DatabaseHelper dbHelper;
    private String fechaDeComida;
    private double totalKcal = 0;
    private int caloriasDesayuno = 0, caloriasComida = 0, caloriasCena = 0;
    private boolean isEditar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_comida);

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Vincular elementos del layout
        tvFechaComida = findViewById(R.id.tvFechaComida);
        tvTotalKcal = findViewById(R.id.tvTotalKcal);
        tvDesayunoSeleccionado = findViewById(R.id.tvDesayunoSeleccionado);
        tvComidaSeleccionada = findViewById(R.id.tvComidaSeleccionada);
        tvCenaSeleccionada = findViewById(R.id.tvCenaSeleccionada);
        tvCaloriasDesayuno = findViewById(R.id.tvCaloriasDesayuno);
        tvCaloriasComida = findViewById(R.id.tvCaloriasComida);
        tvCaloriasCena = findViewById(R.id.tvCaloriasCena);
        btnAnadirDesayuno = findViewById(R.id.btnAnadirDesayuno);
        btnAnadirComida = findViewById(R.id.btnAnadirComida);
        btnAnadirCena = findViewById(R.id.btnAnadirCena);
        cbDesayuno = findViewById(R.id.cbDesayuno);
        cbComida = findViewById(R.id.cbComida);
        cbCena = findViewById(R.id.cbCena);
        btnGuardarComida = findViewById(R.id.btnGuardarComida);
        Button btnIrAtras = findViewById(R.id.btnIrAtras);

        // Obtener la fecha enviada desde MainActivity
        Intent intent = getIntent();
        if (intent.hasExtra("fechaSeleccionada")) {
            fechaDeComida = intent.getStringExtra("fechaSeleccionada");
            tvFechaComida.setText("Comidas del día: " + fechaDeComida);
            isEditar = intent.getBooleanExtra("isEditar", false);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(Calendar.getInstance().getTime());
            tvFechaComida.setText("Comidas del día: " + fechaDeComida);
        }

        if (isEditar) {
            btnGuardarComida.setText("Guardar cambios");
        }

        btnAnadirDesayuno.setOnClickListener(v -> abrirAniadirComida("Desayuno"));
        btnAnadirComida.setOnClickListener(v -> abrirAniadirComida("Comida"));
        btnAnadirCena.setOnClickListener(v -> abrirAniadirComida("Cena"));

        // Guardar selección de comidas
        btnGuardarComida.setOnClickListener(v -> guardarComidas());

        // Ir atrás
        btnIrAtras.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ActividadComida.this, MainActivity.class);
            startActivity(intentVolver);
        });
    }

    private void abrirAniadirComida(String tipoComida) {
        Intent intent = new Intent(this, AniadirComidaActivity.class);
        intent.putExtra("tipo_comida", tipoComida);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String comidaSeleccionada = data.getStringExtra("recetaSeleccionada");
            String tipoComida = data.getStringExtra("tipoComida");
            int caloriasComidaSeleccionada = data.getIntExtra("calorias", 0);

            if (tipoComida != null && comidaSeleccionada != null) {
                switch (tipoComida.toLowerCase()) {
                    case "desayuno":
                        tvDesayunoSeleccionado.setText(comidaSeleccionada);
                        caloriasDesayuno = caloriasComidaSeleccionada;
                        tvCaloriasDesayuno.setText("Calorías: " + caloriasDesayuno);
                        btnAnadirDesayuno.setText("Editar Desayuno");
                        break;
                    case "comida":
                        tvComidaSeleccionada.setText(comidaSeleccionada);
                        caloriasComida = caloriasComidaSeleccionada;
                        tvCaloriasComida.setText("Calorías: " + caloriasComida);
                        btnAnadirComida.setText("Editar Comida");
                        break;
                    case "cena":
                        tvCenaSeleccionada.setText(comidaSeleccionada);
                        caloriasCena = caloriasComidaSeleccionada;
                        tvCaloriasCena.setText("Calorías: " + caloriasCena);
                        btnAnadirCena.setText("Editar Cena");
                        break;
                }
                actualizarTotalKcal();
            }
        }
    }

    private void actualizarTotalKcal() {
        totalKcal = caloriasDesayuno + caloriasComida + caloriasCena;
        tvTotalKcal.setText("Total kcal: " + totalKcal);
    }

    private void guardarComidas() {
        boolean desayunoHecho = cbDesayuno.isChecked();
        boolean comidaHecha = cbComida.isChecked();
        boolean cenaHecha = cbCena.isChecked();

        String desayunoSeleccionado = desayunoHecho ? tvDesayunoSeleccionado.getText().toString() : null;
        String comidaSeleccionada = comidaHecha ? tvComidaSeleccionada.getText().toString() : null;
        String cenaSeleccionada = cenaHecha ? tvCenaSeleccionada.getText().toString() : null;

        boolean diaIncompleto = !desayunoHecho || !comidaHecha || !cenaHecha;

        if (diaIncompleto) {
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Día Incompleto")
                    .setMessage("Has registrado comidas, pero el día no será marcado como completo en el calendario. ¿Quieres continuar?")
                    .setPositiveButton("Guardar", (dialog, which) -> guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, desayunoHecho, comidaHecha, cenaHecha, false))
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, desayunoHecho, comidaHecha, cenaHecha, true);
    }

    private void guardarDatosComida(String desayuno, String comida, String cena, boolean swDesayuno, boolean swComida, boolean swCena, boolean marcarComoCompleto) {
        // Crear objeto JSON con los datos correctos para la tabla dias_completados
        Map<String, Object> comidaData = new HashMap<>();
        comidaData.put("fecha", fechaDeComida);
        comidaData.put("id_usuario_fk", 1); // TODO: Cambia este ID por el real del usuario mas adelante
        comidaData.put("completado", marcarComoCompleto);
        comidaData.put("desayuno", desayuno);
        comidaData.put("sw_desayuno", swDesayuno);
        comidaData.put("comida", comida);
        comidaData.put("sw_comida", swComida);
        comidaData.put("cena", cena);
        comidaData.put("sw_cena", swCena);

        // Hacer petición POST a Supabase
        SupabaseClient.getClient().create(SupabaseApi.class).insertarComida(comidaData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ActividadComida.this, "✅ Comida guardada correctamente en Supabase", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ActividadComida.this, "❌ Error al guardar en Supabase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ActividadComida.this, "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}