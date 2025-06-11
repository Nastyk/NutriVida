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

public class FragmentOrganizacionComidas extends Fragment {

    private RadioGroup rgComidas;
    private Button btnSiguiente;
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
        View view = inflater.inflate(R.layout.fragment_organizacion_comidas, container, false);

        rgComidas = view.findViewById(R.id.rgComidas);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);
        btnRetrocederTest =  view.findViewById(R.id.btnIrAtras);

        btnRetrocederTest.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).retrocederPagina();
        });

        btnSiguiente.setOnClickListener(v -> {
            int selectedId = rgComidas.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Selecciona una opción", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);
                viewModelOnboarding.setOrganizacion(selected.getText().toString());
                viewModelOnboarding.setCuestionarioOrganizacionCompletado(true);
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }

    public void onVisible() {
        //Usamos ViewModel para compàrtir datos entre los fragments de la actividad OnboardingActivity
        viewModelOnboarding = new ViewModelProvider(requireActivity()).get(SharedViewModelOnboarding.class);
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioOrganizacionCompletado().getValue()) && Boolean.TRUE.equals(viewModelOnboarding.cuestionarioRestriccionesCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
        if (Boolean.TRUE.equals(viewModelOnboarding.cuestionarioRestriccionesCompletado().getValue())) {
            btnRetrocederTest.setVisibility(View.VISIBLE);
        }
    }
}
