package com.nutrivda.app.inicializacion;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.R;

public class FragmentPrioridad extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prioridad, container, false);

        RadioGroup rgPrioridad = view.findViewById(R.id.rgPrioridad);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        btnSiguiente.setOnClickListener(v -> {
            int selectedId = rgPrioridad.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Selecciona una opción", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);
                OnboardingData.getInstance().setPrioridad(selected.getText().toString());
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }
}
