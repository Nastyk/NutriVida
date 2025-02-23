package com.nutrivda.app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton btnEditarPerfil, btnIrMain;
    private View btnGuardar;
    private DatabaseHelper dbHelper;
    private int diasCumplidos = 0;

    private static final int USUARIO_ID = 1;

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
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnIrMain = findViewById(R.id.btnIrAmain);

        // Cargar datos actuales del usuario
        cargarDatosUsuario();

        btnEditarPerfil.setOnClickListener(v -> activarEdicion(true));
        btnGuardar.setOnClickListener(v -> guardarDatos());
        btnIrMain.setOnClickListener(v -> {
            startActivity(new Intent(ActividadPerfil.this, MainActivity.class));
            finish();
        });

        btnGuardar.setVisibility(View.GONE);
    }

    private void activarEdicion(boolean activar) {
        etNombre.setEnabled(activar);
        etNombre.setFocusable(activar);
        etNombre.setFocusableInTouchMode(activar);

        etPeso.setEnabled(activar);
        etPeso.setFocusable(activar);
        etPeso.setFocusableInTouchMode(activar);

        etAltura.setEnabled(activar);
        etAltura.setFocusable(activar);
        etAltura.setFocusableInTouchMode(activar);

        if (activar) {
            btnGuardar.setVisibility(View.VISIBLE);
            btnEditarPerfil.setVisibility(View.GONE);
        } else {
            btnGuardar.setVisibility(View.GONE);
            btnEditarPerfil.setVisibility(View.VISIBLE);
        }
    }


    private void cargarDatosUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PERSONA + " WHERE id = ?", new String[]{String.valueOf(USUARIO_ID)});

        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(0));
            etPeso.setText(String.valueOf(cursor.getDouble(1)));
            etAltura.setText(String.valueOf(cursor.getDouble(2)));

            calcularIMC(cursor.getDouble(1), cursor.getDouble(2));
        } else {
            tvIMC.setText("IMC: No disponible");
        }

        cursor.close();
        db.close();

        calcularDiasCumplidos();
    }

    private void calcularIMC(double peso, double altura) {
        if (peso > 0 && altura > 0) {
            double imc = peso / (altura * altura);
            tvIMC.setText("IMC: " + String.format("%.2f", imc));
        } else {
            tvIMC.setText("IMC: No disponible");
        }
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOMBRE, nombre);
        values.put(DatabaseHelper.COLUMN_PESO, peso);
        values.put(DatabaseHelper.COLUMN_ALTURA, altura);

        boolean existe = usuarioExistePorID(db, USUARIO_ID);

        long resultado;
        if (existe) {
            resultado = db.update(DatabaseHelper.TABLE_PERSONA, values, "id = ?", new String[]{String.valueOf(USUARIO_ID)});
        } else {
            values.put(DatabaseHelper.COLUMN_ID, USUARIO_ID);
            resultado = db.insert(DatabaseHelper.TABLE_PERSONA, null, values);
        }

        db.close();

        if (resultado < 0) {
            Toast.makeText(this, "❌ Error al guardar", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "✅ Datos guardados", Toast.LENGTH_SHORT).show();
        }

        calcularIMC(peso, altura);
        activarEdicion(false);
    }

    private boolean usuarioExistePorID(SQLiteDatabase db, int userId) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + DatabaseHelper.TABLE_PERSONA + " WHERE id = ?", new String[]{String.valueOf(userId)});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
}