package com.francode.college_political_parties.Model;

public class TipoDocumento {
    private int id_tipodoc;
    private String nombre;
    private String nombre_corto;
    private String estado;

    public TipoDocumento(String nombre, String nombre_corto, String estado) {
        this.nombre = nombre;
        this.nombre_corto = nombre_corto;
        this.estado = estado;
    }

    public int getId_tipodoc() {
        return id_tipodoc;
    }

    public void setId_tipodoc(int id_tipodoc) {
        this.id_tipodoc = id_tipodoc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre_corto() {
        return nombre_corto;
    }

    public void setNombre_corto(String nombre_corto) {
        this.nombre_corto = nombre_corto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
