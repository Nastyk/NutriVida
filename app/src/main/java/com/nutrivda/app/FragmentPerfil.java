package com.nutrivda.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.database.DatabaseHelper;

public class FragmentPerfil extends Fragment {

    private EditText etNombre, etApellido1, etApellido2, etPeso, etAltura, etEdad;
    private TextView tvIMC;
    private Button btnGuardar;
    private DatabaseHelper dbHelper;
    private int userId;

    public FragmentPerfil() {
        // Constructor público vacío requerido por Android
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Yo inflo el layout del perfil para este fragmento
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Yo obtengo el contexto y datos del usuario
        dbHelper = new DatabaseHelper(requireContext());
        userId = getUserId();

        // Yo conecto los elementos visuales
        etNombre = view.findViewById(R.id.etNombre);
        etApellido1 = view.findViewById(R.id.etApellido1);
        etApellido2 = view.findViewById(R.id.etApellido2);
        etPeso = view.findViewById(R.id.etPeso);
        etAltura = view.findViewById(R.id.etAltura);
        etEdad = view.findViewById(R.id.etEdad);
        tvIMC = view.findViewById(R.id.tvIMCPerfil);
        btnGuardar = view.findViewById(R.id.btnGuardarPerfil);

        // Acción al pulsar "Guardar"
        btnGuardar.setOnClickListener(v -> guardarPerfil());

        // Yo cargo los datos actuales si existen
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);
        etNombre.setText(prefs.getString("nombre", ""));
        etApellido1.setText(prefs.getString("apellido1", ""));
        etApellido2.setText(prefs.getString("apellido2", ""));
        etPeso.setText(prefs.getString("peso", ""));
        etAltura.setText(prefs.getString("altura", ""));
        etEdad.setText(prefs.getString("edad", ""));

        calcularIMC();
    }

    private void guardarPerfil() {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("AppPrefs", 0).edit();
        editor.putString("nombre", etNombre.getText().toString());
        editor.putString("apellido1", etApellido1.getText().toString());
        editor.putString("apellido2", etApellido2.getText().toString());
        editor.putString("peso", etPeso.getText().toString());
        editor.putString("altura", etAltura.getText().toString());
        editor.putString("edad", etEdad.getText().toString());
        editor.apply();

        calcularIMC();
    }

    private void calcularIMC() {
        try {
            float peso = Float.parseFloat(etPeso.getText().toString());
            float alturaCm = Float.parseFloat(etAltura.getText().toString());
            float alturaM = alturaCm / 100f;
            float imc = peso / (alturaM * alturaM);
            tvIMC.setText(String.format("%.2f", imc));
        } catch (Exception e) {
            tvIMC.setText("--");
        }
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);
        return prefs.getInt("userId", -1);
    }
}