package com.nutrivda.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nutrivda.app.adapter.ComidaAdapter;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.data.SupabaseApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AniadirComidaActivity extends AppCompatActivity {

    private EditText etBuscarComida;
    private RecyclerView rvResultados;
    private ProgressBar progressBar;
    private ComidaAdapter adapter;
    private List<Comida> listaComidas = new ArrayList<>();
    private List<Comida> todasLasComidas = new ArrayList<>();
    private SupabaseApi supabaseApi;
    private String tipoComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_comida);

        etBuscarComida = findViewById(R.id.etBuscarComida);
        rvResultados = findViewById(R.id.rvResultados);

        rvResultados.setLayoutManager(new LinearLayoutManager(this));

        tipoComida = getIntent().getStringExtra("tipo_comida");

        adapter = new ComidaAdapter(listaComidas, comida -> {
            // Acción opcional al pulsar el botón "+"
        });

        rvResultados.setAdapter(adapter);

        // Confirmar selección
        /*btnConfirmarSeleccion.setOnClickListener(v -> {
            Comida comidaSeleccionada = adapter.getSelectedComida();
            if (comidaSeleccionada != null) {
                Intent intent = new Intent();
                intent.putExtra("recetaSeleccionada", comidaSeleccionada.getDescComida());
                intent.putExtra("calorias", comidaSeleccionada.getCalorias());
                intent.putExtra("tipoComida", tipoComida);

                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Selecciona una comida antes de continuar", Toast.LENGTH_SHORT).show();
            }
        });*/

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

        supabaseApi.obtenerTodasLasComidas().enqueue(new Callback<List<Comida>>() {
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
}
