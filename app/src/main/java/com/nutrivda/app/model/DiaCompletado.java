package com.nutrivda.app.model;

import java.util.List;

public class DiaCompletado {
    private int id;
    private String fecha;
    private int id_usuario_fk;
    private boolean completado;
    private List<Long> desayuno;
    private boolean sw_desayuno;
    private List<Long> comida;
    private boolean sw_comida;
    private List<Long> cena;
    private boolean sw_cena;
    private int caloria_desayuno;
    private int caloria_comida;
    private int caloria_cena;

    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public int getIdUsuarioFk() { return id_usuario_fk; }
    public boolean isCompletado() { return completado; }

    public List<Long> getDesayuno() { return desayuno; }
    public void setDesayuno(List<Long> desayuno) { this.desayuno = desayuno; }

    public boolean isSwDesayuno() { return sw_desayuno; }
    public void setSwDesayuno(boolean sw_desayuno) { this.sw_desayuno = sw_desayuno; }

    public List<Long> getComida() { return comida; }
    public void setComida(List<Long> comida) { this.comida = comida; }

    public boolean isSwComida() { return sw_comida; }
    public void setSwComida(boolean sw_comida) { this.sw_comida = sw_comida; }

    public List<Long> getCena() { return cena; }
    public void setCena(List<Long> cena) { this.cena = cena; }

    public boolean isSwCena() { return sw_cena; }
    public void setSwCena(boolean sw_cena) { this.sw_cena = sw_cena; }

    public int getCaloria_desayuno() { return caloria_desayuno; }
    public void setCaloria_desayuno(int caloria_desayuno) { this.caloria_desayuno = caloria_desayuno; }

    public int getCaloria_comida() { return caloria_comida; }
    public void setCaloria_comida(int caloria_comida) { this.caloria_comida = caloria_comida; }

    public int getCaloria_cena() { return caloria_cena; }
    public void setCaloria_cena(int caloria_cena) { this.caloria_cena = caloria_cena; }
}

