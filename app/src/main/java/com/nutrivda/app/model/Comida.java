package com.nutrivda.app.model;

public class Comida {
    private int id;
    private String desc_comida;
    private int calorias;
    private int proteinas;
    private int grasas;
    private int carbohidratos;
    private int tRacion;
    private int nRacion;
    private int id_usuario_fk;
    private String unidadMedia;

    // Getters para acceder a los datos
    public int getId() { return id; }
    public String getDescComida() { return desc_comida; }
    public int getCalorias() { return calorias; }
    public int getProteinas() { return proteinas; }
    public int getGrasas() { return grasas; }
    public int getCarbohidratos() { return carbohidratos; }
    public int getTamanioRacion() { return tRacion; }
    public int getNumeroRaciones() { return nRacion; }
    public int getId_usuario_fk() { return id_usuario_fk; }
    public String getUnidadDMedida() { return unidadMedia; }

    public void setDesc_comida(String desc_comida) {
        this.desc_comida = desc_comida;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public void setProteinas(int proteinas) {
        this.proteinas = proteinas;
    }

    public void setGrasas(int grasas) {
        this.grasas = grasas;
    }

    public void setCarbohidratos(int carbohidratos) {
        this.carbohidratos = carbohidratos;
    }

    public void settRacion(int tRacion) {
        this.tRacion = tRacion;
    }

    public void setnRacion(int nRacion) {
        this.nRacion = nRacion;
    }

    public void setId_usuario_fk(int id_usuario_fk) {
        this.id_usuario_fk = id_usuario_fk;
    }

    public void setUnidadMedia(String unidadMedia) {
        this.unidadMedia = unidadMedia;
    }
}
