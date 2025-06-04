package com.nutrivda.app.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrivda.app.ActividadPerfil;
import com.nutrivda.app.BaseActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.pojo.PlanNutricional;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvIMC, tvClasificacionIMC, tvCalorias, tvMacros, tvNivelActividad, tvTiempo, tvAnalisis;
    private Button btnComenzarPlan;
    private PlanNutricional planNutricional;
    private SupabaseApi supabaseApi;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        tvIMC = findViewById(R.id.tvIMC);
        tvClasificacionIMC = findViewById(R.id.tvClasificacionIMC);
        tvCalorias = findViewById(R.id.tvCalorias);
        tvMacros = findViewById(R.id.tvMacros);
        tvNivelActividad = findViewById(R.id.tvNivelActividad);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvAnalisis = findViewById(R.id.tvAnalisis);
        btnComenzarPlan = findViewById(R.id.btnComenzarPlan);

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);
        // Obtengo los datos que llegan con el Intent
        Intent recibido = getIntent();
        planNutricional = (PlanNutricional) getIntent().getSerializableExtra("planNutricional");
        userId = getUserId();

        if (planNutricional != null) {
            tvIMC.setText("IMC: " + planNutricional.getImc());
            tvClasificacionIMC.setText("Clasificación: " + planNutricional.getClasificacion_imc());
            tvCalorias.setText("Calorías recomendadas: " + planNutricional.getCalorias_recomendadas());

            PlanNutricional.MacroRecomendado macro = planNutricional.getMacro_recomendado();
            if (macro != null) {
                String macrosText = "Proteínas: " + macro.getProteinas() + "g\n" +
                        "Grasas: " + macro.getGrasas() + "g\n" +
                        "Carbohidratos: " + macro.getCarbohidratos() + "g";
                tvMacros.setText("Macros (g/día):\n" + macrosText);
            }

            tvNivelActividad.setText("Nivel de actividad recomendado: " + planNutricional.getNivel_actividad_recomendado());
            tvTiempo.setText("Tiempo estimado: " + planNutricional.getTiempo_estimado_para_lograr_objetivo());
            tvAnalisis.setText(planNutricional.getAnalisis_personalizado());
        }

        btnComenzarPlan.setOnClickListener(v -> {
            actualizarDatosUsuario(planNutricional);
            // Lanzo el test emocional como inicio de la cadena
            Intent comenzar = new Intent(ResultadoActivity.this, BaseActivity.class);
            startActivity(comenzar);
            finish();
        });
    }

    private void actualizarDatosUsuario(PlanNutricional planNutricional) {

        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("calorias_objetivo", planNutricional.getCalorias_recomendadas());
        datosUsuario.put("cuestionario_hecho", true);
        supabaseApi.actualizarDatosUsuario("eq." + userId, datosUsuario).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                if (response.code() == 204) {
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
            }
        });
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }
}
