package com.nutrivda.app.inicializacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.MainActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.cohereIA.CohereService;
import com.nutrivda.app.test.ResultadoActivity;

public class FragmentResumenOnboarding extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen_onboarding, container, false);

        TextView tvResumen = view.findViewById(R.id.tvResumen);
        Button btnGenerarPlan = view.findViewById(R.id.btnGenerarPlan);

        // Muestro el resumen del onboarding al usuario
        OnboardingData data = OnboardingData.getInstance();
        String resumen = "Actividad física: " + data.getActividadFisica() + "\n"
                + "Prioridad: " + data.getPrioridad() + "\n"
                + "Restricciones: " + String.join(", ", data.getRestricciones()) + "\n"
                + "Comidas: " + data.getOrganizacionComidas();
        tvResumen.setText(resumen);

        btnGenerarPlan.setOnClickListener(v -> {
            // Creo una instancia del servicio que me conecta con la IA de Cohere
            CohereService ia = new CohereService();

            // Le paso los datos del usuario y defino qué hacer con la respuesta
            ia.generarPlan(OnboardingData.getInstance(), new CohereService.Callback() {
                @Override
                public void onSuccess(String respuestaIA) {
                    // 1. Guardo que el onboarding ya fue completado
                    SharedPreferences prefs = requireActivity().getSharedPreferences("NutriVidaPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("onboardingCompletado", true).apply();

                    // 2. Lanzo ResultadoActivity para mostrar la respuesta de la IA
                    Intent intent = new Intent(getActivity(), ResultadoActivity.class);
                    intent.putExtra("respuestaIA", respuestaIA); // Le paso la respuesta como extra
                    startActivity(intent);

                    // 3. Cierro el onboarding para no volver atrás
                    requireActivity().finish();
                }

                @Override
                public void onError(String error) {
                    // Si algo falla con la IA, muestro el error al usuario
                    Toast.makeText(getContext(), "Error con IA: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });



        return view;
    }
}
