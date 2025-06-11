package com.nutrivda.app.main.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.Manifest;

import com.nutrivda.app.R;
import com.nutrivda.app.ResetPesoReceiver;
import com.nutrivda.app.activity.LoginActivity;
import com.nutrivda.app.inicializacion.activity.OnboardingActivity;
import com.nutrivda.app.test.activity.ExportarResultadoActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentMasOpciones extends Fragment {

    private LinearLayout itemTestNutricional, itemLogout;
    private Switch switchNotificaciones;

    public FragmentMasOpciones() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Yo cargo el layout XML del fragmento
        return inflater.inflate(R.layout.fragment_configuracion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemTestNutricional = view.findViewById(R.id.itemTestNutricional);
        itemLogout = view.findViewById(R.id.itemLogout);
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones);

        // Yo uso SharedPreferences para guardar ajustes del usuario
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);

        // Recupero si las notificaciones estaban activadas
        boolean notificacionesActivas = prefs.getBoolean("notificaciones", false);
        switchNotificaciones.setChecked(notificacionesActivas);

        //Exportar el historial guardado
        LinearLayout itemExportar = view.findViewById(R.id.exportarTest);

        itemExportar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ExportarResultadoActivity.class);
            startActivity(intent);
        });

        // Al hacer clic en "Lanazo la actividad Onboarding, que tiene los tests"
        itemTestNutricional.setOnClickListener(v -> {
            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_test);

            AlertDialog dialog = new AlertDialog.Builder(requireActivity(), R.style.DialogSlideFadeAnimation)
                    .setIcon(icon)
                    .setTitle("¿Reiniciar tests nutricionales?")
                    .setMessage("Esto restablecerá tus calorías objetivo así como tus estadísticas actuales.")
                    .setPositiveButton("Continuar", null)
                    .setNegativeButton("Cancelar", null)
                    .create();

            dialog.setOnShowListener(dlg -> {
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positive.setTextColor(ContextCompat.getColor(requireContext(), R.color.verde));
                negative.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));

                positive.setOnClickListener(btn -> {
                    Intent intent = new Intent(requireContext(), OnboardingActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                });

                negative.setOnClickListener(btn -> dialog.dismiss());
            });

            dialog.show();
        });


        // Al hacer clic en "Cerrar sesión", borro preferencias y regreso al login
        itemLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Elimino todos los datos guardados (como isLoggedIn y userId)
            editor.apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish(); // Cierro la actividad para que no pueda volver atrás
        });

        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Context context = getContext();
            if (context == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1001
                    );
                    switchNotificaciones.setChecked(false); // desactivar hasta que se acepte
                    return;
                }
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ResetPesoReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    Intent permisoIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    context.startActivity(permisoIntent);
                    return;
                }

                long triggerTime = System.currentTimeMillis() + 5000;
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Toast.makeText(context, "Notificación programada en 5 segundos", Toast.LENGTH_SHORT).show();
            } else {
                alarmManager.cancel(pendingIntent);
                Toast.makeText(context, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permiso denegado. No se enviarán notificaciones.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}