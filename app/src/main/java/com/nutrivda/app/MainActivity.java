package com.nutrivda.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nutrivda.app.database.DatabaseHelper;
import com.nutrivda.app.utils.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvNombreUsuario, tvObjetivoKcal, tvIMC;
    private EditText etPesoDiario;
    private Button btnGuardarPeso, btnIrComida, btnIrPerfil;
    private MaterialCalendarView materialCalendarView;
    private DatabaseHelper dbHelper;
    private double imc = 0;
    private int objetivoKcal = 2200; // Valor por defecto
    private String fechaSeleccionadaCalendario;

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
        materialCalendarView = findViewById(R.id.calendarView);
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
            if (fechaSeleccionadaCalendario == null || fechaSeleccionadaCalendario.isEmpty()) {
                // Si el usuario no seleccionó una fecha, usar la fecha actual
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaSeleccionadaCalendario = sdf.format(Calendar.getInstance().getTime());
            }

            Intent intent = new Intent(MainActivity.this, ActividadComida.class);
            intent.putExtra("fechaSeleccionada", fechaSeleccionadaCalendario);
            startActivity(intent);
        });

        // Ir a la actividad de perfil
        btnIrPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActividadPerfil.class);
            startActivity(intent);
        });

        // Manejo de calendario: Detectar cambio de día
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            String fechaSeleccionada = date.getYear() + "-" +
                    String.format("%02d", (date.getMonth() + 1)) + "-" +
                    String.format("%02d", date.getDay());
            int year = date.getYear();
            int month = date.getMonth() + 1;
            int day = date.getDay();

            fechaSeleccionadaCalendario = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
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
        Cursor cursor = null;
        HashSet<CalendarDay> fechasCompletadas = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            cursor = db.rawQuery("SELECT fecha FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS + " WHERE completado = 1", null);
            while (cursor.moveToNext()) {
                try {
                    Date date = sdf.parse(cursor.getString(0));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    fechasCompletadas.add(CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        runOnUiThread(() -> {
            materialCalendarView.removeDecorators();
            materialCalendarView.addDecorator(new EventDecorator(fechasCompletadas));
        });
    }

    private void  verificarDiaCompletado(String fechaSeleccionada) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS + " WHERE fecha = ?", new String[]{fechaSeleccionada});

            if (cursor.getCount() > 0) {
                mostrarComidasDelDia(fechaSeleccionada);
            } else {
                Toast.makeText(this, "Este día no ha sido completado", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    private void mostrarComidasDelDia(String fechaSeleccionada) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        String desayuno = "No registrado";
        String comida = "No registrado";
        String cena = "No registrado";
        boolean diaIncompleto = false;
        String checkDesayuno = "❌";
        String checkComida = "❌";
        String checkCena = "❌";

        try {
            // Obtener desayuno, comida y cena desde `dias_completados`
            cursor = db.rawQuery(
                    "SELECT desayuno, comida, cena, swdesayuno, swcomida, swcena FROM " + DatabaseHelper.TABLE_DIAS_COMPLETADOS +
                            " WHERE fecha = ?", new String[]{fechaSeleccionada});

            if (cursor.moveToFirst()) {
                desayuno = cursor.getString(0) != null ? cursor.getString(0) : "No registrado";
                comida = cursor.getString(1) != null ? cursor.getString(1) : "No registrado";
                cena = cursor.getString(2) != null ? cursor.getString(2) : "No registrado";
                checkDesayuno = cursor.getInt(3) == 1 ? "✅" : "❌";
                checkComida = cursor.getInt(4) == 1 ? "✅" : "❌";
                checkCena = cursor.getInt(5) == 1 ? "✅" : "❌";

                if (desayuno.equals("No registrado") || comida.equals("No registrado") || cena.equals("No registrado")) {
                    diaIncompleto = true;
                }
            } else {
                diaIncompleto = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        //TODO: PASAR A UN NUEVO METODO ESTO MAS ADELANTE
        View dialogView = getLayoutInflater().inflate(R.layout.pop_up_resumen_dia, null);

        TextView tvFecha = dialogView.findViewById(R.id.tvFecha);
        TextView tvDesayuno = dialogView.findViewById(R.id.tvDesayuno);
        TextView tvComida = dialogView.findViewById(R.id.tvComida);
        TextView tvCena = dialogView.findViewById(R.id.tvCena);
        TextView tvEstado = dialogView.findViewById(R.id.tvEstado);
        TextView tvCheckDesayuno = dialogView.findViewById(R.id.tvCheckDesayuno);
        TextView tvCheckComida = dialogView.findViewById(R.id.tvCheckComida);
        TextView tvCheckCena = dialogView.findViewById(R.id.tvCheckCena);
        Button btnEditar = dialogView.findViewById(R.id.btnEditar);
        Button btnCerrar = dialogView.findViewById(R.id.btnCerrar);

        tvFecha.setText("📅 Día: " + fechaSeleccionada);
        tvDesayuno.setText("🍽️ Desayuno: " + desayuno);
        tvComida.setText("🍛 Comida: " + comida);
        tvCena.setText("🍲 Cena: " + cena);

        tvCheckDesayuno.setText(checkDesayuno);
        tvCheckComida.setText(checkComida);
        tvCheckCena.setText(checkCena);

        if (diaIncompleto) {
            tvEstado.setVisibility(View.VISIBLE); // Mostrar "Día Incompleto"
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView) // Usar el layout personalizado
                .create();

        // Botón de Editar
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActividadComida.class);
            intent.putExtra("fechaSeleccionada", fechaSeleccionada);
            startActivity(intent);
            dialog.dismiss();
        });

        // Botón de Cerrar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
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