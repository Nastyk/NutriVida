package com.nutrivda.app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nutrivda.app.database.DatabaseHelper;

import java.util.Calendar;

public class ActividadPerfil extends AppCompatActivity {

    private EditText etNombre, etPeso, etAltura;
    private TextView tvIMC, tvRacha;
    private Button btnGuardar, btnIrMain;
    private DatabaseHelper dbHelper;
    private int diasCumplidos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_perfil);

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Vincular elementos
        etNombre = findViewById(R.id.etNombrePerfil);
        etPeso = findViewById(R.id.etPesoPerfil);
        etAltura = findViewById(R.id.etAlturaPerfil);
        tvIMC = findViewById(R.id.tvIMCPerfil);
        tvRacha = findViewById(R.id.tvRacha);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnIrMain = findViewById(R.id.btnIrAmain);

        // Cargar datos actuales del usuario
        cargarDatosUsuario();

        // Guardar cambios al presionar el botón
        btnGuardar.setOnClickListener(v -> guardarDatos());

        btnIrMain.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadPerfil.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarDatosUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PERSONA, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE);
            @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE));
            if (columnIndex != -1) {
                nombre = cursor.getString(columnIndex);
            }
            @SuppressLint("Range") double peso = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PESO));
            @SuppressLint("Range") double altura = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ALTURA));

            // Validar antes de calcular IMC
            if (peso > 0 && altura > 0) {
                double imc = peso / (altura * altura);
                tvIMC.setText("IMC: " + String.format("%.2f", imc));
            } else {
                tvIMC.setText("IMC: No disponible");
            }

            etNombre.setText(nombre);
            etPeso.setText(String.valueOf(peso));
            etAltura.setText(String.valueOf(altura));
        } else {
            tvIMC.setText("IMC: No disponible");
        }

        cursor.close();
        db.close();

        // Obtener racha de días cumplidos
        calcularDiasCumplidos();
    }

    private void calcularDiasCumplidos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        int añoActual = calendar.get(Calendar.YEAR);
        int mesActual = calendar.get(Calendar.MONTH) + 1;

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS +
                        " WHERE strftime('%Y', fecha) = ? AND strftime('%m', fecha) = ?",
                new String[]{String.valueOf(añoActual), String.format("%02d", mesActual)}
        );

        if (cursor.moveToFirst()) {
            diasCumplidos = cursor.getInt(0);
            tvRacha.setText("Días cumplidos este mes: " + diasCumplidos);
        } else {
            tvRacha.setText("Días cumplidos este mes: 0");
        }

        cursor.close();
        db.close();
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();

        if (nombre.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        double altura = Double.parseDouble(alturaStr);

        // Validación de valores lógicos
        if (peso <= 0 || peso > 300) {
            Toast.makeText(this, "Peso inválido (1 - 300 kg)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (altura <= 0.5 || altura > 2.5) {
            Toast.makeText(this, "Altura inválida (0.5 - 2.5 m)", Toast.LENGTH_SHORT).show();
            return;
        }

        double imc = peso / (altura * altura);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOMBRE, nombre);
        values.put(DatabaseHelper.COLUMN_PESO, peso);
        values.put(DatabaseHelper.COLUMN_ALTURA, altura);

        if (usuarioExiste()) {
            int filasAfectadas = db.update(DatabaseHelper.TABLE_PERSONA, values, "id = (SELECT id FROM persona LIMIT 1)", null);
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            long resultado = db.insert(DatabaseHelper.TABLE_PERSONA, null, values);
            if (resultado == -1) {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
            }
        }

        db.close();
        tvIMC.setText("IMC: " + String.format("%.2f", imc));
    }

    private boolean usuarioExiste() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PERSONA, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }
}