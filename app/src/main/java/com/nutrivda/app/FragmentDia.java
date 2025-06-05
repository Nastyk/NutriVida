package com.nutrivda.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.databinding.FragmentDiaBinding;
import com.nutrivda.app.model.Comida;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.Utilidades;
import com.nutrivda.app.viewmodel.SharedViewModelCalendarioDia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDia extends Fragment {

    private static final int REQUEST_CODE = 1;
    private TextView tvFechaComida, tvTotalKcal;
    private TextView tvCaloriasDesayuno, tvCaloriasComida, tvCaloriasCena;
    private Button btnAnadirDesayuno, btnAnadirComida, btnAnadirCena;
    private String fechaDeComida;
    private int totalKcal = 0;
    private float caloriasObjetivo = 0;
    private int userId = 0, caloriasDesayuno = 0, caloriasComida = 0, caloriasCena = 0;
    private ImageButton btnIrAtras, btnIrAdelante;
    private List<Long> desayunoList, comidaList, cenaList;
    private boolean isEditar = false;
    private SupabaseApi supabaseApi;
    private SharedViewModelCalendarioDia viewModel;
    private ActivityResultLauncher<Intent> aniadirComidaLauncher;
    private Calendar currentDate;
    private FragmentDiaBinding binding;

    public FragmentDia() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 💡 Aquí se instancia el launcher
        aniadirComidaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        limpiarComidasDelDia();
                        cargarDatosDelDia();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Yo conecto todos los elementos visuales
        tvFechaComida = view.findViewById(R.id.tvFechaComida);
        tvCaloriasDesayuno = view.findViewById(R.id.tvKcalDesayunoTotal);
        tvCaloriasComida = view.findViewById(R.id.tvKcalComidaTotal);
        tvCaloriasCena = view.findViewById(R.id.tvKcalCenaTotal);
        tvTotalKcal = view.findViewById(R.id.tvTotalKcal);

        btnAnadirDesayuno = view.findViewById(R.id.btnAnadirDesayuno);
        btnAnadirComida = view.findViewById(R.id.btnAnadirComida);
        btnAnadirCena = view.findViewById(R.id.btnAnadirCena);

        btnIrAtras = view.findViewById(R.id.btnIrAtras);
        btnIrAdelante = view.findViewById(R.id.btnIrAdelante);

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModelCalendarioDia.class);

        // Observar la fecha recibida
        this.fechaDeComida = viewModel.getFechaSeleccionadaString().getValue();
        this.userId = viewModel.getUserId().getValue()  != null ? viewModel.getUserId().getValue().intValue() : getUserId();
        this.caloriasObjetivo = getCaloriasObjetivo();


        if (this.fechaDeComida != null && !this.fechaDeComida.equals("")) {
            currentDate = parseFechaString(fechaDeComida);
            //tvFechaComida.setText("Comidas del día: " + fechaDeComida);
            updateDateText(fechaDeComida);
        } else {
            currentDate = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(currentDate.getTime());
        }

        cargarDatosDelDia();
        updateDateText("");

        // Botón para ir al día anterior
        btnIrAtras.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, -1); // Resta un día
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(currentDate.getTime());
            cargarDatosDelDia();
            updateDateText("");
        });

        // Botón para ir al día siguiente
        btnIrAdelante.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, 1); // Suma un día
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(currentDate.getTime());
            cargarDatosDelDia();
            updateDateText("");
        });

        btnAnadirDesayuno.setOnClickListener(v -> abrirAniadirComida("Desayuno", fechaDeComida));
        btnAnadirComida.setOnClickListener(v -> abrirAniadirComida("Comida", fechaDeComida));
        btnAnadirCena.setOnClickListener(v -> abrirAniadirComida("Cena", fechaDeComida));
    }

    private void añadirComida(String tipoComida, String nombre, int kcal, long idComida) {
        if (binding == null || getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View alimentoView = null;

        if (tipoComida.equals("Desayuno")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosDesayuno, false);
            caloriasDesayuno += kcal;
        } else if (tipoComida.equals("Comida")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosComida, false);
            caloriasComida += kcal;
        } else if (tipoComida.equals("Cena")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosCena, false);
            caloriasCena += kcal;
        }

        TextView tvNombre = alimentoView.findViewById(R.id.tvNombreAlimento);
        TextView tvKcal = alimentoView.findViewById(R.id.tvKcalAlimento);
        alimentoView.setTag(R.id.tag_id_comida, idComida);
        alimentoView.setTag(R.id.tag_kcal_comida, kcal);

        tvNombre.setText(nombre);
        tvKcal.setText(kcal + " kcal");

        alimentoView.setOnLongClickListener(view -> {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenu().add("Eliminar");
            popup.setOnMenuItemClickListener(item -> {
                if ("Eliminar".contentEquals(item.getTitle())) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    if (parent != null) {
                        parent.removeView(view);
                    }

                    // Recuperar datos del tag
                    Long idComidaTag = (Long) view.getTag(R.id.tag_id_comida);
                    int kcalTag = (int) view.getTag(R.id.tag_kcal_comida);

                    // Restar kcal
                    switch (tipoComida.toLowerCase()) {
                        case "desayuno": caloriasDesayuno -= kcalTag; break;
                        case "comida": caloriasComida -= kcalTag; break;
                        case "cena": caloriasCena -= kcalTag; break;
                    }

                    actualizarTotalKcal();

                    List<Long> listaActual = obtenerListaTipo(tipoComida);
                    listaActual.remove(idComidaTag);

                    int nuevasKcal = kcalTipo(tipoComida);
                    Map<String, Object> body = Utilidades.prepararCuerpoActualizado(tipoComida, listaActual, nuevasKcal);

                    supabaseApi.eliminarComidaDeDia("eq." + userId, "eq." + fechaDeComida, body).enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Comida eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Comida no eliminada", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error Supabase", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                comprobarDia();

                return false;
            });
            popup.show();
            return true;
        });


        if (tipoComida.equals("Desayuno")) {
            binding.layoutAlimentosDesayuno.addView(alimentoView);;
        } else if (tipoComida.equals("Comida")) {
            binding.layoutAlimentosComida.addView(alimentoView);
        } else if (tipoComida.equals("Cena")) {
            binding.layoutAlimentosCena.addView(alimentoView);
        }

        actualizarTotalKcal();
    }


    private void comprobarDia() {

        StringBuilder camposSelect = new StringBuilder();
        camposSelect.append("desayuno");
        camposSelect.append(",");
        camposSelect.append("comida");
        camposSelect.append(",");
        camposSelect.append("cena");
        supabaseApi.obtenerDiaComida("eq." + userId, "eq." + fechaDeComida, camposSelect.toString()).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DiaCompletado diaCompletado = response.body().get(0);
                    List<Long> desayuno = diaCompletado.getDesayuno();
                    List<Long> comida = diaCompletado.getComida();
                    List<Long> cena = diaCompletado.getCena();

                    boolean sinComidas = (desayuno == null || desayuno.isEmpty()) && (comida == null || comida.isEmpty()) && (cena == null || cena.isEmpty());

                    if(sinComidas) {
                        supabaseApi.eliminarDiaComida("eq." + userId, "eq." + fechaDeComida).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Dia eliminado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(getContext(), "❌ Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param tipoComida
     *
     * Metodo que inicia la actividad AniadirComida donde se pasa el dato REQUEST_CODE
     * que permite identificar la actividad origen en el onActivityResult
     */
    private void abrirAniadirComida(String tipoComida, String fechaDeComida) {
        Intent intent = new Intent(getActivity(), AniadirComidaActivity.class);
        intent.putExtra("tipo_comida", tipoComida);
        intent.putExtra("fecha_comida", fechaDeComida);
        intent.putExtra("user_id", userId);
        aniadirComidaLauncher.launch(intent);
    }

    private void cargarDatosDelDia() {
        limpiarComidasDelDia();

        supabaseApi.obtenerDiaCompletado("eq." + fechaDeComida, "eq." + userId).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Tomar el primer resultado, ya que la fecha debería ser única
                    DiaCompletado dia = response.body().get(0);

                    if (dia.getDesayuno() != null && !dia.getDesayuno().isEmpty()) {
                        desayunoList = dia.getDesayuno();
                        for (Long idAlimento : dia.getDesayuno()) {
                            supabaseApi.obtenerComidaPorId("eq." + idAlimento, "eq." + userId, "*").enqueue(new Callback<List<Comida>>() {
                                @Override
                                public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                        Comida comida = response.body().get(0);
                                        añadirComida("Desayuno", comida.getDescComida(), comida.getCalorias(), comida.getId());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Comida>> call, Throwable t) {
                                }
                            });
                        }
                    }

                    if (dia.getComida() != null && !dia.getComida().isEmpty()) {
                        comidaList = dia.getComida();
                        for (Long idAlimento : dia.getComida()) {
                            supabaseApi.obtenerComidaPorId("eq." + idAlimento, "eq." + userId, "*").enqueue(new Callback<List<Comida>>() {
                                @Override
                                public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                        Comida comida = response.body().get(0);
                                        añadirComida("Comida", comida.getDescComida(), comida.getCalorias(), comida.getId());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Comida>> call, Throwable t) {
                                }
                            });
                        }
                    }

                    if (dia.getCena() != null && !dia.getCena().isEmpty()) {
                        cenaList = dia.getCena();
                        for (Long idAlimento : dia.getCena()) {
                            supabaseApi.obtenerComidaPorId("eq." + idAlimento, "eq." + userId, "*").enqueue(new Callback<List<Comida>>() {
                                @Override
                                public void onResponse(Call<List<Comida>> call, Response<List<Comida>> response) {
                                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                        Comida comida = response.body().get(0);
                                        añadirComida("Cena", comida.getDescComida(), comida.getCalorias(), comida.getId());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Comida>> call, Throwable t) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error al conectar con Supabase: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarTotalKcal() {
        tvCaloriasDesayuno.setText(caloriasDesayuno + " kcal");
        tvCaloriasComida.setText(caloriasComida + " kcal");
        tvCaloriasCena.setText(caloriasCena + " kcal");

        totalKcal = caloriasDesayuno + caloriasComida + caloriasCena;
        tvTotalKcal.setText("Has consumido " + totalKcal + " de " + (int)caloriasObjetivo);
    }

    private void updateDateText(String fechaStr) {
        Calendar today = Calendar.getInstance();
        setToMidnight(today);

        Calendar selected;
        if (fechaStr == null || fechaStr.equals("")) {
            selected = (Calendar) currentDate.clone();;
            setToMidnight(selected);
        } else {
            selected = parseFechaString(fechaStr);
            setToMidnight(selected);
        }


        if (selected.equals(today)) {
            tvFechaComida.setText("Hoy");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
            String formattedDate = dateFormat.format(selected.getTime());
            tvFechaComida.setText(capitalizeFirst(formattedDate));
        }

    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private Calendar parseFechaString(String fechaStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = sdf.parse(fechaStr);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    private List<Long> obtenerListaTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "desayuno": return desayunoList;
            case "comida": return comidaList;
            case "cena": return cenaList;
            default: return new ArrayList<>();
        }
    }

    private int kcalTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "desayuno": return caloriasDesayuno;
            case "comida": return caloriasComida;
            case "cena": return caloriasCena;
            default: return 0;
        }
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }

    private float getCaloriasObjetivo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getFloat("calorias_objetivo", 0);
    }

    private void limpiarComidasDelDia() {
        if (desayunoList != null && !desayunoList.isEmpty()) {
            desayunoList.clear();
        }
        if (comidaList != null && !comidaList.isEmpty()) {
            comidaList.clear();
        }
        if (cenaList != null && !cenaList.isEmpty()) {
            cenaList.clear();
        }


        if (binding == null) return;

        binding.layoutAlimentosDesayuno.removeAllViews();
        binding.layoutAlimentosComida.removeAllViews();
        binding.layoutAlimentosCena.removeAllViews();

        caloriasDesayuno = 0;
        caloriasComida = 0;
        caloriasCena = 0;

        actualizarTotalKcal();
    }
}

