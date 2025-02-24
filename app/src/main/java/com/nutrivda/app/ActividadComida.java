package com.nutrivda.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Spinner;
import android.app.DatePickerDialog;
import android.widget.Toast;

import com.nutrivda.app.callback.CaloriasCallback;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.database.DatabaseHelper;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.StringUtil;

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
    private String fechaDeComida;
    private double totalKcal = 0;
    private int userId = 0, caloriasDesayuno = 0, caloriasComida = 0, caloriasCena = 0;
    private ImageButton btnBorrarDesayuno, btnBorrarComida, btnBorrarCena, btnIrAtras;
    private boolean isEditar = false;
    private SupabaseApi supabaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_comida);

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

        btnIrAtras = findViewById(R.id.btnIrAtras);
        btnBorrarDesayuno = findViewById(R.id.btnBorrarDesayuno);
        btnBorrarComida = findViewById(R.id.btnBorrarComida);
        btnBorrarCena = findViewById(R.id.btnBorrarCena);

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Obtener la fecha enviada desde MainActivity
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
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
            cargarDatosDelDia();
            btnGuardarComida.setText("Guardar cambios");
        }

        btnAnadirDesayuno.setOnClickListener(v -> abrirAniadirComida("Desayuno"));
        btnAnadirComida.setOnClickListener(v -> abrirAniadirComida("Comida"));
        btnAnadirCena.setOnClickListener(v -> abrirAniadirComida("Cena"));

        configurarBotonBorrar(btnBorrarDesayuno, tvDesayunoSeleccionado, tvCaloriasDesayuno, btnAnadirDesayuno, cbDesayuno, "Desayuno");
        configurarBotonBorrar(btnBorrarComida, tvComidaSeleccionada, tvCaloriasComida, btnAnadirComida, cbComida, "Comida");
        configurarBotonBorrar(btnBorrarCena, tvCenaSeleccionada, tvCaloriasCena, btnAnadirCena, cbCena, "Cena");

        // Guardar selección de comidas
        btnGuardarComida.setOnClickListener(v -> guardarComidas());

        // Ir atrás
        btnIrAtras.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ActividadComida.this, MainActivity.class);
            startActivity(intentVolver);
        });
    }

    /**
     *
     * @param botonBorrar
     * @param textViewComida
     * @param textViewCalorias
     * @param botonAnadir
     * @param checkBox
     * @param tipoComida
     *
     * Configuracion del boton borrar una vez añadida la comida
     */
    private void configurarBotonBorrar(ImageButton botonBorrar, TextView textViewComida, TextView textViewCalorias, Button botonAnadir, CheckBox checkBox, String tipoComida) {
        botonBorrar.setOnClickListener(v -> {
            // Borrar la comida seleccionada
            textViewComida.setText("Ninguno");
            textViewCalorias.setText("Calorías: 0");
            checkBox.setChecked(false);

            switch (tipoComida) {
                case "Desayuno":
                    caloriasDesayuno = 0;
                    break;
                case "Comida":
                    caloriasComida = 0;
                    break;
                case "Cena":
                    caloriasCena = 0;
                    break;
                default:
                    Log.e("NutriVida","ERROR en configurarBotonBorrar -> tipoComida en switch erroneo");
            }

            // Ocultar el botón de borrar
            botonBorrar.setVisibility(View.GONE);
            botonAnadir.setVisibility(View.VISIBLE);

            // Actualizar total de calorías
            actualizarTotalKcal();
        });
    }

    /**
     *
     * @param tipoComida
     *
     * Metodo que inicia la actividad AniadirComida donde se pasa el dato REQUEST_CODE
     * que permite identificar la actividad origen en el onActivityResult
     */
    private void abrirAniadirComida(String tipoComida) {
        Intent intent = new Intent(this, AniadirComidaActivity.class);
        intent.putExtra("tipo_comida", tipoComida);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     *
     * @param requestCode El código de solicitud entero proporcionado originalmente a
     *                    startActivityForResult(), lo que permite identificar de dónde
     *                    proviene este resultado.
     * @param resultCode El código de resultado entero devuelto por la actividad secundaria
     *                   a través de su setResult().
     * @param data Un Intent, que puede devolver datos de resultado al llamador
     *             (se pueden adjuntar varios datos a los "extras" del Intent).
     *
     * Este metodo se ejecuta tras la finalizacion de la actividad AniadorComida
     */
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
                        actualizarVistaComida(tvDesayunoSeleccionado, btnBorrarDesayuno, tvCaloriasDesayuno, btnAnadirDesayuno, comidaSeleccionada, caloriasComidaSeleccionada, "Desayuno");
                        break;
                    case "comida":
                        actualizarVistaComida(tvComidaSeleccionada, btnBorrarComida, tvCaloriasComida, btnAnadirComida, comidaSeleccionada, caloriasComidaSeleccionada, "Comida");
                        break;
                    case "cena":
                        actualizarVistaComida(tvCenaSeleccionada, btnBorrarCena, tvCaloriasCena, btnAnadirCena, comidaSeleccionada, caloriasComidaSeleccionada, "Cena");
                        break;
                }
                actualizarTotalKcal();
            }
        }
    }

    /**
     *
     * @param textViewComida
     * @param botonBorrar
     * @param textViewCalorias
     * @param botonAnadir
     * @param comida
     * @param calorias
     * @param tipoComida
     *
     * actualiza la vista de las comidas conforme a los argumentos pasados en el alta
     */
    private void actualizarVistaComida(TextView textViewComida, ImageButton botonBorrar, TextView textViewCalorias, Button botonAnadir, String comida, int calorias, String tipoComida) {
        textViewComida.setText(comida);
        textViewCalorias.setText("Calorías: " + calorias);

        switch (tipoComida) {
            case "Desayuno":
                caloriasDesayuno = calorias;
                break;
            case "Comida":
                caloriasComida = calorias;
                break;
            case "Cena":
                caloriasCena = calorias;
                break;
            default:
                Log.e("NutriVida","ERROR en actualizarVistaComida -> tipoComida en switch erroneo");
        }

        // Mostrar botón de borrar y ocultar el de añadir
        botonBorrar.setVisibility(View.VISIBLE);
        botonAnadir.setVisibility(View.GONE);
    }

    /**
     *
     * @param textViewComida
     * @param botonBorrar
     * @param textViewCalorias
     * @param botonAnadir
     * @param comida
     * @param calorias
     * @param tipoComida
     *
     * Actualiza la vista de las comidas conforme los argumentos pasados en el modo de edicion
     */
    private void actualizarVistaComidaEnEdicion(TextView textViewComida, ImageButton botonBorrar, TextView textViewCalorias, Button botonAnadir, String comida, int calorias, String tipoComida) {
        textViewComida.setText(comida);
        textViewCalorias.setText("Calorías: " + calorias);

        switch (tipoComida) {
            case "Desayuno":
                caloriasDesayuno = calorias;
                break;
            case "Comida":
                caloriasComida = calorias;
                break;
            case "Cena":
                caloriasCena = calorias;
                break;
            default:
                Log.e("NutriVida","ERROR en actualizarVistaComida -> tipoComida en switch erroneo");
        }

        if(comida.equals("Ninguno")) {
            // Ocultar el botón de borrar
            Log.i("NutriVida", "INFO: ENTRA EN IF********************** CON COMIDA VALOR  :" + comida);
            botonBorrar.setVisibility(View.GONE);
            botonAnadir.setVisibility(View.VISIBLE);
        } else {
            // Mostrar botón de borrar y ocultar el de añadir
            botonBorrar.setVisibility(View.VISIBLE);
            botonAnadir.setVisibility(View.GONE);
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

        String desayunoSeleccionado = StringUtil.isCadenaVacia(tvDesayunoSeleccionado.getText().toString()) ? null : tvDesayunoSeleccionado.getText().toString();
        String comidaSeleccionada = StringUtil.isCadenaVacia(tvComidaSeleccionada.getText().toString()) ? null : tvComidaSeleccionada.getText().toString();
        String cenaSeleccionada = StringUtil.isCadenaVacia(tvCenaSeleccionada.getText().toString()) ? null : tvCenaSeleccionada.getText().toString();

        caloriasDesayuno = StringUtil.isCadenaVacia(tvDesayunoSeleccionado.getText().toString()) ? 0 : caloriasDesayuno;
        caloriasComida = StringUtil.isCadenaVacia(tvComidaSeleccionada.getText().toString()) ? 0 : caloriasComida;
        caloriasCena = StringUtil.isCadenaVacia(tvCenaSeleccionada.getText().toString()) ? 0 : caloriasCena;

        boolean diaIncompleto = !desayunoHecho || !comidaHecha || !cenaHecha;

        if (diaIncompleto) {
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Día Incompleto")
                    .setMessage("Has registrado comidas, pero el día no será marcado como completo en el calendario. ¿Quieres continuar?")
                    .setPositiveButton("Guardar", (dialog, which) -> guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, desayunoHecho, comidaHecha, cenaHecha, false, caloriasDesayuno, caloriasComida, caloriasCena))
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, desayunoHecho, comidaHecha, cenaHecha, true, caloriasDesayuno, caloriasComida, caloriasCena);
    }

    private void guardarDatosComida(String desayuno, String comida, String cena, boolean swDesayuno, boolean swComida, boolean swCena, boolean marcarComoCompleto, int caloriasDesayuno, int caloriasComida, int caloriasCena) {
        // Crear objeto JSON con los datos correctos para la tabla dias_completados
        Map<String, Object> comidaData = new HashMap<>();
        comidaData.put("completado", marcarComoCompleto);
        comidaData.put("desayuno", comprobarCampo(desayuno));
        comidaData.put("sw_desayuno", swDesayuno);
        comidaData.put("comida", comprobarCampo(comida));
        comidaData.put("sw_comida", swComida);
        comidaData.put("cena", comprobarCampo(cena));
        comidaData.put("sw_cena", swCena);
        comidaData.put("caloria_desayuno", caloriasDesayuno);
        comidaData.put("caloria_comida", caloriasComida);
        comidaData.put("caloria_cena", caloriasCena);

        if (isEditar) {
            // Si está en modo edición, hacemos un PATCH en lugar de POST
            supabaseApi.actualizarComida("eq." + userId, "eq." + fechaDeComida, comidaData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ActividadComida.this, "✅ Comida actualizada correctamente en Supabase", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ActividadComida.this, "❌ Error al actualizar en Supabase: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ActividadComida.this, "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("NutriVida","ERROR en actualizarComida -> " + t.getMessage());
                }
            });
        } else {
            comidaData.put("id_usuario_fk", userId);
            comidaData.put("fecha", fechaDeComida);
            supabaseApi.insertarComida(comidaData).enqueue(new Callback<Void>() {
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
                    Log.e("NutriVida","ERROR en actualizarComida -> " + t.getMessage());
                }
            });
        }

    }

    private void cargarDatosDelDia() {
        supabaseApi.obtenerDiaCompletado("eq." + fechaDeComida, "eq." + userId).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Tomar el primer resultado, ya que la fecha debería ser única
                    DiaCompletado dia = response.body().get(0);

                    String desayunoSeleciconado = dia.getDesayuno() != null ? dia.getDesayuno() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvDesayunoSeleccionado, btnBorrarDesayuno, tvCaloriasDesayuno, btnAnadirDesayuno, desayunoSeleciconado, dia.getCaloria_desayuno(), "Desayuno");
                    cbDesayuno.setChecked(dia.isSwDesayuno());

                    String comidaSeleccionada = dia.getComida() != null ? dia.getComida() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvComidaSeleccionada, btnBorrarComida, tvCaloriasComida, btnAnadirComida, comidaSeleccionada, dia.getCaloria_comida(), "Comida");
                    cbComida.setChecked(dia.isSwComida());

                    String cenaSeleccionada = dia.getCena() != null ? dia.getCena() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvCenaSeleccionada, btnBorrarCena, tvCaloriasCena, btnAnadirCena, cenaSeleccionada, dia.getCaloria_cena(), "Cena");
                    cbCena.setChecked(dia.isSwCena());

                    actualizarTotalKcal();
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(ActividadComida.this, "Error al conectar con Supabase: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String comprobarCampo(String campo) {
        return campo == null ? "Sin datos" : campo;
    }

}