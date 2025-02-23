package com.nutrivda.app;

import android.content.Intent;
import android.os.Bundle;
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
    private Button btnBuscar, btnConfirmarSeleccion;
    private RecyclerView rvResultados;
    private ProgressBar progressBar;
    private ComidaAdapter adapter;
    private List<Comida> listaComidas = new ArrayList<>();
    private SupabaseApi supabaseApi;
    private String tipoComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_comida);

        etBuscarComida = findViewById(R.id.etBuscarComida);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnConfirmarSeleccion = findViewById(R.id.btnConfirmarSeleccion);
        rvResultados = findViewById(R.id.rvResultados);
        progressBar = findViewById(R.id.progressBar);

        rvResultados.setLayoutManager(new LinearLayoutManager(this));

        tipoComida = getIntent().getStringExtra("tipo_comida");

        adapter = new ComidaAdapter(listaComidas, comida -> {
            btnConfirmarSeleccion.setVisibility(View.VISIBLE);
        });

        // Botón para confirmar la selección
        btnConfirmarSeleccion.setOnClickListener(v -> {
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
        });


        rvResultados.setAdapter(adapter);

        // Inicializar Supabase API
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        btnBuscar.setOnClickListener(v -> buscarComidas());
    }

    private void buscarComidas() {
        String filtro = etBuscarComida.getText().toString().trim();
        String filtroQuery = "ilike.%"+filtro+"%";

        // Mostrar ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        supabaseApi.obtenerTodasLasComidas().enqueue(new Callback<List<Comida>>() {
            @Override
            public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                progressBar.setVisibility(View.GONE);
                listaComidas.clear();

                if (response.isSuccessful() && response.body() != null) {
                    List<Comida> todasLasComidas = response.body();

                    // Filtrar en Java: buscar "huevo" en cualquier parte del texto
                    for (Comida comida : todasLasComidas) {
                        if (comida.getDescComida().toLowerCase().contains(filtro.toLowerCase())) {
                            listaComidas.add(comida);
                        }
                    }

                    if (listaComidas.isEmpty()) {
                        Toast.makeText(AniadirComidaActivity.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AniadirComidaActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Comida>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AniadirComidaActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
