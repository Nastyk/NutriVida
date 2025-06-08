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
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.ApiHelper;
import com.nutrivda.app.utils.CacheControlUtil;
import com.nutrivda.app.utils.StringUtil;

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
                agregarComidaAlDia(tipoComida.toLowerCase(), comida);
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
        //Peticion get con caché de 5 mins habiliutada
        Call<List<Comida>> conCache = supabaseApi.obtenerTodasLasComidas(
                "eq." + userId, "*", "public, max-age=300"
        );
        //Peticion si caché, para recuperar datos nuevos
        Call<List<Comida>> sinCache = supabaseApi.obtenerTodasLasComidas(
                "eq." + userId, "*", "no-cache"
        );

        ApiHelper.ejecutarGetConControlCache(this, "todas_comidas", conCache, sinCache, new Callback<List<Comida>>() {
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

    private void agregarComidaAlDia(String tipo, Comida comida) {
        String tipoCalorias = "";
        if ("desayuno".equalsIgnoreCase(tipo)) {
            tipoCalorias = "caloria_desayuno";
        } else if ("comida".equalsIgnoreCase(tipo)) {
            tipoCalorias = "caloria_comida";
        } else if ("cena".equalsIgnoreCase(tipo)) {
            tipoCalorias = "caloria_cena";
        }

        StringBuilder camposSelect = new StringBuilder();
        camposSelect.append(tipo);
        if (!StringUtil.isCadenaVacia(tipoCalorias)) {
            camposSelect.append(",");
            camposSelect.append(tipoCalorias);
        }

        Long nuevoId = (long) comida.getId();
        supabaseApi.obtenerDiaComida("eq." + userId, "eq." + fechaDeComida, camposSelect.toString()).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DiaCompletado diaCompletado = response.body().get(0);
                    List<Long> idListComidas =  new ArrayList<>();
                    if ("desayuno".equalsIgnoreCase(tipo)) {
                        idListComidas = diaCompletado.getDesayuno();
                    } else if ("comida".equalsIgnoreCase(tipo)) {
                        idListComidas = diaCompletado.getComida();
                    } else if ("cena".equalsIgnoreCase(tipo)) {
                        idListComidas = diaCompletado.getCena();
                    }

                    int caloriasComida = 0;
                    if ("desayuno".equalsIgnoreCase(tipo)) {
                        caloriasComida = diaCompletado.getCaloria_desayuno();
                    } else if ("comida".equalsIgnoreCase(tipo)) {
                        caloriasComida = diaCompletado.getCaloria_comida();
                    } else if ("cena".equalsIgnoreCase(tipo)) {
                        caloriasComida = diaCompletado.getCaloria_cena();
                    }

                    caloriasComida += comida.getCalorias();

                    if (idListComidas != null) {
                        idListComidas.add(nuevoId);
                    } else {
                        idListComidas =  new ArrayList<>();
                        idListComidas.add(nuevoId);
                    }

                    Map<String, Object> body = new HashMap<>();
                    body.put(tipo, idListComidas);
                    if ("desayuno".equalsIgnoreCase(tipo)) {
                        body.put("caloria_desayuno", caloriasComida);
                    } else if ("comida".equalsIgnoreCase(tipo)) {
                        body.put("caloria_comida", caloriasComida);
                    } else if ("cena".equalsIgnoreCase(tipo)) {
                        body.put("caloria_cena", caloriasComida);
                    }
                    supabaseApi.actualizarDiaComida("eq." + userId, "eq." + fechaDeComida, body).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            CacheControlUtil.marcarParaRefrescar(AniadirComidaActivity.this, "dias_completados");
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
                    data.put("completado", true);
                    data.put(tipo, ids);
                    if ("desayuno".equalsIgnoreCase(tipo)) {
                        data.put("caloria_desayuno", comida.getCalorias());
                    } else if ("comida".equalsIgnoreCase(tipo)) {
                        data.put("caloria_comida", comida.getCalorias());
                    } else if ("cena".equalsIgnoreCase(tipo)) {
                        data.put("caloria_cena", comida.getCalorias());
                    }
                    supabaseApi.insertarDiaCompletado(data).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            CacheControlUtil.marcarParaRefrescar(AniadirComidaActivity.this, "dias_completados");
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
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
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
