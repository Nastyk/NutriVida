package com.nutrivda.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleComidaActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvCalorias, tvCarbs, tvGrasas, tvProteinas, tvNombreComida, tvNRaciones, tvTRacion, tvNRacionesUnidad, tvUnidadMedida;
    private EditText etNombreComida, etNRaciones, etTRacion, etTCalorias, etTCarbohidratos, etTGrasas, etTProteinas;
    private LinearLayout llCarbohidratosEdicion, llGrasasEdicion, llProteinasEdicion;
    private int idAlimento, userId, calorias, carbohidratos, grasas, proteinas;
    private SupabaseApi supabaseApi;
    private ImageButton btnAtras, btnEditar, btnGuardar;
    private boolean esEdicion, valido, esAdicion;
    private Comida comidaEnMemoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_comida);

        pieChart = findViewById(R.id.pieChart);
        tvCalorias = findViewById(R.id.tvCalorias);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvGrasas = findViewById(R.id.tvGrasas);
        tvProteinas = findViewById(R.id.tvProteinas);
        tvNombreComida = findViewById(R.id.tvNombreComida);
        tvNRaciones = findViewById(R.id.tvNRaciones);
        tvTRacion = findViewById(R.id.tvTRacion);
        btnAtras = findViewById(R.id.btnBack);
        btnEditar = findViewById(R.id.btnEditar);
        btnGuardar = findViewById(R.id.btnGuardar);

        etNombreComida= findViewById(R.id.etNombreComida);
        etNRaciones = findViewById(R.id.etNRaciones);
        etTRacion = findViewById(R.id.etTRacion);
        etTCalorias = findViewById(R.id.etTCalorias);
        etTCarbohidratos = findViewById(R.id.etTCarbohidratos);
        etTGrasas = findViewById(R.id.etTGrasas);
        etTProteinas =  findViewById(R.id.etTProteinas);

        tvNRacionesUnidad = findViewById(R.id.tvNRacionesUnidad);
        tvUnidadMedida = findViewById(R.id.tvUnidadMedida);

        llCarbohidratosEdicion = findViewById(R.id.llCarbohidratosEdicion);
        llGrasasEdicion = findViewById(R.id.llGrasasEdicion);
        llProteinasEdicion = findViewById(R.id.llProteinasEdicion);


        idAlimento = getIntent().getIntExtra("idAlimento", -1);
        userId = getIntent().getIntExtra("user_id",0) > 0 ? getIntent().getIntExtra("user_id", 0) : getUserId();
        esEdicion = getIntent().getBooleanExtra("esEdicion", false);
        esAdicion = getIntent().getBooleanExtra("esAdicion", false);
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        recuperarinforAlimento(idAlimento);

        if (esEdicion) {
            tvNombreComida.setVisibility(View.GONE);
            etNombreComida.setVisibility(View.VISIBLE);

            tvNRaciones.setVisibility(View.GONE);
            etNRaciones.setVisibility(View.VISIBLE);
            tvNRacionesUnidad.setVisibility(View.VISIBLE);

            tvTRacion.setVisibility(View.GONE);
            etTRacion.setVisibility(View.VISIBLE);

            llCarbohidratosEdicion.setVisibility(View.VISIBLE);
            llGrasasEdicion.setVisibility(View.VISIBLE);
            llProteinasEdicion.setVisibility(View.VISIBLE);

            btnEditar.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.GONE);
        } else if (esAdicion) {
            tvNombreComida.setVisibility(View.GONE);
            etNombreComida.setVisibility(View.VISIBLE);

            tvNRaciones.setVisibility(View.GONE);
            etNRaciones.setVisibility(View.VISIBLE);
            tvNRacionesUnidad.setVisibility(View.VISIBLE);

            tvTRacion.setVisibility(View.GONE);
            etTRacion.setVisibility(View.VISIBLE);

            llCarbohidratosEdicion.setVisibility(View.VISIBLE);
            llGrasasEdicion.setVisibility(View.VISIBLE);
            llProteinasEdicion.setVisibility(View.VISIBLE);

            btnEditar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.VISIBLE);
        } else {
            tvUnidadMedida.setVisibility(View.GONE);

            llCarbohidratosEdicion.setVisibility(View.GONE);
            llGrasasEdicion.setVisibility(View.GONE);
            llProteinasEdicion.setVisibility(View.GONE);
            btnEditar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
        }

        etTCalorias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String caloriasNuevas =  StringUtil.isCadenaVacia(s.toString()) ? "-" : s.toString();
                tvCalorias.setText(caloriasNuevas + "\nCalorías");
            }
        });

        etTCarbohidratos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String carbsNuevos = s.toString();
                carbohidratos = StringUtil.isCadenaVacia(carbsNuevos) ? 0 : Integer.parseInt(carbsNuevos);
                configurarPieChart(carbohidratos, grasas, proteinas);
                tvCarbs.setText("" + Math.round(100f * carbohidratos / (carbohidratos + grasas + proteinas)) + "%\n" + carbohidratos + "g\nCarbohidratos");
            }
        });

        etTGrasas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String grasasNuevas = s.toString();
                grasas = StringUtil.isCadenaVacia(grasasNuevas) ? 0 : Integer.parseInt(grasasNuevas);;
                configurarPieChart(carbohidratos, grasas, proteinas);
                tvGrasas.setText("" + Math.round(100f * grasas / (carbohidratos + grasas + proteinas)) + "%\n" + grasas + "g\nGrasas");
            }
        });

        etTProteinas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String proteinasNuevas = s.toString();
                proteinas = StringUtil.isCadenaVacia(proteinasNuevas) ? 0 : Integer.parseInt(proteinasNuevas);
                configurarPieChart(carbohidratos, grasas, proteinas);
                tvProteinas.setText("" + Math.round(100f * proteinas / (carbohidratos + grasas + proteinas)) + "%\n" + proteinas + "g\nProteínas");
            }
        });

        btnEditar.setOnClickListener(v -> {
            actualizarComida();
            if (valido) {
                setResult(AniadirComidaActivity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(DetalleComidaActivity.this, "❌ Hay campos sin completar", Toast.LENGTH_SHORT).show();
            }

        });

        btnGuardar.setOnClickListener(v -> {
            insertarNuevaComida();
            if (!valido) {
                Toast.makeText(DetalleComidaActivity.this, "❌ Hay campos sin completar", Toast.LENGTH_SHORT).show();
            }
        });

        btnAtras.setOnClickListener(v -> {
            setResult(DetalleComidaActivity.RESULT_CANCELED);
            finish();
        });
    }

    private void configurarPieChart(int carbs, int grasas, int proteinas) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(carbs, "Carbs"));
        entries.add(new PieEntry(grasas, "Grasas"));
        entries.add(new PieEntry(proteinas, "Proteínas"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.parseColor("#FFA726"), Color.parseColor("#66BB6A"), Color.parseColor("#42A5F5")});
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.invalidate();
    }

    private void recuperarinforAlimento(int idComida) {
        supabaseApi.obtenerComidaPorId("eq." + idComida, "eq." + userId, "*").enqueue(new Callback<List<Comida>>() {
            @Override
            public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Comida comida = response.body().get(0);
                    comidaEnMemoria = comida;
                    calorias = comida.getCalorias();
                    carbohidratos = comida.getCarbohidratos(); // gramos
                    grasas = comida.getGrasas();         // gramos
                    proteinas = comida.getProteinas();     // gramos


                    if (esEdicion) {
                        configurarPieChart(carbohidratos, grasas, proteinas);
                        tvNombreComida.setText(comida.getDescComida());
                        tvTRacion.setText(montarTextoTamanioRacion(comida));
                        tvCalorias.setText(calorias + "\nCalorías");
                        tvCarbs.setText("" + Math.round(100f * carbohidratos / (carbohidratos + grasas + proteinas)) + "%\n" + carbohidratos + "g\nCarbohidratos");
                        tvGrasas.setText("" + Math.round(100f * grasas / (carbohidratos + grasas + proteinas)) + "%\n" + grasas + "g\nGrasas");
                        tvProteinas.setText("" + Math.round(100f * proteinas / (carbohidratos + grasas + proteinas)) + "%\n" + proteinas + "g\nProteínas");

                        etNombreComida.setText(comida.getDescComida());
                        etNRaciones.setText(Integer.toString(comida.getNumeroRaciones()));
                        etTRacion.setText(Integer.toString(comida.getTamanioRacion()));
                        etTCalorias.setText(Integer.toString(comida.getCalorias()));
                        etTCarbohidratos.setText(Integer.toString(comida.getCarbohidratos()));
                        etTGrasas.setText(Integer.toString(comida.getGrasas()));
                        etTProteinas.setText(Integer.toString(comida.getProteinas()));
                    } else {
                        configurarPieChart(carbohidratos, grasas, proteinas);
                        tvNombreComida.setText(comida.getDescComida());
                        tvTRacion.setText(montarTextoTamanioRacion(comida));
                        tvNRaciones.setText(montarTextoNumeroRacion(comida));
                        tvCalorias.setText(calorias + "\nCalorías");
                        tvCarbs.setText("" + Math.round(100f * carbohidratos / (carbohidratos + grasas + proteinas)) + "%\n" + carbohidratos + "g\nCarbohidratos");
                        tvGrasas.setText("" + Math.round(100f * grasas / (carbohidratos + grasas + proteinas)) + "%\n" + grasas + "g\nGrasas");
                        tvProteinas.setText("" + Math.round(100f * proteinas / (carbohidratos + grasas + proteinas)) + "%\n" + proteinas + "g\nProteínas");
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Comida>> call, Throwable t) {
            }
        });
    }

    private void insertarNuevaComida() {
        if (datosValidos()) {
            Comida comidaNueva = new Comida();
            comidaNueva.setId_usuario_fk(userId);
            comidaNueva.setDesc_comida(etNombreComida.getText().toString());
            comidaNueva.setnRaciones(Integer.parseInt(etNRaciones.getText().toString()));
            comidaNueva.settRacion(Integer.parseInt(etTRacion.getText().toString()));
            comidaNueva.setCalorias(Integer.parseInt(etTCalorias.getText().toString()));
            comidaNueva.setCarbohidratos(Integer.parseInt(etTCarbohidratos.getText().toString()));
            comidaNueva.setGrasas(Integer.parseInt(etTGrasas.getText().toString()));
            comidaNueva.setProteinas(Integer.parseInt(etTProteinas.getText().toString()));

            supabaseApi.insertarComida(comidaNueva).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("API", "Comida insertada");
                        Toast.makeText(DetalleComidaActivity.this, "✅ Comida creada", Toast.LENGTH_SHORT).show();
                        setResult(AniadirComidaActivity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(DetalleComidaActivity.this, "❌ Error en el guardado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API", "Error al insertar", t);
                }
            });
        }
    }

    private void actualizarComida() {
        if (datosValidos()) {
            Map<String, Object> comidaData = new HashMap<>();
            comidaData.put("desc_comida", etNombreComida.getText().toString());
            comidaData.put("nRaciones", Integer.parseInt(etNRaciones.getText().toString()));
            comidaData.put("tRacion", Integer.parseInt(etTRacion.getText().toString()));
            comidaData.put("calorias", Integer.parseInt(etTCalorias.getText().toString()));
            comidaData.put("carbohidratos", Integer.parseInt(etTCarbohidratos.getText().toString()));
            comidaData.put("grasas", Integer.parseInt(etTGrasas.getText().toString()));
            comidaData.put("proteinas", Integer.parseInt(etTProteinas.getText().toString()));
            supabaseApi.actualizarComida("eq." + comidaEnMemoria.getId(), comidaData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("API", "Comida actualizada");
                        Toast.makeText(DetalleComidaActivity.this, "✅ Comida editada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetalleComidaActivity.this, "❌ Error en la edicion", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API", "Error al actualizar", t);
                }
            });
        }
    }

    private boolean datosValidos() {
        valido = true;

        if (etNombreComida.getText().toString().trim().isEmpty()) {
            etNombreComida.setError("Requerido");
            valido = false;
        }

        if (etNRaciones.getText().toString().trim().isEmpty()) {
            etNRaciones.setError("Requerido");
            valido = false;
        }

        if (etTRacion.getText().toString().trim().isEmpty()) {
            etTRacion.setError("Requerido");
            valido = false;
        }

        if (etTCalorias.getText().toString().trim().isEmpty()) {
            etTCalorias.setError("Requerido");
            valido = false;
        }

        if (etTCarbohidratos.getText().toString().trim().isEmpty()) {
            etTCarbohidratos.setError("Requerido");
            valido = false;
        }

        if (etTGrasas.getText().toString().trim().isEmpty()) {
            etTGrasas.setError("Requerido");
            valido = false;
        }

        if (etTProteinas.getText().toString().trim().isEmpty()) {
            etTProteinas.setError("Requerido");
            valido = false;
        }

        return valido;
    }

    private String montarTextoTamanioRacion(Comida comida) {
        String mensaje = "";

        if (StringUtil.isCadenaVacia(comida.getUnidadDMedida())) {
            mensaje = "Sin datos";
        } else {
            switch (comida.getUnidadDMedida()) {
                case "g" :
                    mensaje = comida.getTamanioRacion() > 1 ? comida.getTamanioRacion() + " gramos" : comida.getTamanioRacion() + " gramo";
                    break;
                case "ml":
                    mensaje = comida.getTamanioRacion() + " ml";
                    break;
                default:
                    mensaje = "Sin datos";
            }
        }

        return mensaje;
    }

    private String montarTextoNumeroRacion(Comida comida) {
        return comida.getNumeroRaciones() > 1 ? comida.getNumeroRaciones() + " uds": comida.getNumeroRaciones() + " ud";
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }
}