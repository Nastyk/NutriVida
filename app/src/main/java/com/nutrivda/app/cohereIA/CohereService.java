package com.nutrivda.app.cohereIA;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.widget.Toast;

import com.nutrivda.app.inicializacion.OnboardingData;
import com.nutrivda.app.conf.Config;

import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// Este servicio lo uso para construir el prompt, hacer la petición a Cohere y devolver la respuesta
public class CohereService {

    public interface Callback {
        void onSuccess(String respuestaIA);
        void onError(String error);
    }

    public void generarPlan(OnboardingData data, Callback callback) {
        // 1. Construyo el prompt a partir de los datos del onboarding
        String prompt = "Quiero que actúes como un nutricionista profesional. "
                + "El usuario tiene el siguiente perfil:\n"
                + "- Nivel de actividad física: " + data.getActividadFisica() + "\n"
                + "- Prioridad actual: " + data.getPrioridad() + "\n"
                + "- Restricciones alimentarias: " + String.join(", ", data.getRestricciones()) + "\n"
                + "- Organización de comidas: " + data.getOrganizacionComidas() + "\n\n"
                + "Basado en esta información, genera un plan nutricional personalizado con recomendaciones claras, motivadoras y prácticas. Usa un tono amigable y profesional.";

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
                        // Extraigo el texto generado por Cohere desde la respuesta JSON
                        JSONObject json = new JSONObject(body);
                        String texto = json.getJSONArray("generations").getJSONObject(0).getString("text");

                        // Devuelvo la respuesta limpia al hilo principal
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onSuccess(texto.trim()));

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