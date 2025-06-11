package com.nutrivda.app.main.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutrivda.app.main.fragment.FragmentCalendario;
import com.nutrivda.app.main.fragment.FragmentComida;
import com.nutrivda.app.main.fragment.FragmentMasOpciones;
import com.nutrivda.app.main.fragment.FragmentDia;
import com.nutrivda.app.main.fragment.FragmentEstadisticas;
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
        loadFragment(new FragmentEstadisticas());

        bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.parseColor("#096C55")));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment;

            if (itemId == R.id.nav_estadisticas) {
                selectedFragment = new FragmentEstadisticas();
            } else if (itemId == R.id.nav_calendario) {
                selectedFragment = new FragmentCalendario();
            } else if (itemId == R.id.nav_dia) {
                selectedFragment = new FragmentDia();
            } else if (itemId == R.id.nav_comida) {
                selectedFragment = new FragmentComida();
            } else if (itemId == R.id.nav_configuracion) {
                selectedFragment = new FragmentMasOpciones();
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

    public void setSelectedNavItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }
}

