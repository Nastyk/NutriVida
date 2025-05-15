package com.nutrivda.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentComida extends Fragment {
    private TextView tvFecha, tvDesayuno, tvComida, tvCena;
    private TextView tvCalDesayuno, tvCalComida, tvCalCena, tvTotal;
    private ImageButton btnBorrarDesayuno, btnBorrarComida, btnBorrarCena;
    private Button btnGuardar, btnAnadirDesayuno, btnAnadirComida, btnAnadirCena;
    private CheckBox cbDesayuno, cbComida, cbCena;

    public FragmentComida() {
        // Constructor público vacío requerido por Android
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comida, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Yo conecto todos los elementos visuales
        tvFecha = view.findViewById(R.id.tvFechaComida);
        tvDesayuno = view.findViewById(R.id.tvDesayunoSeleccionado);
        tvComida = view.findViewById(R.id.tvComidaSeleccionada);
        tvCena = view.findViewById(R.id.tvCenaSeleccionada);
        tvCalDesayuno = view.findViewById(R.id.tvCaloriasDesayuno);
        tvCalComida = view.findViewById(R.id.tvCaloriasComida);
        tvCalCena = view.findViewById(R.id.tvCaloriasCena);
        tvTotal = view.findViewById(R.id.tvTotalKcal);

        btnAnadirDesayuno = view.findViewById(R.id.btnAnadirDesayuno);
        btnAnadirComida = view.findViewById(R.id.btnAnadirComida);
        btnAnadirCena = view.findViewById(R.id.btnAnadirCena);

        btnBorrarDesayuno = view.findViewById(R.id.btnBorrarDesayuno);
        btnBorrarComida = view.findViewById(R.id.btnBorrarComida);
        btnBorrarCena = view.findViewById(R.id.btnBorrarCena);

        cbDesayuno = view.findViewById(R.id.cbDesayuno);
        cbComida = view.findViewById(R.id.cbComida);
        cbCena = view.findViewById(R.id.cbCena);

        btnGuardar = view.findViewById(R.id.btnGuardarComida);

        // Yo configuro la fecha actual por defecto
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvFecha.setText("Comidas del día: " + fechaHoy);

        // Simulo la carga de datos (lo conectarás a BD si lo deseas)
        cargarDatosGuardados();

        // Guardar comidas en SharedPreferences al pulsar
        btnGuardar.setOnClickListener(v -> guardarComidas());
    }

    private void guardarComidas() {
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences("AppPrefs", 0)
                .edit();

        editor.putString("comida_desayuno", tvDesayuno.getText().toString());
        editor.putString("comida_comida", tvComida.getText().toString());
        editor.putString("comida_cena", tvCena.getText().toString());
        editor.putInt("cal_desayuno", getCalorias(tvCalDesayuno));
        editor.putInt("cal_comida", getCalorias(tvCalComida));
        editor.putInt("cal_cena", getCalorias(tvCalCena));
        editor.putBoolean("hecho_desayuno", cbDesayuno.isChecked());
        editor.putBoolean("hecho_comida", cbComida.isChecked());
        editor.putBoolean("hecho_cena", cbCena.isChecked());
        editor.apply();

        Toast.makeText(requireContext(), "✅ Comidas guardadas", Toast.LENGTH_SHORT).show();
    }

    private void cargarDatosGuardados() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("AppPrefs", 0);

        tvDesayuno.setText(prefs.getString("comida_desayuno", "Ninguno"));
        tvComida.setText(prefs.getString("comida_comida", "Ninguna"));
        tvCena.setText(prefs.getString("comida_cena", "Ninguna"));
        tvCalDesayuno.setText("Calorías: " + prefs.getInt("cal_desayuno", 0));
        tvCalComida.setText("Calorías: " + prefs.getInt("cal_comida", 0));
        tvCalCena.setText("Calorías: " + prefs.getInt("cal_cena", 0));
        cbDesayuno.setChecked(prefs.getBoolean("hecho_desayuno", false));
        cbComida.setChecked(prefs.getBoolean("hecho_comida", false));
        cbCena.setChecked(prefs.getBoolean("hecho_cena", false));

        int total = prefs.getInt("cal_desayuno", 0)
                + prefs.getInt("cal_comida", 0)
                + prefs.getInt("cal_cena", 0);

        tvTotal.setText("Total kcal: " + total);
    }

    private int getCalorias(TextView tv) {
        try {
            return Integer.parseInt(tv.getText().toString().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}