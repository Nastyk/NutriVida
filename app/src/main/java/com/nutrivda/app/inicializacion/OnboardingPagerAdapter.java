package com.nutrivda.app.inicializacion;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Este adapter lo uso para decir qué fragment va en cada página del onboarding
public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Según la posición devuelvo el fragmento correcto
        switch (position) {
            case 0: return new FragmentActividadFisica();
            case 1: return new FragmentPrioridad();
            case 2: return new FragmentRestricciones();
            case 3: return new FragmentOrganizacionComidas();
            case 4: return new FragmentResumenOnboarding();
            default: return new FragmentActividadFisica(); // por defecto
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < getItemCount();
    }

    @Override
    public int getItemCount() {
        return 5; // Tengo 5 pantallas/fragments en total
    }
}
