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
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.MainActivity;
import com.nutrivda.app.R;
import com.nutrivda.app.cohereIA.CohereService;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.inicializacion.OnboardingData;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.pojo.PlanNutricional;
import com.nutrivda.app.test.ResultadoActivity;
import com.nutrivda.app.viewmodel.SharedViewModelOnboarding;

import java.util.List;
import java.util.Objects;

public class FragmentResumenOnboarding extends Fragment {

    private int userId;
    private SupabaseApi supabaseApi;
    private DatosUsuario datosUsuario;
    private Button btnGenerarPlan, btnRealizarTests;
    private TextView tvActividadFisica, tvObjetivo, tvRestricciones, tvOrganizacion;
    private SharedViewModelOnboarding viewModelOnboarding;

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            // Muestro el resumen del onboarding al usuario
            tvActividadFisica.setText(viewModelOnboarding.getActividadFisica().getValue());
            tvObjetivo.setText(viewModelOnboarding.getObjetivo().getValue());
            tvRestricciones.setText(String.join(", ", Objects.requireNonNull(viewModelOnboarding.getRestricciones().getValue())));
            tvOrganizacion.setText(viewModelOnboarding.getOrganizacion().getValue());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen_onboarding, container, false);

        tvActividadFisica = view.findViewById(R.id.tvActividadFisica);
        tvObjetivo = view.findViewById(R.id.tvObjetivo);
        tvRestricciones = view.findViewById(R.id.tvRestricciones);
        tvOrganizacion = view.findViewById(R.id.tvOrganizacion);
        btnGenerarPlan = view.findViewById(R.id.btnGenerarPlan);
        btnRealizarTests = view.findViewById(R.id.btnRealizarTests);
        viewModelOnboarding = new ViewModelProvider(requireActivity()).get(SharedViewModelOnboarding.class);

        // Inicializar API de Supabase
        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);
        userId = getUserId();
        obtenerDatosUsusario();

        btnRealizarTests.setOnClickListener(v -> {
            ((OnboardingActivity) requireActivity()).getViewPager().setCurrentItem(0, true);
        });

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
            ia.generarPlan(viewModelOnboarding, new CohereService.Callback() {
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
                    // Si la primera peticion falla, lanzo una nueva
                    ia.generarPlan(viewModelOnboarding, new CohereService.Callback() {
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

                        }
                    });

                    loadingDialog.dismiss();

                }
            });

        });



        return view;
    }

    private void obtenerDatosUsusario() {
        // Obtener datos desde la tabla datos_usuario
        supabaseApi.obtenerDatosUsuario("eq." + userId, "non-cache").enqueue(new retrofit2.Callback<List<DatosUsuario>>() {
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
