package com.nutrivda.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nutrivda.app.adapter.ComidaAdapter;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.databinding.FragmentComidaBinding;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.utils.ApiHelper;
import com.nutrivda.app.utils.CacheControlUtil;
import com.nutrivda.app.viewmodel.SharedViewModelCalendarioDia;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentComida extends Fragment {

    private EditText etBuscarComida;
    private RecyclerView rvResultados;
    private ComidaAdapter adapter;
    private List<Comida> listaComidas = new ArrayList<>();
    private List<Comida> todasLasComidas = new ArrayList<>();
    private SupabaseApi supabaseApi;
    private int userId = 0;
    private SharedViewModelCalendarioDia viewModel;
    private FragmentComidaBinding binding;
    private ActivityResultLauncher<Intent> comidaLauncher;
    private FloatingActionButton fabAgregarComida;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comidaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarComidas();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComidaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etBuscarComida = view.findViewById(R.id.etBuscarComida);
        rvResultados = view.findViewById(R.id.rvResultados);
        fabAgregarComida = view.findViewById(R.id.fabAgregarComida);

        rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModelCalendarioDia.class);
        this.userId = viewModel.getUserId().getValue()  != null ? viewModel.getUserId().getValue().intValue() : getUserId();

        fabAgregarComida.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DetalleComidaActivity.class);
            intent.putExtra("esAdicion", true);
            comidaLauncher.launch(intent);
        });

        adapter = new ComidaAdapter(listaComidas, new ComidaAdapter.OnComidaClickListener() {
            @Override
            public void onComidaClick(Comida comida) {}

            @Override
            public void onGuardarClick(Comida comida) {}

            @Override
            public void onEliminarClick(Comida comida) {
                mostrarDialogoConfirmacion(comida);
            }
        }, comidaLauncher);

        adapter.setMostrarBoton(false);
        adapter.setEsEdicion(true);

        rvResultados.setAdapter(adapter);

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

        ApiHelper.ejecutarGetConControlCache(getContext(), "todas_comidas", conCache, sinCache, new Callback<List<Comida>>() {
            @Override
            public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                //progressBar.setVisibility(View.GONE);
                listaComidas.clear();

                if (response.isSuccessful() && response.body() != null) {
                    todasLasComidas = response.body();
                    listaComidas.addAll(todasLasComidas);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comida>> call, Throwable t) {
                Toast.makeText(getContext(), "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void mostrarDialogoConfirmacion(Comida comida) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar comida")
                .setMessage("¿Deseas eliminar \"" + comida.getDescComida() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    supabaseApi.eliminarComida("eq." + comida.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                CacheControlUtil.debeRefrescar(getContext() , "todas_comidas");
                                Toast.makeText(getContext(), "Comida eliminada", Toast.LENGTH_SHORT).show();
                                cargarComidas(); // recargar lista
                            } else {
                                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);  // Retorna -1 si no encuentra el userId
    }
}