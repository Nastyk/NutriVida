package com.nutrivda.app.inicializacion;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.R;

import java.util.*;

public class FragmentRestricciones extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restricciones, container, false);

        CheckBox cbVegetariano = view.findViewById(R.id.cbVegetariano);
        CheckBox cbVegano = view.findViewById(R.id.cbVegano);
        CheckBox cbSinGluten = view.findViewById(R.id.cbSinGluten);
        CheckBox cbSinLactosa = view.findViewById(R.id.cbSinLactosa);
        CheckBox cbNinguna = view.findViewById(R.id.cbNinguna);
        EditText etOtra = view.findViewById(R.id.etOtra);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        btnSiguiente.setOnClickListener(v -> {
            List<String> restricciones = new ArrayList<>();
            if (cbVegetariano.isChecked()) restricciones.add("Vegetariano");
            if (cbVegano.isChecked()) restricciones.add("Vegano");
            if (cbSinGluten.isChecked()) restricciones.add("Sin gluten");
            if (cbSinLactosa.isChecked()) restricciones.add("Sin lactosa");
            if (cbNinguna.isChecked()) restricciones.add("Ninguna");
            String otra = etOtra.getText().toString().trim();
            if (!otra.isEmpty()) restricciones.add(otra);

            if (restricciones.isEmpty()) {
                Toast.makeText(getContext(), "Selecciona al menos una opción", Toast.LENGTH_SHORT).show();
            } else {
                OnboardingData.getInstance().setRestricciones(restricciones);
                ((OnboardingActivity) requireActivity()).avanzarPagina();
            }
        });

        return view;
    }
}
