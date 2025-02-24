package com.nutrivda.app.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("id")  // Este es el nombre de la columna en Supabase
    private Integer userId; // Puede ser null al crear un usuario nuevo
    @SerializedName("usuario")
    private String usuario;
    @SerializedName("contraseña")
    private String contraseña;

    public Usuario(String usuario, String contraseña) {
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public Usuario(Integer userId, String usuario, String contraseña) {
        this.userId = userId;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    // Getters y Setters
    public Integer getId() {
        return userId;
    }

    public void setId(Integer userId) {
        this.userId = userId;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }
}
