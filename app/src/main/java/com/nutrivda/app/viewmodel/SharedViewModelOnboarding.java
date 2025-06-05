package com.nutrivda.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModelOnboarding extends ViewModel {

    private final MutableLiveData<String> objetivo = new MutableLiveData<>();
    private final MutableLiveData<String> actividadFisica = new MutableLiveData<>();
    private final MutableLiveData<List<String>> restricciones = new MutableLiveData<>();
    private final MutableLiveData<String> organizacion = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuestionarioFisicoCompletado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuestionarioObjetivoCompletado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuestionarioRestriccionesCompletado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuestionarioOrganizacionCompletado = new MutableLiveData<>();

    public LiveData<String> getObjetivo() { return objetivo; }
    public void setObjetivo(String val) { objetivo.setValue(val); }

    public LiveData<String> getActividadFisica() { return actividadFisica; }
    public void setActividadFisica(String val) { actividadFisica.setValue(val); }

    public LiveData<List<String>> getRestricciones() { return restricciones; }
    public void setRestricciones(List<String> val) { restricciones.setValue(val); }

    public LiveData<String> getOrganizacion() { return organizacion; }
    public void setOrganizacion(String val) { organizacion.setValue(val); }

    public LiveData<Boolean> cuestionarioFisicoCompletado() { return cuestionarioFisicoCompletado; }
    public void setCuestionarioFisicoCompletado(boolean swCuestionarioFisico) { cuestionarioFisicoCompletado.setValue(swCuestionarioFisico); }

    public LiveData<Boolean> cuestionarioObjetivoCompletado() { return cuestionarioObjetivoCompletado; }
    public void setCuestionarioObjetivoCompletado(boolean swCuestionarioObjetivo) { cuestionarioObjetivoCompletado.setValue(swCuestionarioObjetivo); }

    public LiveData<Boolean> cuestionarioRestriccionesCompletado() { return cuestionarioRestriccionesCompletado; }
    public void setCuestionarioRestriccionesCompletado(boolean swCuestionarioRestricciones) { cuestionarioRestriccionesCompletado.setValue(swCuestionarioRestricciones); }

    public LiveData<Boolean> cuestionarioOrganizacionCompletado() { return cuestionarioOrganizacionCompletado; }
    public void setCuestionarioOrganizacionCompletado(boolean swCuestionarioOrganizacion) { cuestionarioOrganizacionCompletado.setValue(swCuestionarioOrganizacion); }
}
