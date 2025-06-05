package com.nutrivda.app.inicializacion;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.nutrivda.app.R;

// En esta actividad lanzo el ViewPager2 que contiene todas las pantallas del onboarding
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Aquí conecto el ViewPager2 con su adapter personalizado
        viewPager = findViewById(R.id.viewPagerOnboarding);
        adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Dejo el swipe horizontal activado por defecto (esto me permite deslizar)
    }

    // Esta función me permitirá avanzar desde cada fragment
    public void avanzarPagina() {
        int paginaActual = viewPager.getCurrentItem();
        if (paginaActual < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(paginaActual + 1);
        }
    }

    // Esta función me permitirá retroceder si fuera necesario (por si añado botón atrás)
    public void retrocederPagina() {
        int paginaActual = viewPager.getCurrentItem();
        if (paginaActual > 0) {
            viewPager.setCurrentItem(paginaActual - 1);
        }
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }
}
