package com.nutrivda.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.database.DatabaseHelper;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.EventDecorator;
import com.nutrivda.app.viewmodel.CompartidoViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCalendario extends Fragment {

    private MaterialCalendarView materialCalendarView;
    private Button btnIrComida;
    private DatabaseHelper dbHelper;
    private String fechaSeleccionadaCalendario;
    private int userId;

    public FragmentCalendario() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        userId = getUserId();

        // Enlazo los elementos del layout con el código
        btnIrComida = view.findViewById(R.id.btnIrComida);
        materialCalendarView = view.findViewById(R.id.calendarView);
        ImageButton btnLogOut = view.findViewById(R.id.btnLogout);

        // Acciones del botón de logout
        btnLogOut.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Marcar los días completados desde Supabase
        marcarDiasEnCalendario();

        // Configurar alarma diaria para reinicio de peso
        configurarAlarmaDiaria();

        // Acceder al registro de comida
        btnIrComida.setOnClickListener(v -> {
            if (fechaSeleccionadaCalendario == null || fechaSeleccionadaCalendario.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaSeleccionadaCalendario = sdf.format(Calendar.getInstance().getTime());
            }

            CompartidoViewModel viewModel = new ViewModelProvider(requireActivity()).get(CompartidoViewModel.class);

            // Enviar la fecha seleccionada
            viewModel.setFechaSeleccionadaString(fechaSeleccionadaCalendario);
            viewModel.setUserId(userId);

            FragmentComida fragmentComida = new FragmentComida();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentComida)
                    .addToBackStack(null)
                    .commit();

            // Actualizar visualmente el item de menú inferior
            ((BaseActivity) requireActivity()).setSelectedNavItem(R.id.nav_dia);
        });

        // Manejo del calendario cuando selecciono un día
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", date.getYear(), (date.getMonth() + 1), date.getDay());
            fechaSeleccionadaCalendario = fechaSeleccionada;
            verificarDiaCompletado(fechaSeleccionada);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        marcarDiasEnCalendario(); // Actualiza los decoradores del calendario
    }

    private void marcarDiasEnCalendario() {
        SupabaseApi supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);
        HashSet<CalendarDay> fechasCompletadas = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        supabaseApi.obtenerDiasCompletados("eq.true", "eq." + userId).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (DiaCompletado dia : response.body()) {
                        try {
                            Date date = sdf.parse(dia.getFecha());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            fechasCompletadas.add(CalendarDay.from(
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    requireActivity().runOnUiThread(() -> {
                        materialCalendarView.removeDecorators();
                        materialCalendarView.addDecorator(new EventDecorator(fechasCompletadas));
                    });
                } else {
                    Toast.makeText(requireContext(), "No hay días completados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error al conectar con Supabase: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarDiaCompletado(String fechaSeleccionada) {
        SupabaseApi supabaseApi = SupabaseClient.getClient().create(SupabaseApi.class);

        /*supabaseApi.obtenerDiaCompletado("eq." + fechaSeleccionada, "eq." + userId).enqueue(new Callback<List<DiaCompletado>>() {
            @Override
            public void onResponse(Call<List<DiaCompletado>> call, Response<List<DiaCompletado>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DiaCompletado dia = response.body().get(0);

                    String desayuno = dia.getDesayuno() != null ? dia.getDesayuno() : "No registrado";
                    String comida = dia.getComida() != null ? dia.getComida() : "No registrado";
                    String cena = dia.getCena() != null ? dia.getCena() : "No registrado";
                    boolean diaIncompleto = !dia.isSwDesayuno() || !dia.isSwComida() || !dia.isSwCena();

                    String checkDesayuno = dia.isSwDesayuno() ? "✅" : "❌";
                    String checkComida = dia.isSwComida() ? "✅" : "❌";
                    String checkCena = dia.isSwCena() ? "✅" : "❌";

                    mostrarDialogoResumen(fechaSeleccionada, desayuno, comida, cena, checkDesayuno, checkComida, checkCena, diaIncompleto);
                } else {
                    Toast.makeText(requireContext(), "Este día no ha sido completado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DiaCompletado>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error al conectar con Supabase: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void mostrarDialogoResumen(String fecha, String desayuno, String comida, String cena,
                                       String checkDesayuno, String checkComida, String checkCena, boolean diaIncompleto) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_resumen_dia, null);

        TextView tvFecha = dialogView.findViewById(R.id.tvFecha);
        TextView tvDesayuno = dialogView.findViewById(R.id.tvDesayuno);
        TextView tvComida = dialogView.findViewById(R.id.tvComida);
        TextView tvCena = dialogView.findViewById(R.id.tvCena);
        TextView tvEstado = dialogView.findViewById(R.id.tvEstado);
        TextView tvCheckDesayuno = dialogView.findViewById(R.id.tvCheckDesayuno);
        TextView tvCheckComida = dialogView.findViewById(R.id.tvCheckComida);
        TextView tvCheckCena = dialogView.findViewById(R.id.tvCheckCena);
        Button btnEditar = dialogView.findViewById(R.id.btnEditar);
        Button btnCerrar = dialogView.findViewById(R.id.btnCerrar);

        tvFecha.setText("📅 Día: " + fecha);
        tvDesayuno.setText("🍽️ Desayuno: " + desayuno);
        tvComida.setText("🍛 Comida: " + comida);
        tvCena.setText("🍲 Cena: " + cena);
        tvCheckDesayuno.setText(checkDesayuno);
        tvCheckComida.setText(checkComida);
        tvCheckCena.setText(checkCena);

        if (diaIncompleto) {
            tvEstado.setVisibility(View.VISIBLE);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnEditar.setOnClickListener(v -> {
            CompartidoViewModel viewModel = new ViewModelProvider(requireActivity()).get(CompartidoViewModel.class);

            // Enviar la fecha seleccionada
            viewModel.setFechaSeleccionadaString(fechaSeleccionadaCalendario);
            viewModel.setUserId(userId);

            FragmentComida fragmentComida = new FragmentComida();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentComida)
                    .addToBackStack(null)
                    .commit();

            dialog.dismiss();
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void configurarAlarmaDiaria() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(requireContext().ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ResetPesoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}
