package com.nutrivda.app.test;

public class ResultadoTest {
    private String tipo;
    private String resultado;
    private String resumen;
    private String fecha;
    public ResultadoTest(String tipo, String resultado, String resumen, String fecha) {
        this.tipo = tipo;
        this.resultado = resultado;
        this.resumen = resumen;
        this.fecha = fecha;
    }

    public String getTipo() { return tipo; }
    public String getResultado() { return resultado; }
    public String getResumen() { return resumen; }
    public String getFecha() { return fecha; }
}