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

public class FragmentPrioridad extends Fragment {

    private SharedViewModelOnboarding viewModelOnboarding;
    private RadioGroup rgPrioridad;
    private Button btnSiguiente;
    private ImageButton btnAvanzarTest;
    private ImageButton btnRetrocederTest;

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
        View view = inflater.inflate(R.layout.fragment_prioridad, container, false);

        rgPrioridad = view.findViewById(R.id.rgPrioridad);
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
            int selectedId = rgPrioridad.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Selecciona una opción", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);
                viewModelOnboarding.setObjetivo(selected.getText().toString());
                viewModelOnboarding.setCuestionarioObjetivoCompletado(true);
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }

    public void onVisible() {
        //Usamos ViewModel para compàrtir datos entre los fragments de la actividad OnboardingActivity
        viewModelOnboarding = new ViewModelProvider(requireActivity()).get(SharedViewModelOnboarding.class);
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioObjetivoCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioFisicoCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioObjetivoCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioRestriccionesCompletado().getValue())) {
            btnAvanzarTest.setVisibility(View.VISIBLE);
        }
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioFisicoCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
    }
}
