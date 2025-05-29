package com.nutrivda.app.inicializacion;

import java.util.ArrayList;
import java.util.List;

// Esta clase la uso para guardar las respuestas del usuario durante el onboarding
public class OnboardingData {
    private static OnboardingData instance;

    private String actividadFisica;
    private String prioridad;
    private List<String> restricciones = new ArrayList<>();
    private String organizacionComidas;

    public static OnboardingData getInstance() {
        if (instance == null) {
            instance = new OnboardingData();
        }
        return instance;
    }

    public void reset() {
        actividadFisica = null;
        prioridad = null;
        restricciones.clear();
        organizacionComidas = null;
    }

    // Setters y Getters que uso desde los fragments para guardar y consultar respuestas

    public String getActividadFisica() { return actividadFisica; }
    public void setActividadFisica(String actividadFisica) { this.actividadFisica = actividadFisica; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public List<String> getRestricciones() { return restricciones; }
    public void setRestricciones(List<String> restricciones) { this.restricciones = restricciones; }

    public String getOrganizacionComidas() { return organizacionComidas; }
    public void setOrganizacionComidas(String organizacionComidas) { this.organizacionComidas = organizacionComidas; }
}
