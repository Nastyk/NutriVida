package com.nutrivda.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.nutrivda.app.test.HistorialResultadoActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nutrivda.app.test.ExportarResultadoActivity;

public class FragmentConfiguracion extends Fragment {

    private LinearLayout itemPerfil, itemExportar, itemLogout;
    private Switch switchModoOscuro, switchNotificaciones;

    public FragmentConfiguracion() {}

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

        itemPerfil = view.findViewById(R.id.itemPerfil);
        itemExportar = view.findViewById(R.id.itemExportar);
        itemLogout = view.findViewById(R.id.itemLogout);
        switchModoOscuro = view.findViewById(R.id.switchModoOscuro);
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones);
        LinearLayout itemHistorial = view.findViewById(R.id.itemHistorial);

        // Yo uso SharedPreferences para guardar ajustes del usuario
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", 0);

        // Recupero si el modo oscuro estaba activado
        boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
        switchModoOscuro.setChecked(modoOscuro);
        aplicarModoOscuro(modoOscuro); // Lo aplico visualmente

        // Recupero si las notificaciones estaban activadas
        boolean notificacionesActivas = prefs.getBoolean("notificaciones", false);
        switchNotificaciones.setChecked(notificacionesActivas);

        // Al hacer clic en "Editar perfil", muestro el fragmento de perfil
        itemPerfil.setOnClickListener(v -> {
            FragmentPerfil fragmentPerfil = new FragmentPerfil();
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentPerfil);
            transaction.addToBackStack(null); // Para que pueda volver atrás
            transaction.commit();
        });

        // Al hacer clic en "Exportar resultados", abro la actividad de exportar
        itemExportar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ExportarResultadoActivity.class);
            startActivity(intent);
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

        // Si el usuario activa o desactiva el modo oscuro, lo aplico y lo guardo
        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            aplicarModoOscuro(isChecked);
            prefs.edit().putBoolean("modo_oscuro", isChecked).apply();
        });

        // Si el usuario activa o desactiva las notificaciones, lo guardo
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notificaciones", isChecked).apply();
            Toast.makeText(requireContext(),
                    isChecked ? "🔔 Notificaciones activadas" : "🔕 Notificaciones desactivadas",
                    Toast.LENGTH_SHORT).show();
        });

        itemHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HistorialResultadoActivity.class);
            startActivity(intent);
        });
    }

    // Esta función cambia el tema entre claro y oscuro
    private void aplicarModoOscuro(boolean activar) {
        AppCompatDelegate.setDefaultNightMode(
                activar ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}