package com.nutrivda.app.test;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrivda.app.R;
import com.nutrivda.app.test.ResultadoAdapter;
import com.nutrivda.app.test.ResultadoTest;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistorialResultadoActivity extends AppCompatActivity {
    private RecyclerView recyclerHistorial;
    private ResultadoAdapter adapter;
    private ArrayList<ResultadoTest> listaResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_resultado);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        cargarHistorial();
    }

    private void cargarHistorial() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String json = prefs.getString("historial_resultados", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ResultadoTest>>(){}.getType();
        listaResultados = gson.fromJson(json, type);

        adapter = new ResultadoAdapter(listaResultados);
        recyclerHistorial.setAdapter(adapter);
    }
}