package com.nutrivda.app.model;

public class DatosUsuario {
    private int id_usuario_fk;
    private double peso;
    private double altura;
    private int edad;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String actividad_fisica;

    public DatosUsuario(int  id_usuario_fk, double peso, double altura, int edad, String nombre, String apellido1, String apellido2, String actividadFisica) {
        this.id_usuario_fk = id_usuario_fk;
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.actividad_fisica = actividadFisica;
    }

    public DatosUsuario(double peso, double altura, int edad, String nombre, String apellido1, String apellido2, String actividadFisica) {
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.actividad_fisica = actividadFisica;
    }

    public int getId_usuario_fk() {
        return id_usuario_fk;
    }

    public void setId_usuario_fk(int id_usuario_fk) {
        this.id_usuario_fk = id_usuario_fk;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getActividad_fisica() {
        return actividad_fisica;
    }

    public void setActividad_fisica(String actividad_fisica) {
        this.actividad_fisica = actividad_fisica;
    }
}
