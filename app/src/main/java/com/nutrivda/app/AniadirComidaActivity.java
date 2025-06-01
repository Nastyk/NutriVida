package com.nutrivda.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.adapter.ComidaAdapter;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.data.SupabaseApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AniadirComidaActivity extends AppCompatActivity {

    private EditText etBuscarComida;
    private TextView tvTipoComida;
    private RecyclerView rvResultados;
    private ImageButton btnAtras;
    private ComidaAdapter adapter;
    private List<Comida> listaComidas = new ArrayList<>();
    private List<Comida> todasLasComidas = new ArrayList<>();
    private SupabaseApi supabaseApi;
    private String tipoComida, fechaDeComida;
    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_comida);

        tvTipoComida = findViewById(R.id.tvTipoComida);
        etBuscarComida = findViewById(R.id.etBuscarComida);
        rvResultados = findViewById(R.id.rvResultados);
        btnAtras = findViewById(R.id.btnBack);

        rvResultados.setLayoutManager(new LinearLayoutManager(this));

        tipoComida = getIntent().getStringExtra("tipo_comida");
        tvTipoComida.setText(tipoComida);
        fechaDeComida = getIntent().getStringExtra("fecha_comida");
        userId = getIntent().getIntExtra("user_id",0) > 0 ? getIntent().getIntExtra("user_id", 0) : getUserId();

        adapter = new ComidaAdapter(listaComidas, new ComidaAdapter.OnComidaClickListener() {
            @Override
            public void onComidaClick(Comida comida) {}

            @Override
            public void onEliminarClick(Comida comida) {}

            @Override
            public void onGuardarClick(Comida comida) {
                agregarComidaAlDia(tipoComida.toLowerCase(), (long) comida.getId());
            }
        }, null);

        rvResultados.setAdapter(adapter);

        btnAtras.setOnClickListener(v -> {
            setResult(AniadirComidaActivity.RESULT_CANCELED);
            finish();
        });

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        // Obtener todas las comidas una vez
        cargarComidas();

        // Filtrado en tiempo real
        etBuscarComida.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarComidas(s.toString());
            }
        });
    }

    private void cargarComidas() {
       // progressBar.setVisibility(View.VISIBLE);

        supabaseApi.obtenerTodasLasComidas("eq." + userId, "*").enqueue(new Callback<List<Comida>>() {
            @Override
            public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                //progressBar.setVisibility(View.GONE);
                listaComidas.clear();

                if (response.isSuccessful() && response.body() != null) {
                    todasLasComidas = response.body();
                    listaComidas.addAll(todasLasComidas);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AniadirComidaActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comida>> call, Throwable t) {
               // progressBar.setVisibility(View.GONE);
                Toast.makeText(AniadirComidaActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarComidaAlDia(String tipo, Long nuevoId) {
        supabaseApi.obtenerDiaComida("eq." + userId, "eq." + fechaDeComida, tipo).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Map<String, Object> registro = response.body().get(0);
                    Object listaBruta = registro.get(tipo);

                    List<Long> idList = new ArrayList<>();
                    if (listaBruta instanceof List<?>) {
                        for (Object item : (List<?>) listaBruta) {
                            if (item instanceof Number) {
                                idList.add(((Number) item).longValue());
                            }
                        }
                    }
                    idList.add(nuevoId);
                    Map<String, Object> body = new HashMap<>();
                    body.put(tipo, idList);
                    supabaseApi.actualizarDiaComida("eq." + userId, "eq." + fechaDeComida, body).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(AniadirComidaActivity.this, "✅ Comida añadida", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AniadirComidaActivity.this, "❌ Error al actualizar comida", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Map<String, Object> data = new HashMap<>();
                    List<Long> ids = new ArrayList<>();
                    ids.add(nuevoId);
                    data.put("id_usuario_fk", userId);
                    data.put("fecha", fechaDeComida);
                    data.put("completado", false);
                    data.put(tipo, ids);
                    supabaseApi.insertarDiaCompletado(data).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(AniadirComidaActivity.this, "✅ Comida guardada", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AniadirComidaActivity.this, "❌ Error al guardar comida", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(AniadirComidaActivity.this, "❌ Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarComidas(String query) {
        List<Comida> filtradas = new ArrayList<>();
        for (Comida comida : todasLasComidas) {
            if (comida.getDescComida().toLowerCase().contains(query.toLowerCase())) {
                filtradas.add(comida);
            }
        }
        listaComidas.clear();
        listaComidas.addAll(filtradas);
        adapter.notifyDataSetChanged();
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }
}
