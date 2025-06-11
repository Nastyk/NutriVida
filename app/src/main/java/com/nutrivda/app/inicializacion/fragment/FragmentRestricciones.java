package com.nutrivda.app.inicializacion.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.R;
import com.nutrivda.app.inicializacion.activity.OnboardingActivity;
import com.nutrivda.app.viewmodel.SharedViewModelOnboarding;

import java.util.*;

public class FragmentRestricciones extends Fragment {

    private CheckBox cbVegetariano;
    private CheckBox cbVegano;
    private CheckBox cbSinGluten;
    private CheckBox cbSinLactosa;
    private CheckBox cbNinguna;
    private EditText etOtra;
    private Button btnSiguiente;
    private ImageButton btnAvanzarTest;
    private ImageButton btnRetrocederTest;
    private SharedViewModelOnboarding viewModelOnboarding;

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            onVisible();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restricciones, container, false);

        cbVegetariano = view.findViewById(R.id.cbVegetariano);
        cbVegano = view.findViewById(R.id.cbVegano);
        cbSinGluten = view.findViewById(R.id.cbSinGluten);
        cbSinLactosa = view.findViewById(R.id.cbSinLactosa);
        cbNinguna = view.findViewById(R.id.cbNinguna);
        etOtra = view.findViewById(R.id.etOtra);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);
        btnAvanzarTest =  view.findViewById(R.id.btnIrAdelante);
        btnRetrocederTest =  view.findViewById(R.id.btnIrAtras);

        btnRetrocederTest.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).retrocederPagina();
        });

        btnAvanzarTest.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).avanzarPagina();
        });

        btnSiguiente.setOnClickListener(v -> {

            if (respuestaValida()) {
                List<String> restricciones = new ArrayList<>();
                if (cbVegetariano.isChecked()) restricciones.add("Vegetariano");
                if (cbVegano.isChecked()) restricciones.add("Vegano");
                if (cbSinGluten.isChecked()) restricciones.add("Sin gluten");
                if (cbSinLactosa.isChecked()) restricciones.add("Sin lactosa");
                if (cbNinguna.isChecked()) restricciones.add("Ninguna");
                String otra = etOtra.getText().toString().trim();
                if (!otra.isEmpty()) restricciones.add(otra);

                viewModelOnboarding.setRestricciones(restricciones);
                viewModelOnboarding.setCuestionarioRestriccionesCompletado(true);
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }

    private boolean respuestaValida() {
        String otra = etOtra.getText().toString().trim();

        if (!cbNinguna.isChecked() && !cbVegano.isChecked() && !cbVegetariano.isChecked() && !cbSinGluten.isChecked() && !cbSinLactosa.isChecked() && otra.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos una opción o especifica tu restricción alimentaria", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cbNinguna.isChecked()) {
            if (cbVegano.isChecked() || cbVegetariano.isChecked() || cbSinGluten.isChecked() || cbSinLactosa.isChecked() || !otra.isEmpty()) {
                Toast.makeText(getContext(), "No puedes seleccionar la opcion Ninguna con otras opciones", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void onVisible() {
        viewModelOnboarding = new ViewModelProvider(requireActivity()).get(SharedViewModelOnboarding.class);
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioRestriccionesCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioObjetivoCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioRestriccionesCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioOrganizacionCompletado().getValue())) {
            btnAvanzarTest.setVisibility(View.VISIBLE);
        }
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioObjetivoCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
    }
}
