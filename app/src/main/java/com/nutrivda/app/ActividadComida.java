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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Spinner;
import android.app.DatePickerDialog;
import android.widget.Toast;

import com.nutrivda.app.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ActividadComida extends AppCompatActivity {

    private TextView tvFechaComida, tvTotalKcal;
    private Spinner spinnerDesayuno, spinnerComida, spinnerCena;
    private CheckBox cbDesayuno, cbComida, cbCena;
    private Button btnGuardarComida;
    private DatabaseHelper dbHelper;
    private String fechaDeComida;
    private int totalKcal = 0;
    private DatePickerDialog datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_comida);

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Vincular elementos del layout
        tvFechaComida = findViewById(R.id.tvFechaComida);
        tvTotalKcal = findViewById(R.id.tvTotalKcal);
        spinnerDesayuno = findViewById(R.id.spinnerDesayuno);
        spinnerComida = findViewById(R.id.spinnerComida);
        spinnerCena = findViewById(R.id.spinnerCena);
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
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(Calendar.getInstance().getTime());
            tvFechaComida.setText("Comidas del día: " + fechaDeComida);
        }

        // Cargar opciones en los Spinners
        if (verificarTablaComidas()) {
            cargarOpcionesComida(spinnerDesayuno, "Desayuno");
            cargarOpcionesComida(spinnerComida, "Comida");
            cargarOpcionesComida(spinnerCena, "Cena");
        } else {
            Toast.makeText(this, "⚠️ Error: No hay base de datos de comidas.", Toast.LENGTH_SHORT).show();
        }

        cargarComidasDelDia(fechaDeComida);

        // Guardar selección de comidas
        btnGuardarComida.setOnClickListener(v -> guardarComidas());

        // Seleccionar Fecha
        btnIrAtras.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ActividadComida.this, MainActivity.class);
            startActivity(intentVolver);
        });

        // Listeners para los Spinners
        spinnerDesayuno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTotalKcal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerComida.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTotalKcal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCena.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarTotalKcal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Verifica si la tabla de comidas existe antes de hacer consultas
    private boolean verificarTablaComidas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + DatabaseHelper.TABLE_COMIDAS + "'", null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    // Carga las opciones de comidas desde la base de datos
    private void cargarOpcionesComida(Spinner spinner, String momento) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> opciones = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT nombre FROM " + DatabaseHelper.TABLE_COMIDAS + " WHERE momento = ?", new String[]{momento});
        while (cursor.moveToNext()) {
            opciones.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        if (opciones.isEmpty()) {
            opciones.add("No hay comidas registradas.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);
    }

    // Guarda las comidas seleccionadas y marca el día como completado si todas están seleccionadas
    private void guardarComidas() {
        boolean desayunoHecho = cbDesayuno.isChecked();
        boolean comidaHecha = cbComida.isChecked();
        boolean cenaHecha = cbCena.isChecked();

        String desayunoSeleccionado = desayunoHecho ? spinnerDesayuno.getSelectedItem().toString() : null;
        String comidaSeleccionada = comidaHecha ? spinnerComida.getSelectedItem().toString() : null;
        String cenaSeleccionada = cenaHecha ? spinnerCena.getSelectedItem().toString() : null;

        boolean diaIncompleto = !desayunoHecho || !comidaHecha || !cenaHecha;

        // Si el día está incompleto, mostrar alerta antes de guardar
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT fecha FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS + " WHERE fecha = ?", new String[]{fechaDeComida});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FECHA, fechaDeComida);
        values.put(DatabaseHelper.COLUMN_COMPLETADO, marcarComoCompleto ? 1 : 0);
        if (desayuno != null) values.put(DatabaseHelper.COLUMN_DESAYUNO, desayuno);
        if (comida != null) values.put(DatabaseHelper.COLUMN_COMIDA, comida);
        if (cena != null) values.put(DatabaseHelper.COLUMN_CENA, cena);
        values.put(DatabaseHelper.COLUMN_SW_DESAYUNO, swDesayuno ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_SW_COMIDA, swComida ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_SW_CENA, swCena ? 1 : 0);

        long resultado;
        if (existe) {
            resultado = db.update(DatabaseHelper.TABLE_DIAS_COMPLETADOS, values, "fecha = ?", new String[]{fechaDeComida});
        } else {
            resultado = db.insert(DatabaseHelper.TABLE_DIAS_COMPLETADOS, null, values);
        }

        db.close();

        if (resultado < 0) {
            Toast.makeText(this, "❌ Error al guardar en calendario", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "✅ Comida guardada correctamente", Toast.LENGTH_SHORT).show();
        }

        finish();
    }


    // Calcula las calorías totales de las comidas seleccionadas
    private void actualizarTotalKcal() {
        int kcalDesayuno = obtenerKcal(spinnerDesayuno);
        int kcalComida = obtenerKcal(spinnerComida);
        int kcalCena = obtenerKcal(spinnerCena);
        totalKcal = kcalDesayuno + kcalComida + kcalCena;
        tvTotalKcal.setText("Total kcal: " + totalKcal);
    }

    // Obtiene las calorías de la comida seleccionada en el Spinner
    private int obtenerKcal(Spinner spinner) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int kcal = 0;

        Cursor cursor = db.rawQuery(
                "SELECT kcal FROM " + DatabaseHelper.TABLE_COMIDAS + " WHERE nombre = ?",
                new String[]{spinner.getSelectedItem().toString()}
        );
        if (cursor.moveToFirst()) {
            kcal = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return kcal;
    }

    private void cargarComidasDelDia(String fechaSeleccionada) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        String desayunoGuardado = null;
        String comidaGuardada = null;
        String cenaGuardada = null;
        boolean swDesayuno = false;
        boolean swComida = false;
        boolean swCena = false;

        try {
            cursor = db.rawQuery(
                    "SELECT desayuno, comida, cena, swdesayuno, swcomida, swcena FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS +
                            " WHERE fecha = ?", new String[]{fechaSeleccionada});

            if (cursor.moveToFirst()) {
                desayunoGuardado = cursor.getString(0);
                comidaGuardada = cursor.getString(1);
                cenaGuardada = cursor.getString(2);
                swDesayuno = cursor.getInt(3) == 1;
                swComida = cursor.getInt(4) == 1;
                swCena = cursor.getInt(5) == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        if (desayunoGuardado != null) {
            seleccionarValorEnSpinner(spinnerDesayuno, desayunoGuardado);
        }
        if (comidaGuardada != null) {
            seleccionarValorEnSpinner(spinnerComida, comidaGuardada);
        }
        if (cenaGuardada != null) {
            seleccionarValorEnSpinner(spinnerCena, cenaGuardada);
        }
        cbDesayuno.setChecked(swDesayuno);
        cbComida.setChecked(swComida);
        cbCena.setChecked(swCena);
    }

    private void seleccionarValorEnSpinner(Spinner spinner, String valor) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(valor);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }



}