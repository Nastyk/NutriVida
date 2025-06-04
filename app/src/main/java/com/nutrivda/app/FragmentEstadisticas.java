package com.nutrivda.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.custom.view.DiasRegistradosDelMesView;
import com.nutrivda.app.custom.view.SemicircularGaugeView;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.database.DatabaseHelper;
import com.nutrivda.app.model.DatosUsuario;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.StringUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEstadisticas extends Fragment {

    private TextView tvNombreUsuario, tvAlturaUsuario, tvPesoUsuario;
    private SemicircularGaugeView graficaDiasCompletados, graficaCaloriasConsumidas;
    private DiasRegistradosDelMesView graficoDiasRegistrados;
    private int userId, diasCompletadosDelMes, diasTotalesDelMes, caloriasObjetivo;
    private SupabaseApi supabaseApi;
    private DiaCompletado diaDeHoy;
    private List<String> fechasCompletadasString;
    private ImageView fotoPerfil;

    public FragmentEstadisticas() {
        // Constructor público vacío requerido por Android
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Yo inflo el layout del perfil para este fragmento
        return inflater.inflate(R.layout.fragment_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvAlturaUsuario = view.findViewById(R.id.tvAlturaUsuario);
        tvPesoUsuario = view.findViewById(R.id.tvPesoUsuario);
        graficaDiasCompletados = view.findViewById(R.id.graficaDiasCompletados);
        graficaCaloriasConsumidas = view.findViewById(R.id.graficaCaloriasConsumidas);
        graficoDiasRegistrados = view.findViewById(R.id.graficoDiasRegistrados);
        fotoPerfil = view.findViewById(R.id.fotoPerfil);

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);
        caloriasObjetivo = getCaloriasObjetivo();
        userId = getUserId();

        obtenerCantidadDiasDelMesActual();
        recuperarDiasCompletadosDelMesEnCurso();
        obtenerDatosUsuario();
        recuperarDatosDeHoy();
        recuperarDiasCompletadosDelUsuario();
    }

    private void configurarGraficoDiasCompletados() {

        graficaDiasCompletados.setMaxValue(diasTotalesDelMes);
        graficaDiasCompletados.setValue(diasCompletadosDelMes);
        graficaDiasCompletados.setText("Días registrados");
    }


    private void recuperarDiasCompletadosDelMesEnCurso() {
        String fechaLike = "like." + obtenerMesActualFormato() + "%";

        supabaseApi.obtenerDiasDelMes("eq." + userId, fechaLike, "id").enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<DiaCompletado> diasCompletados = response.body();
                    diasCompletadosDelMes = diasCompletados.size();
                    configurarGraficoDiasCompletados();
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {

            }
        });
    }

    private void recuperarDiasCompletadosDelUsuario() {
        supabaseApi.obtenerDiasCompletadosDeUsuario("eq." + userId, "fecha").enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<DiaCompletado> diasCompletados = response.body();
                    if (diasCompletados!= null && diasCompletados.size() > 0) {
                        fechasCompletadasString = new ArrayList<>();
                        for (DiaCompletado diaCompletado : diasCompletados) {
                            fechasCompletadasString.add(diaCompletado.getFecha());
                        }
                        graficoDiasRegistrados.setFechasCompletadas(fechasCompletadasString);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {

            }
        });
    }

    private void obtenerDatosUsuario() {
        supabaseApi.obtenerDatosUsuario("eq." + userId).enqueue(new Callback<List<DatosUsuario>>() {
            @Override
            public void onResponse(Call<List<DatosUsuario>> call, Response<List<DatosUsuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DatosUsuario datos = response.body().get(0);
                    tvNombreUsuario.setText(datos.getNombre() + " " + datos.getApellido1() + " " +  datos.getApellido2());
                    tvAlturaUsuario.setText("Altura: " + datos.getAltura() + " cm");
                    tvPesoUsuario.setText("Peso " + datos.getPeso() + " kg");
                }
            }

            @Override
            public void onFailure(Call<List<DatosUsuario>> call, Throwable t) {
            }
        });
    }

    private void configurarGraficoCalorias() {
        int caloriasConsumidas = recuperarCaloriasConsumidas();

        graficaCaloriasConsumidas.setMaxValue(caloriasObjetivo);
        graficaCaloriasConsumidas.setValue(caloriasConsumidas);
        graficaCaloriasConsumidas.setText("Calorías consumidas");

    }

    private int recuperarCaloriasConsumidas() {
        int caloriasConsumidas = 0;
        if (diaDeHoy != null) {
            if (diaDeHoy.getCaloria_desayuno() > 0) {
                caloriasConsumidas += diaDeHoy.getCaloria_desayuno();
            }
            if (diaDeHoy.getCaloria_comida() > 0) {
                caloriasConsumidas += diaDeHoy.getCaloria_comida();
            }
            if (diaDeHoy.getCaloria_cena() > 0) {
                caloriasConsumidas += diaDeHoy.getCaloria_cena();
            }
        }
        return caloriasConsumidas;
    }

    private void recuperarDatosDeHoy() {
        String fechaDeHoy = obtenerFechaActualFormato();
        StringBuilder camposSelect = new StringBuilder();
        camposSelect.append("caloria_desayuno").append(",");
        camposSelect.append("caloria_comida").append(",");
        camposSelect.append("caloria_cena");

        supabaseApi.obtenerDiaComida("eq." + userId, "eq." + fechaDeHoy, camposSelect.toString()).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() ) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        diaDeHoy = response.body().get(0);
                    } else {
                        diaDeHoy = null;
                    }
                }
                configurarGraficoCalorias();
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {

            }
        });
    }

    private void obtenerCantidadDiasDelMesActual() {
        Calendar calendar = Calendar.getInstance();
        diasTotalesDelMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private String obtenerMesActualFormato() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Meses comienzan en 0

        return String.format(Locale.getDefault(), "%04d-%02d", year, month);
    }

    private String obtenerFechaActualFormato() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // enero = 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }


    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);
        return prefs.getInt("userId", -1);
    }

    private int getCaloriasObjetivo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);
        return  (int) prefs.getFloat("calorias_objetivo", 0);
    }
}