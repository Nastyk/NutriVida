package com.nutrivda.app.model;

public class Comida {
    private int id;
    private String created_at;
    private String tipo_comida;
    private String desc_comida;
    private int calorias;
    private int proteinas;
    private int grasas;
    private int carbohidratos;

    // Getters para acceder a los datos
    public int getId() { return id; }
    public String getCreatedAt() { return created_at; }
    public String getTipoComida() { return tipo_comida; }
    public String getDescComida() { return desc_comida; }
    public int getCalorias() { return calorias; }
    public int getProteinas() { return proteinas; }
    public int getGrasas() { return grasas; }
    public int getCarbohidratos() { return carbohidratos; }
}
