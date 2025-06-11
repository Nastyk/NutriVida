package com.nutrivda.app.main.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nutrivda.app.R;
import com.nutrivda.app.ResetPesoReceiver;
import com.nutrivda.app.main.activity.BaseActivity;
import com.nutrivda.app.conf.SupabaseClient;
import com.nutrivda.app.data.SupabaseApi;
import com.nutrivda.app.model.DiaCompletado;
import com.nutrivda.app.utils.EventDecorator;
import com.nutrivda.app.viewmodel.SharedViewModelCalendarioDia;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

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
    private String fechaSeleccionadaCalendario;
    private int userId;
    private TextView txtMonthTitle;
    private ImageView btnPrevMonth, btnNextMonth;

    public FragmentCalendario() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = getUserId();

        // Enlazo los elementos del layout con el código
        btnIrComida = view.findViewById(R.id.btnIrComida);
        materialCalendarView = view.findViewById(R.id.calendarView);
        txtMonthTitle = view.findViewById(R.id.txtMonthTitle);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);

        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return "";
            }
        });

        // Marcar los días completados desde Supabase
        marcarDiasEnCalendario();

        // Configurar alarma diaria para reinicio de peso
        configurarAlarmaDiaria();

        // Actualizar título con el mes actual
        updateMonthTitle(materialCalendarView.getCurrentDate(), txtMonthTitle);

        btnPrevMonth.setOnClickListener(v -> {
            materialCalendarView.goToPrevious();
            updateMonthTitle(materialCalendarView.getCurrentDate(), txtMonthTitle);
        });

        btnNextMonth.setOnClickListener(v -> {
            materialCalendarView.goToNext();
            updateMonthTitle(materialCalendarView.getCurrentDate(), txtMonthTitle);
        });

        // Si el usuario cambia el mes con swipe, actualiza el texto también
        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            updateMonthTitle(date, txtMonthTitle);
        });

        // Acceder al registro de comida
        btnIrComida.setOnClickListener(v -> {
            if (fechaSeleccionadaCalendario == null || fechaSeleccionadaCalendario.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaSeleccionadaCalendario = sdf.format(Calendar.getInstance().getTime());
            }

            SharedViewModelCalendarioDia viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModelCalendarioDia.class);

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

    // Método para formatear el texto del mes
    private void updateMonthTitle(CalendarDay date, TextView textView) {
        Locale locale = new Locale("es", "ES"); // Español
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", locale);
        String mes = sdf.format(date.getDate());
        mes = mes.substring(0, 1).toUpperCase(locale) + mes.substring(1); // Capitalizar
        textView.setText(mes);
    }

    private int getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}
