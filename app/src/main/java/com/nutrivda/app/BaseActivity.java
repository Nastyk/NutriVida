package com.nutrivda.app;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutrivda.app.FragmentComida;
import com.nutrivda.app.FragmentConfiguracion;
import com.nutrivda.app.FragmentDia;
import com.nutrivda.app.FragmentPerfil;
import com.nutrivda.app.FragmentCalendario;
import com.nutrivda.app.R;

public class BaseActivity extends AppCompatActivity {

    // Este será el BottomNavigationView que siempre estará visible abajo
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Enlazo con el layout que hicimos antes

        // Enlazo el BottomNavigationView con su ID del layout
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Cargo el fragment por defecto (puedes cambiarlo si quieres empezar con otro)
        loadFragment(new FragmentCalendario());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment;

            if (itemId == R.id.nav_perfil) {
                selectedFragment = new FragmentPerfil();
            } else if (itemId == R.id.nav_calendario) {
                selectedFragment = new FragmentCalendario();
            } else if (itemId == R.id.nav_dia) {
                selectedFragment = new FragmentDia();
            } else if (itemId == R.id.nav_comida) {
                selectedFragment = new FragmentComida();
            } else if (itemId == R.id.nav_configuracion) {
                selectedFragment = new FragmentConfiguracion();
            } else {
                return false;
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    // Esta función carga el fragment deseado dentro del contenedor
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}

