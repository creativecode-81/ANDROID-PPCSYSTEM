package com.francode.college_political_parties.Model;

public class Alumno {

    private int id_alumno;
    private String nombres;
    private String apellido_paterno;
    private String apellido_materno;
    private TipoDocumento typeDoc;
    private String nro_doc;
    private String telefono;
    private String estado;

    public Alumno(String nombres, String apellido_paterno, String apellido_materno, TipoDocumento typeDoc, String nro_doc, String telefono, String estado) {
        this.nombres = nombres;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.typeDoc = typeDoc;
        this.nro_doc = nro_doc;
        this.telefono = telefono;
        this.estado = estado;
    }

    public int getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(int id_alumno) {
        this.id_alumno = id_alumno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido_paterno() {
        return apellido_paterno;
    }

    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getApellido_materno() {
        return apellido_materno;
    }

    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public TipoDocumento getTypeDoc() {
        return typeDoc;
    }

    public void setTypeDoc(TipoDocumento typeDoc) {
        this.typeDoc = typeDoc;
    }

    public String getNro_doc() {
        return nro_doc;
    }

    public void setNro_doc(String nro_doc) {
        this.nro_doc = nro_doc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
