package com.nutrivda.app.inicializacion.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.R;
import com.nutrivda.app.inicializacion.activity.OnboardingActivity;
import com.nutrivda.app.viewmodel.SharedViewModelOnboarding;

public class FragmentActividadFisica extends Fragment {

    private RadioGroup rgActividad;
    private Button btnSiguiente;
    private ImageButton btnAvanzarTest;
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
        // Aquí inflo el layout de este fragmento
        View view = inflater.inflate(R.layout.fragment_actividad_fisica, container, false);

        rgActividad = view.findViewById(R.id.rgActividad);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);
        btnAvanzarTest =  view.findViewById(R.id.btnIrAdelante);

        //Usamos ViewModel para compàrtir datos entre los fragments de la actividad OnboardingActivity


        btnAvanzarTest.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).avanzarPagina();
        });

        // Al pulsar el botón, guardo la respuesta y paso al siguiente fragmento
        btnSiguiente.setOnClickListener(v -> {
            int selectedId = rgActividad.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);

                viewModelOnboarding.setActividadFisica(selected.getText().toString());
                viewModelOnboarding.setCuestionarioFisicoCompletado(true);

                // Le digo a la actividad que avance al siguiente fragmento
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }

    public void onVisible() {
        viewModelOnboarding = new ViewModelProvider(requireActivity()).get(SharedViewModelOnboarding.class);
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioFisicoCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioObjetivoCompletado().getValue())) {
            btnAvanzarTest.setVisibility(View.VISIBLE);
        }
    }
}
