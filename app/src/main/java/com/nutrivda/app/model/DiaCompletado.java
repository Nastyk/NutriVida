package com.nutrivda.app.model;

public class DiaCompletado {
    private int id;
    private String fecha;
    private int id_usuario_fk;
    private boolean completado;
    private String desayuno;
    private boolean sw_desayuno;
    private String comida;
    private boolean sw_comida;
    private String cena;
    private boolean sw_cena;
    private int caloria_desayuno;
    private int caloria_comida;
    private int caloria_cena;


    // Getters
    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public int getIdUsuarioFk() { return id_usuario_fk; }
    public boolean isCompletado() { return completado; }
    public String getDesayuno() { return desayuno; }
    public boolean isSwDesayuno() { return sw_desayuno; }
    public String getComida() { return comida; }
    public boolean isSwComida() { return sw_comida; }
    public String getCena() { return cena; }
    public boolean isSwCena() { return sw_cena; }

    public int getCaloria_desayuno() {
        return caloria_desayuno;
    }

    public void setCaloria_desayuno(int caloria_desayuno) {
        this.caloria_desayuno = caloria_desayuno;
    }

    public int getCaloria_comida() {
        return caloria_comida;
    }

    public void setCaloria_comida(int caloria_comida) {
        this.caloria_comida = caloria_comida;
    }

    public int getCaloria_cena() {
        return caloria_cena;
    }

    public void setCaloria_cena(int caloria_cena) {
        this.caloria_cena = caloria_cena;
    }
}

