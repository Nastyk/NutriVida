package com.nutrivda.app.cohereIA;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nutrivda.app.ActividadPerfil;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.inicializacion.OnboardingData;
import com.nutrivda.app.conf.Config;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.pojo.PlanNutricional;
import com.nutrivda.app.viewmodel.SharedViewModelOnboarding;

import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// Este servicio lo uso para construir el prompt, hacer la petición a Cohere y devolver la respuesta
public class CohereService {

    private int userId;
    private final SupabaseApi supabaseApi;
    private final DatosUsuario datosUsuario;

    public interface Callback {
        void onSuccess(PlanNutricional planNutricional);
        void onError(String error);
    }

    public CohereService(SupabaseApi supabaseApi, DatosUsuario datosUsuario) {
        this.supabaseApi = supabaseApi;
        this.datosUsuario = datosUsuario;
    }

    public void generarPlan(SharedViewModelOnboarding viewModelOnboarding, Callback callback) {
        // 1. Construyo el prompt a partir de los datos del onboarding
        String prompt = "Quiero que actúes como un nutricionista profesional. "
                + "El usuario tiene el siguiente perfil:\n"
                + "- Edad: " + datosUsuario.getEdad() + " años\n"
                + "- Peso: " + datosUsuario.getPeso() + " kg\n"
                + "- Altura: " + datosUsuario.getAltura() + " cm\n"
                + "- Nivel de actividad física actual: " + viewModelOnboarding.getActividadFisica().getValue() + "\n"
                + "- Prioridad actual: " + viewModelOnboarding.getObjetivo().getValue() + "\n"
                + "- Restricciones alimentarias: " + String.join(", ", Objects.requireNonNull(viewModelOnboarding.getRestricciones().getValue())) + "\n"
                + "- Organización de comidas: " + viewModelOnboarding.getOrganizacion().getValue() + "\n\n"
                + "Con base en esta información, responde únicamente en formato JSON válido con los siguientes campos:\n\n"
                + "{\n"
                + "  \"imc\": <IMC calculado como número flotante>,\n"
                + "  \"clasificacion_imc\": \"<Clasificación según OMS: Bajo peso, Normal, Sobrepeso, Obesidad>\",\n"
                + "  \"analisis_personalizado\": \"<Análisis claro y profesional que resuma el estado nutricional del usuario en base a su IMC, edad, peso, altura, prioridad, calorías recomendadas y nivel de actividad. Debe incluir consejos concretos para alcanzar su objetivo de forma saludable.>\",\n"
                + "  \"calorias_recomendadas\": <número entero>,\n"
                + "  \"macro_recomendado\": {\n"
                + "    \"proteinas\": <gramos por día>,\n"
                + "    \"grasas\": <gramos por día>,\n"
                + "    \"carbohidratos\": <gramos por día>\n"
                + "  },\n"
                + "  \"nivel_actividad_recomendado\": \"<Uno de los siguientes: ACTIVO, MEDIO, SEDENTARIO>\",\n"
                + "  \"tiempo_estimado_para_lograr_objetivo\": \"<Texto breve>\"\n"
                + "}\n\n"
                + "No incluyas texto adicional. Solo responde con el JSON.";


        // 2. Preparo el JSON para la API de Cohere
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "command-r-plus"); // puedes cambiar por otro modelo
            jsonBody.put("prompt", prompt);
            jsonBody.put("max_tokens", 300);
            jsonBody.put("temperature", 0.7);
        } catch (Exception e) {
            callback.onError("Error al crear JSON: " + e.getMessage());
            return;
        }

        // 3. Creo el cuerpo de la solicitud con la forma correcta para OkHttp moderno
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());

        // 4. Construyo la solicitud HTTP con los headers adecuados
        Request request = new Request.Builder()
                .url("https://api.cohere.ai/v1/generate")
                .addHeader("Authorization", "Bearer " + Config.COHERE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 5. Uso OkHttp para enviar la solicitud de forma asíncrona
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Si falla la conexión, muestro el error en el hilo principal
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error de conexión: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(body);
                        String texto = json.getJSONArray("generations").getJSONObject(0).getString("text").trim();

                        Gson gson = new Gson();
                        PlanNutricional plan = gson.fromJson(texto, PlanNutricional.class);

                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onSuccess(plan));

                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError("Error al interpretar la respuesta"));
                    }
                } else {
                    // Si la API respondió con error, muestro el mensaje de error
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError("Respuesta no exitosa: " + body));
                }
            }
        });
    }



}