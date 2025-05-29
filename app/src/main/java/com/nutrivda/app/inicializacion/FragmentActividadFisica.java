package com.nutrivda.app.inicializacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.R;

public class FragmentActividadFisica extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Aquí inflo el layout de este fragmento
        View view = inflater.inflate(R.layout.fragment_actividad_fisica, container, false);

        RadioGroup rgActividad = view.findViewById(R.id.rgActividad);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        // Al pulsar el botón, guardo la respuesta y paso al siguiente fragmento
        btnSiguiente.setOnClickListener(v -> {
            int selectedId = rgActividad.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);
                OnboardingData.getInstance().setActividadFisica(selected.getText().toString());

                // Le digo a la actividad que avance al siguiente fragmento
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }
}
