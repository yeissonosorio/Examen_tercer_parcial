package com.example.examen_tercer_parcial.Modelo;

public class Entrevista {
    private String audio;
    private String descripcion;
    private String fecha;
    private String foto;
    private String periodista;

    public Entrevista() {
    }

    public Entrevista(String audio, String descripcion, String fecha, String foto, String periodista) {
        this.audio = audio;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.foto = foto;
        this.periodista = periodista;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }
}
