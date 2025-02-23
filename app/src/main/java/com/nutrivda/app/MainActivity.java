package com.nutrivda.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.nutrivda.app.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvNombreUsuario, tvObjetivoKcal, tvIMC;
    private EditText etPesoDiario;
    private Button btnGuardarPeso, btnIrComida, btnIrPerfil;
    private CalendarView calendarView;
    private DatabaseHelper dbHelper;
    private double imc = 0;
    private int objetivoKcal = 2200; // Valor por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar base de datosf
        dbHelper = new DatabaseHelper(this);

        // Vincular elementos del layout con el código
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvObjetivoKcal = findViewById(R.id.tvObjetivoKcal);
        etPesoDiario = findViewById(R.id.etPesoDiario);
        btnGuardarPeso = findViewById(R.id.btnGuardarPeso);
        btnIrComida = findViewById(R.id.btnIrComida);
        btnIrPerfil = findViewById(R.id.btnIrPerfil);
        tvIMC = findViewById(R.id.tvIMC);
        calendarView = findViewById(R.id.calendarView);
        Button btnLogOut = findViewById(R.id.btnLogout);

        btnLogOut.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Marcar los días completados en el calendario
        marcarDiasEnCalendario();

        // Guardar peso diario
        btnGuardarPeso.setOnClickListener(v -> guardarPesoDiario());

        // Configurar alarma de reinicio de peso diario
        configurarAlarmaDiaria();

        // Ir a la actividad de comidas
        btnIrComida.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActividadComida.class);
            startActivity(intent);
        });

        // Ir a la actividad de perfil
        btnIrPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActividadPerfil.class);
            startActivity(intent);
        });

        // Manejo de calendario: Detectar cambio de día
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
            verificarDiaCompletado(fechaSeleccionada);
        });
    }

    // Recargar datos cada que la actividad se reinicie
    protected void onResume() {
        super.onResume();
        cargarDatosUsuario(); // Recarga los datos del usuario
        marcarDiasEnCalendario(); // Actualiza el calendario
    }

    private void cargarDatosUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PERSONA, null);

        if (cursor.moveToFirst()) {
            int indexNombre = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE);
            int indexPeso = cursor.getColumnIndex(DatabaseHelper.COLUMN_PESO);
            int indexAltura = cursor.getColumnIndex(DatabaseHelper.COLUMN_ALTURA);

            String nombre = "";
            if (indexNombre > 0 || indexNombre == 0) {
                nombre = cursor.getString(indexNombre);
            }
            double peso = 0;
            if(indexPeso > 0 || indexPeso == 0) {
                peso = cursor.getDouble(indexPeso);
            }
            double altura = 0;
            if(indexAltura > 0 || indexAltura == 0) {
                altura = cursor.getDouble(indexAltura);
            }

            if (peso > 0 && altura > 0) {
                imc = peso / (altura * altura);
                calcularObjetivoKcal();
                tvIMC.setText("IMC: " + String.format("%.2f", imc));
            } else {
                tvIMC.setText("IMC: No disponible");
            }

            tvNombreUsuario.setText("¡Bienvenido, " + nombre + "!");
            tvObjetivoKcal.setText("Objetivo: " + objetivoKcal + " kcal");
        } else {
            tvNombreUsuario.setText("¡Bienvenido!");
            tvIMC.setText("IMC: No disponible");
            tvObjetivoKcal.setText("Objetivo: No definido");
        }

        cursor.close();
        db.close();
    }

    private void calcularObjetivoKcal() {
        if (imc < 18.5) {
            objetivoKcal = 2600; // Subir de peso
        } else if (imc < 25) {
            objetivoKcal = 2200; // Mantener
        } else {
            objetivoKcal = 1800; // Bajar de peso
        }
    }

    private void guardarPesoDiario() {
        String pesoStr = etPesoDiario.getText().toString().trim();
        if (pesoStr.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PESO, Double.parseDouble(pesoStr));

        int filasAfectadas = db.update(DatabaseHelper.TABLE_PERSONA, values, "id = (SELECT id FROM persona LIMIT 1)", null);

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Peso actualizado ✅", Toast.LENGTH_SHORT).show();
            cargarDatosUsuario();
        } else {
            Toast.makeText(this, "Error: No se encontró el usuario", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void marcarDiasEnCalendario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT fecha FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS, null);
        List<Long> fechasCompletadas = new ArrayList<>();

        while (cursor.moveToNext()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(cursor.getString(0));
                if (date != null) {
                    fechasCompletadas.add(date.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();

        runOnUiThread(() -> {
            for (long fecha : fechasCompletadas) {
                calendarView.setDate(fecha, true, true);
            }
        });
    }

    private void verificarDiaCompletado(String fecha) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS + " WHERE fecha = ?", new String[]{fecha});

        if (cursor.getCount() > 0) {
            Toast.makeText(this, "Este día está marcado como completado 🎉", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Este día no ha sido completado", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private void configurarAlarmaDiaria() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ResetPesoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}