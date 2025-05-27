package com.nutrivda.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.databinding.FragmentDiaBinding;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.StringUtil;
import com.nutrivda.app.viewmodel.CompartidoViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDia extends Fragment {

    private static final int REQUEST_CODE = 1;
    private TextView tvFechaComida, tvTotalKcal;
    private TextView tvDesayunoSeleccionado, tvComidaSeleccionada, tvCenaSeleccionada;
    private TextView tvCaloriasDesayuno, tvCaloriasComida, tvCaloriasCena; // Campos de calorías
    private Button btnGuardarComida, btnAnadirDesayuno, btnAnadirComida, btnAnadirCena;
    private String fechaDeComida;
    private double totalKcal = 0;
    private int userId = 0, caloriasDesayuno = 0, caloriasComida = 0, caloriasCena = 0;
    private ImageButton btnBorrarDesayuno, btnBorrarComida, btnBorrarCena, btnIrAtras, btnIrAdelante;
    private boolean isEditar = false;
    private SupabaseApi supabaseApi;
    private CompartidoViewModel viewModel;
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
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String comidaSeleccionada = data.getStringExtra("recetaSeleccionada");
                            int caloriasComidaSeleccionada = data.getIntExtra("calorias", 0);
                            String tipoComida = data.getStringExtra("tipoComida");

                            añadirComida(tipoComida, comidaSeleccionada, caloriasComidaSeleccionada);
                        }
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
        tvDesayunoSeleccionado = view.findViewById(R.id.tvDesayunoSeleccionado);
        tvComidaSeleccionada = view.findViewById(R.id.tvComidaSeleccionada);
        tvCenaSeleccionada = view.findViewById(R.id.tvCenaSeleccionada);
        tvCaloriasDesayuno = view.findViewById(R.id.tvCaloriasDesayuno);
        tvCaloriasComida = view.findViewById(R.id.tvCaloriasComida);
        tvCaloriasCena = view.findViewById(R.id.tvCaloriasCena);
        tvTotalKcal = view.findViewById(R.id.tvTotalKcal);

        btnAnadirDesayuno = view.findViewById(R.id.btnAnadirDesayuno);
        btnAnadirComida = view.findViewById(R.id.btnAnadirComida);
        btnAnadirCena = view.findViewById(R.id.btnAnadirCena);

        btnIrAtras = view.findViewById(R.id.btnIrAtras);
        btnIrAdelante = view.findViewById(R.id.btnIrAdelante);
        btnBorrarDesayuno = view.findViewById(R.id.btnBorrarDesayuno);
        btnBorrarComida = view.findViewById(R.id.btnBorrarComida);
        btnBorrarCena = view.findViewById(R.id.btnBorrarCena);

        btnGuardarComida = view.findViewById(R.id.btnGuardarComida);

        supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        viewModel = new ViewModelProvider(requireActivity()).get(CompartidoViewModel.class);

        // Observar la fecha recibida
        this.fechaDeComida = viewModel.getFechaSeleccionadaString().getValue();
        this.userId = viewModel.getUserId().getValue()  != null ? viewModel.getUserId().getValue().intValue() : getUserId();



        if (this.fechaDeComida != null && !this.fechaDeComida.equals("")) {
            currentDate = parseFechaString(fechaDeComida);
            //tvFechaComida.setText("Comidas del día: " + fechaDeComida);
            updateDateText(fechaDeComida);
        } else {
            currentDate = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaDeComida = sdf.format(currentDate.getTime());
        }

        // Yo configuro la fecha actual por defectos
        //tvFechaComida.setText("Comidas del día: " +  viewModel.getFechaSeleccionadaString().getValue());

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

        btnAnadirDesayuno.setOnClickListener(v -> abrirAniadirComida("Desayuno"));
        btnAnadirComida.setOnClickListener(v -> abrirAniadirComida("Comida"));
        btnAnadirCena.setOnClickListener(v -> abrirAniadirComida("Cena"));

        configurarBotonBorrar(btnBorrarDesayuno, tvDesayunoSeleccionado, tvCaloriasDesayuno, btnAnadirDesayuno, "Desayuno");
        configurarBotonBorrar(btnBorrarComida, tvComidaSeleccionada, tvCaloriasComida, btnAnadirComida, "Comida");
        configurarBotonBorrar(btnBorrarCena, tvCenaSeleccionada, tvCaloriasCena, btnAnadirCena, "Cena");

        // Guardar selección de comidas
        btnGuardarComida.setOnClickListener(v -> guardarComidas());
    }

    private void añadirComida(String tipoComida, String nombre, int kcal) {
        if (binding == null || getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View alimentoView = null;

        if (tipoComida.equals("Desayuno")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosDesayuno, false);
        } else if (tipoComida.equals("Comida")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosComida, false);
        } else if (tipoComida.equals("Cena")) {
            alimentoView = inflater.inflate(R.layout.item_alimento, binding.layoutAlimentosCena, false);
        }

        TextView tvNombre = alimentoView.findViewById(R.id.tvNombreAlimento);
        TextView tvKcal = alimentoView.findViewById(R.id.tvKcalAlimento);

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
                }
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
    }

    /**
     *
     * @param tipoComida
     *
     * Metodo que inicia la actividad AniadirComida donde se pasa el dato REQUEST_CODE
     * que permite identificar la actividad origen en el onActivityResult
     */
    private void abrirAniadirComida(String tipoComida) {
        Intent intent = new Intent(getActivity(), AniadirComidaActivity.class);
        intent.putExtra("tipo_comida", tipoComida);
        aniadirComidaLauncher.launch(intent);
    }

    private void guardarComidas() {

        String desayunoSeleccionado = StringUtil.isCadenaVacia(tvDesayunoSeleccionado.getText().toString()) ? null : tvDesayunoSeleccionado.getText().toString();
        String comidaSeleccionada = StringUtil.isCadenaVacia(tvComidaSeleccionada.getText().toString()) ? null : tvComidaSeleccionada.getText().toString();
        String cenaSeleccionada = StringUtil.isCadenaVacia(tvCenaSeleccionada.getText().toString()) ? null : tvCenaSeleccionada.getText().toString();

        caloriasDesayuno = StringUtil.isCadenaVacia(tvDesayunoSeleccionado.getText().toString()) ? 0 : caloriasDesayuno;
        caloriasComida = StringUtil.isCadenaVacia(tvComidaSeleccionada.getText().toString()) ? 0 : caloriasComida;
        caloriasCena = StringUtil.isCadenaVacia(tvCenaSeleccionada.getText().toString()) ? 0 : caloriasCena;

        boolean diaIncompleto = false;

        if (diaIncompleto) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("⚠️ Día Incompleto")
                    .setMessage("Has registrado comidas, pero el día no será marcado como completo en el calendario. ¿Quieres continuar?")
                    .setPositiveButton("Guardar", (dialog, which) -> guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, false, caloriasDesayuno, caloriasComida, caloriasCena))
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        guardarDatosComida(desayunoSeleccionado, comidaSeleccionada, cenaSeleccionada, true, caloriasDesayuno, caloriasComida, caloriasCena);
    }

    private void guardarDatosComida(String desayuno, String comida, String cena, boolean marcarComoCompleto, int caloriasDesayuno, int caloriasComida, int caloriasCena) {
        // Crear objeto JSON con los datos correctos para la tabla dias_completados
        Map<String, Object> comidaData = new HashMap<>();
        comidaData.put("completado", marcarComoCompleto);
        comidaData.put("desayuno", comprobarCampo(desayuno));
        comidaData.put("comida", comprobarCampo(comida));
        comidaData.put("cena", comprobarCampo(cena));
        comidaData.put("caloria_desayuno", caloriasDesayuno);
        comidaData.put("caloria_comida", caloriasComida);
        comidaData.put("caloria_cena", caloriasCena);

        if (isEditar) {
            // Si está en modo edición, hacemos un PATCH en lugar de POST
            supabaseApi.actualizarComida("eq." + userId, "eq." + fechaDeComida, comidaData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "✅ Comida actualizada correctamente en Supabase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "❌ Error al actualizar en Supabase: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("NutriVida","ERROR en actualizarComida -> " + t.getMessage());
                }
            });
        } else {
            comidaData.put("id_usuario_fk", userId);
            comidaData.put("fecha", fechaDeComida);
            supabaseApi.insertarComida(comidaData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "✅ Comida guardada correctamente en Supabase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "❌ Error al guardar en Supabase", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "❌ Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("NutriVida","ERROR en actualizarComida -> " + t.getMessage());
                }
            });
        }

    }

    private String comprobarCampo(String campo) {
        return campo == null ? "Sin datos" : campo;
    }

    private int getCalorias(TextView tv) {
        try {
            return Integer.parseInt(tv.getText().toString().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void cargarDatosDelDia() {
        supabaseApi.obtenerDiaCompletado("eq." + fechaDeComida, "eq." + userId).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Tomar el primer resultado, ya que la fecha debería ser única
                    DiaCompletado dia = response.body().get(0);

                    String desayunoSeleciconado = dia.getDesayuno() != null ? dia.getDesayuno() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvDesayunoSeleccionado, btnBorrarDesayuno, tvCaloriasDesayuno, btnAnadirDesayuno, desayunoSeleciconado, dia.getCaloria_desayuno(), "Desayuno");

                    String comidaSeleccionada = dia.getComida() != null ? dia.getComida() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvComidaSeleccionada, btnBorrarComida, tvCaloriasComida, btnAnadirComida, comidaSeleccionada, dia.getCaloria_comida(), "Comida");

                    String cenaSeleccionada = dia.getCena() != null ? dia.getCena() : "Ninguno";
                    actualizarVistaComidaEnEdicion(tvCenaSeleccionada, btnBorrarCena, tvCaloriasCena, btnAnadirCena, cenaSeleccionada, dia.getCaloria_cena(), "Cena");

                    actualizarTotalKcal();
                } else {
                    String desayunoSeleciconado = "Ninguno";
                    actualizarVistaComidaEnEdicion(tvDesayunoSeleccionado, btnBorrarDesayuno, tvCaloriasDesayuno, btnAnadirDesayuno, desayunoSeleciconado, 0, "Desayuno");

                    String comidaSeleccionada = "Ninguno";
                    actualizarVistaComidaEnEdicion(tvComidaSeleccionada, btnBorrarComida, tvCaloriasComida, btnAnadirComida, comidaSeleccionada, 0, "Comida");

                    String cenaSeleccionada = "Ninguno";
                    actualizarVistaComidaEnEdicion(tvCenaSeleccionada, btnBorrarCena, tvCaloriasCena, btnAnadirCena, cenaSeleccionada, 0, "Cena");

                    actualizarTotalKcal();
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error al conectar con Supabase: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param textViewComida
     * @param botonBorrar
     * @param textViewCalorias
     * @param botonAnadir
     * @param comida
     * @param calorias
     * @param tipoComida
     *
     * Actualiza la vista de las comidas conforme los argumentos pasados en el modo de edicion
     */
    private void actualizarVistaComidaEnEdicion(TextView textViewComida, ImageButton botonBorrar, TextView textViewCalorias, Button botonAnadir, String comida, int calorias, String tipoComida) {
        /*textViewComida.setText(comida);
        textViewCalorias.setText("Calorías: " + calorias);

        switch (tipoComida) {
            case "Desayuno":
                caloriasDesayuno = calorias;
                break;
            case "Comida":
                caloriasComida = calorias;
                break;
            case "Cena":
                caloriasCena = calorias;
                break;
            default:
                Log.e("NutriVida","ERROR en actualizarVistaComida -> tipoComida en switch erroneo");
        }

        if(comida.equals("Ninguno")) {
            // Ocultar el botón de borrar
            Log.i("NutriVida", "INFO: ENTRA EN IF********************** CON COMIDA VALOR  :" + comida);
            botonBorrar.setVisibility(View.GONE);
            botonAnadir.setVisibility(View.VISIBLE);
        } else {
            // Mostrar botón de borrar y ocultar el de añadir
            botonBorrar.setVisibility(View.VISIBLE);
            botonAnadir.setVisibility(View.GONE);
        }*/
    }

    /**
     *
     * @param botonBorrar
     * @param textViewComida
     * @param textViewCalorias
     * @param botonAnadir
     * @param tipoComida
     *
     * Configuracion del boton borrar una vez añadida la comida
     */
    private void configurarBotonBorrar(ImageButton botonBorrar, TextView textViewComida, TextView textViewCalorias, Button botonAnadir, String tipoComida) {
        /*botonBorrar.setOnClickListener(v -> {
            // Borrar la comida seleccionada
            textViewComida.setText("Ninguno");
            textViewCalorias.setText("Calorías: 0");

            switch (tipoComida) {
                case "Desayuno":
                    caloriasDesayuno = 0;
                    break;
                case "Comida":
                    caloriasComida = 0;
                    break;
                case "Cena":
                    caloriasCena = 0;
                    break;
                default:
                    Log.e("NutriVida","ERROR en configurarBotonBorrar -> tipoComida en switch erroneo");
            }

            // Ocultar el botón de borrar
            botonBorrar.setVisibility(View.GONE);
            botonAnadir.setVisibility(View.VISIBLE);

            // Actualizar total de calorías
            actualizarTotalKcal();
        });*/
    }

    private void tratarRespuestaDeActividad(String comidaSeleccionada, String tipoComida, int caloriasComidaSeleccionada) {

        if (tipoComida != null && comidaSeleccionada != null) {
            switch (tipoComida.toLowerCase()) {
                case "desayuno":
                    actualizarVistaComida(tvDesayunoSeleccionado, btnBorrarDesayuno, tvCaloriasDesayuno, btnAnadirDesayuno, comidaSeleccionada, caloriasComidaSeleccionada, "Desayuno");
                    break;
                case "comida":
                    actualizarVistaComida(tvComidaSeleccionada, btnBorrarComida, tvCaloriasComida, btnAnadirComida, comidaSeleccionada, caloriasComidaSeleccionada, "Comida");
                    break;
                case "cena":
                    actualizarVistaComida(tvCenaSeleccionada, btnBorrarCena, tvCaloriasCena, btnAnadirCena, comidaSeleccionada, caloriasComidaSeleccionada, "Cena");
                    break;
            }
            actualizarTotalKcal();
        }
    }

    /**
     *
     * @param textViewComida
     * @param botonBorrar
     * @param textViewCalorias
     * @param botonAnadir
     * @param comida
     * @param calorias
     * @param tipoComida
     *
     * actualiza la vista de las comidas conforme a los argumentos pasados en el alta
     */
    private void actualizarVistaComida(TextView textViewComida, ImageButton botonBorrar, TextView textViewCalorias, Button botonAnadir, String comida, int calorias, String tipoComida) {
        textViewComida.setText(comida);
        textViewCalorias.setText("Calorías: " + calorias);

        switch (tipoComida) {
            case "Desayuno":
                caloriasDesayuno = calorias;
                break;
            case "Comida":
                caloriasComida = calorias;
                break;
            case "Cena":
                caloriasCena = calorias;
                break;
            default:
                Log.e("NutriVida","ERROR en actualizarVistaComida -> tipoComida en switch erroneo");
        }

        // Mostrar botón de borrar y ocultar el de añadir
        botonBorrar.setVisibility(View.VISIBLE);
        botonAnadir.setVisibility(View.GONE);
    }

    private void actualizarTotalKcal() {
        totalKcal = caloriasDesayuno + caloriasComida + caloriasCena;
        tvTotalKcal.setText("Total kcal: " + totalKcal);
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

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}

