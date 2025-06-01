package com.nutrivda.app.model.pojo;

import java.io.Serializable;

public class PlanNutricional implements Serializable {

    private double imc;
    private String clasificacion_imc;
    private String analisis_personalizado;
    private int calorias_recomendadas;
    private MacroRecomendado macro_recomendado;
    private String nivel_actividad_recomendado;
    private String tiempo_estimado_para_lograr_objetivo;

    public static class MacroRecomendado implements Serializable {
        private int proteinas;
        private int grasas;
        private int carbohidratos;

        // Getters y Setters
        public int getProteinas() { return proteinas; }
        public void setProteinas(int proteinas) { this.proteinas = proteinas; }

        public int getGrasas() { return grasas; }
        public void setGrasas(int grasas) { this.grasas = grasas; }

        public int getCarbohidratos() { return carbohidratos; }
        public void setCarbohidratos(int carbohidratos) { this.carbohidratos = carbohidratos; }
    }

    // Getters y Setters
    public double getImc() { return imc; }
    public void setImc(double imc) { this.imc = imc; }

    public String getClasificacion_imc() { return clasificacion_imc; }
    public void setClasificacion_imc(String clasificacion_imc) { this.clasificacion_imc = clasificacion_imc; }

    public String getAnalisis_personalizado() { return analisis_personalizado; }
    public void setAnalisis_personalizado(String analisis_personalizado) { this.analisis_personalizado = analisis_personalizado; }

    public int getCalorias_recomendadas() { return calorias_recomendadas; }
    public void setCalorias_recomendadas(int calorias_recomendadas) { this.calorias_recomendadas = calorias_recomendadas; }

    public MacroRecomendado getMacro_recomendado() { return macro_recomendado; }
    public void setMacro_recomendado(MacroRecomendado macro_recomendado) { this.macro_recomendado = macro_recomendado; }

    public String getNivel_actividad_recomendado() { return nivel_actividad_recomendado; }
    public void setNivel_actividad_recomendado(String nivel_actividad_recomendado) { this.nivel_actividad_recomendado = nivel_actividad_recomendado; }

    public String getTiempo_estimado_para_lograr_objetivo() { return tiempo_estimado_para_lograr_objetivo; }
    public void setTiempo_estimado_para_lograr_objetivo(String tiempo_estimado_para_lograr_objetivo) { this.tiempo_estimado_para_lograr_objetivo = tiempo_estimado_para_lograr_objetivo; }
}

