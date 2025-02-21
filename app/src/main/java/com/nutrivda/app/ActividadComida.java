package com.nutrivda.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private String fechaActual;
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
        Button btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);

        // Obtener la fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fechaActual = sdf.format(Calendar.getInstance().getTime());
        tvFechaComida.setText("Comidas del día: " + fechaActual);

        // Cargar opciones en los Spinners
        if (verificarTablaComidas()) {
            cargarOpcionesComida(spinnerDesayuno, "Desayuno");
            cargarOpcionesComida(spinnerComida, "Comida");
            cargarOpcionesComida(spinnerCena, "Cena");
        } else {
            Toast.makeText(this, "⚠️ Error: No hay base de datos de comidas.", Toast.LENGTH_SHORT).show();
        }

        // Guardar selección de comidas
        btnGuardarComida.setOnClickListener(v -> guardarComidas());

        // Seleccionar Fecha
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());

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

        if (!desayunoHecho || !comidaHecha || !cenaHecha) {
            Toast.makeText(this, "Comida guardada, pero el día no será marcado en el calendario.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FECHA, fechaActual);
        values.put("completado", 1);

        long resultado = db.insertWithOnConflict(
                DatabaseHelper.TABLE_DIAS_COMPLETADOS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        if (resultado == -1) {
            Toast.makeText(this, "❌ Error al guardar en calendario", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "✅ Día guardado en calendario 🎉", Toast.LENGTH_SHORT).show();
        }
        db.close();
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

    // DatePicker para seleccionar fechas anteriores
    private void mostrarDatePicker() {
        Calendar calendario = Calendar.getInstance();
        datePicker = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    fechaActual = year + "-" + (month + 1) + "-" + day;
                    tvFechaComida.setText("Comidas del día: " + fechaActual);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

}