package com.nutrivda.app.inicializacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nutrivda.app.MainActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.cohereIA.CohereService;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.inicializacion.OnboardingData;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.pojo.PlanNutricional;
import com.nutrivda.app.test.ResultadoActivity;

import java.util.List;

public class FragmentResumenOnboarding extends Fragment {

    private int userId;
    private SupabaseApi supabaseApi;
    private DatosUsuario datosUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen_onboarding, container, false);

        TextView tvResumen = view.findViewById(R.id.tvResumen);
        Button btnGenerarPlan = view.findViewById(R.id.btnGenerarPlan);

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);
        userId = getUserId();
        obtenerDatosUsusario();

        // Muestro el resumen del onboarding al usuario
        OnboardingData data = OnboardingData.getInstance();
        String resumen  = "✅ Actividad física: " + data.getActividadFisica() + "\n\n"
                + "🎯 Prioridad principal: " + data.getPrioridad() + "\n\n"
                + "🥗 Restricciones: " + String.join(", ", data.getRestricciones()) + "\n\n"
                + "🍽 Organización de comidas: " + data.getOrganizacionComidas();
        tvResumen.setText(resumen);

        btnGenerarPlan.setOnClickListener(v -> {

            // Creo el diálogo de carga con Lottie
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = inflater.inflate(R.layout.dialog_cargando_plan, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            AlertDialog loadingDialog = builder.create();
            loadingDialog.show();

            // Creo una instancia del servicio que me conecta con la IA de Cohere
            CohereService ia = new CohereService(supabaseApi, datosUsuario);

            // Le paso los datos del usuario y defino qué hacer con la respuesta
            ia.generarPlan(OnboardingData.getInstance(), new CohereService.Callback() {
                @Override
                public void onSuccess(PlanNutricional planNutricional) {

                    // 🔒 Guardo que el onboarding ha sido completado
                    SharedPreferences prefs = requireActivity().getSharedPreferences("NutriVidaPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("onboardingCompletado", true).apply();


                    // 2. Lanzo ResultadoActivity para mostrar la respuesta de la IA
                    Intent intent = new Intent(getActivity(), ResultadoActivity.class);
                    intent.putExtra("planNutricional", planNutricional); // Le paso la respuesta como extra
                    startActivity(intent);

                    // 3. Cierro el onboarding para no volver atrás
                    requireActivity().finish();

                    loadingDialog.dismiss();

                }

                @Override
                public void onError(String error) {
                    // Si algo falla con la IA, muestro el error al usuario
                    Toast.makeText(getContext(), "Error con IA: " + error, Toast.LENGTH_LONG).show();

                    loadingDialog.dismiss();

                }
            });

        });



        return view;
    }

    private void obtenerDatosUsusario() {
        // Obtener datos desde la tabla datos_usuario
        supabaseApi.obtenerDatosUsuario("eq." + userId).enqueue(new retrofit2.Callback<List<DatosUsuario>>() {
            @Override
            public void onResponse(retrofit2.Call<List<DatosUsuario>> call, retrofit2.Response<List<DatosUsuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DatosUsuario datos = response.body().get(0);
                    if (datos != null) {
                        datosUsuario = datos;
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<DatosUsuario>> call, Throwable t) {
            }
        });
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}
