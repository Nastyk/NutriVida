package com.nutrivda.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nutrivda.app.database.DatabaseHelper;

public class ActividadIMC extends AppCompatActivity {

    private EditText etNombre, etPeso, etAltura;
    private Button btnGuardar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_imc);

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Vincular elementos del layout
        etNombre = findViewById(R.id.etNombre);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Evento del botón Guardar
        btnGuardar.setOnClickListener(v -> guardarDatos());

        // Comprobar si ya hay un usuario registrado
        verificarDatosUsuario();
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

        // **Validación de valores lógicos**
        if (peso <= 0 || peso > 300) {
            Toast.makeText(this, "Por favor ingresa un peso válido (1 - 300 kg)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (altura <= 0.5 || altura > 2.5) {
            Toast.makeText(this, "Por favor ingresa una altura válida (0.5 - 2.5 m)", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOMBRE, nombre);
        values.put(DatabaseHelper.COLUMN_PESO, peso);
        values.put(DatabaseHelper.COLUMN_ALTURA, altura);

        // Verificar si ya existe un usuario
        if (usuarioExiste()) {
            int filasAfectadas = db.update(DatabaseHelper.TABLE_PERSONA, values, "id = (SELECT id FROM persona LIMIT 1)", null);
            if (filasAfectadas > 0) {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
            }
        } else {
            long resultado = db.insert(DatabaseHelper.TABLE_PERSONA, null, values);
            if (resultado == -1) {
                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            }
        }

        db.close();
        finish(); // Cerrar la actividad y volver a MainActivity
    }

    private boolean usuarioExiste() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PERSONA, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    private void verificarDatosUsuario() {
        if (usuarioExiste()) {
            irAMainActivity(); // Si hay datos, ir a MainActivity
        }
    }

    private void irAMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Cerrar esta actividad para que no vuelva a aparecer
    }
}