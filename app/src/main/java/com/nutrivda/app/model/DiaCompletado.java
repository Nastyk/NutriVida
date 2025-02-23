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
}

