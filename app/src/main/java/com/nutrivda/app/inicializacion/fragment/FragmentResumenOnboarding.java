package com.nutrivda.app.inicializacion.fragment;

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

import com.nutrivda.app.R;
import com.nutrivda.app.cohereIA.CohereService;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.inicializacion.activity.OnboardingActivity;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.ResultadoTest;
import com.nutrivda.app.model.pojo.PlanNutricional;
import com.nutrivda.app.test.activity.ResultadoActivity;
import com.nutrivda.app.viewmodel.SharedViewModelOnboarding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


                    recuperarRespuestasTest();

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

                            recuperarRespuestasTest();

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

    private void actualizarRespuestaDeTest() {
        Map<String, Object> respuestasData = new HashMap<>();
        respuestasData.put("id_usuario_fk", userId);
        respuestasData.put("actividad_fisica_respuesta", viewModelOnboarding.getActividadFisica().getValue());
        respuestasData.put("objetivo_respuesta", viewModelOnboarding.getObjetivo().getValue());
        respuestasData.put("restricciones_respuesta", String.join(", ", Objects.requireNonNull(viewModelOnboarding.getRestricciones().getValue())));
        respuestasData.put("organizacion_respuesta", viewModelOnboarding.getOrganizacion().getValue());

        supabaseApi.actualizarResultadosTest("eq." + userId, respuestasData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void recuperarRespuestasTest() {
        supabaseApi.obtenerResultadosTest("eq." + userId, "+").enqueue(new Callback<List<ResultadoTest>>() {
            @Override
            public void onResponse(Call<List<ResultadoTest>> call, Response<List<ResultadoTest>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    ResultadoTest responseTest = response.body().get(0);
                    if (responseTest != null) {
                        actualizarRespuestaDeTest();
                    }
                } else {
                    guardarRespuestasDeTest();
                }
            }

            @Override
            public void onFailure(Call<List<ResultadoTest>> call, Throwable t) {

            }
        });
    }

    private void guardarRespuestasDeTest() {
        Map<String, Object> respuestasData = new HashMap<>();
        respuestasData.put("id_usuario_fk", userId);
        respuestasData.put("actividad_fisica_respuesta", viewModelOnboarding.getActividadFisica().getValue());
        respuestasData.put("objetivo_respuesta", viewModelOnboarding.getObjetivo().getValue());
        respuestasData.put("restricciones_respuesta", String.join(", ", Objects.requireNonNull(viewModelOnboarding.getRestricciones().getValue())));
        respuestasData.put("organizacion_respuesta", viewModelOnboarding.getOrganizacion().getValue());
        supabaseApi.guardarResultadosTest("eq." + userId, respuestasData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
