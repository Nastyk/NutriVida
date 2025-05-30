package com.nutrivda.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadPerfil extends AppCompatActivity {

    private EditText etNombre, etApellido1, etApellido2, etPeso, etAltura, etEdad, etActividad;
    Spinner spinnerActividad;
    private TextView tvIMC;
    private ImageButton btnEditarPerfil, btnIrMain;
    private Button btnGuardar;
    private int userId;
    private SupabaseApi supabaseApi;
    private LineChart chartPeso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_perfil);

        // Obtener el userId del Intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Vincular elementos del layout
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        etEdad = findViewById(R.id.etEdad);
//        etActividad = findViewById(R.id.etActividad);
        tvIMC = findViewById(R.id.tvIMCPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnIrMain = findViewById(R.id.btnIrAmain);
        //chartPeso = findViewById(R.id.chartPeso);

        /*spinnerActividad = findViewById(R.id.spinnerActividad);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.actividad_fisica_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActividad.setAdapter(adapter);*/


        btnEditarPerfil.setOnClickListener(v -> activarEdicion(true));
        btnGuardar.setOnClickListener(v -> guardarDatos());
        btnIrMain.setOnClickListener(v -> {
            startActivity(new Intent(ActividadPerfil.this, MainActivity.class));
            finish();
        });

        btnGuardar.setVisibility(View.GONE);

        // Cargar datos del usuario
        cargarDatosUsuario();
       // configurarGraficoPeso();
    }

    private void activarEdicion(boolean activar) {
        etNombre.setEnabled(activar);
        etApellido1.setEnabled(activar);
        etApellido2.setEnabled(activar);
        etPeso.setEnabled(activar);
        etAltura.setEnabled(activar);
        etEdad.setEnabled(activar);
        //etActividad.setEnabled(activar);
        //spinnerActividad.setVisibility(View.VISIBLE);

        if (activar) {
            btnGuardar.setVisibility(View.VISIBLE);
            btnEditarPerfil.setVisibility(View.GONE);
        } else {
            btnGuardar.setVisibility(View.GONE);
            btnEditarPerfil.setVisibility(View.VISIBLE);
        }
    }

    /*private void configurarGraficoPeso() {
        List<Entry> entradas = new ArrayList<>();
        entradas.add(new Entry(1, 85)); // Día 1 - Peso 85kg
        entradas.add(new Entry(2, 83)); // Día 2 - Peso 83kg
        entradas.add(new Entry(3, 82));
        entradas.add(new Entry(4, 81));
        entradas.add(new Entry(5, 80));

        LineDataSet dataSet = new LineDataSet(entradas, "Evolución del Peso");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);

        LineData data = new LineData(dataSet);
        chartPeso.setData(data);

        XAxis xAxis = chartPeso.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chartPeso.invalidate(); // Refrescar la gráfica
    }*/

    private void cargarDatosUsuario() {
        // Obtener datos desde la tabla usuario
        supabaseApi.obtenerUsuario("eq." + userId).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Usuario usuario = response.body().get(0);
                } else {
                    Toast.makeText(ActividadPerfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(ActividadPerfil.this, "Error al obtener usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener datos desde la tabla datos_usuario
        supabaseApi.obtenerDatosUsuario("eq." + userId).enqueue(new Callback<List<DatosUsuario>>() {
            @Override
            public void onResponse(Call<List<DatosUsuario>> call, Response<List<DatosUsuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DatosUsuario datos = response.body().get(0);
                    etNombre.setText(datos.getNombre());
                    etApellido1.setText(datos.getApellido1());
                    etApellido2.setText(datos.getApellido2());
                    etPeso.setText(String.valueOf(datos.getPeso()));
                    etAltura.setText(String.valueOf(datos.getAltura()));
                    etEdad.setText(String.valueOf(datos.getEdad()));
//                    etActividad.setText(datos.getActividad_fisica());
                    calcularIMC(datos.getPeso(), datos.getAltura());
                } else {
                    Toast.makeText(ActividadPerfil.this, "No se encontraron datos adicionales del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DatosUsuario>> call, Throwable t) {
                Toast.makeText(ActividadPerfil.this, "Error al obtener datos del usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularIMC(double peso, double altura) {
        if (peso > 0 && altura > 0) {
            double imc = peso / ((altura / 100) * (altura / 100));
            tvIMC.setText("IMC: " + String.format("%.2f", imc));
        } else {
            tvIMC.setText("IMC: No disponible");
        }
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String apellido1 = etApellido1.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String actividad = "ACTIVO";
        //TODO: esto hay que calcularlo con un algoritmo
        double caloriasObjetivo = 2000;

        if (nombre.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty() || edadStr.isEmpty() || actividad.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        double altura = Double.parseDouble(alturaStr);
        int edad = Integer.parseInt(edadStr);

        // Actualizar tabla datos_usuario
        DatosUsuario datosUsuario = new DatosUsuario(userId,peso, altura, edad, nombre, apellido1, apellido2, actividad, caloriasObjetivo);
        supabaseApi.actualizarDatosUsuario("eq." + userId, datosUsuario).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                if (response.code() == 204) {
                    Toast.makeText(ActividadPerfil.this, "✅ Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    calcularIMC(peso, altura);
                    activarEdicion(false);
                } else {
                    Toast.makeText(ActividadPerfil.this, "⚠️ Error al actualizar datos: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                Toast.makeText(ActividadPerfil.this, "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}