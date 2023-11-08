package com.example.esportstracker;

import java.sql.Blob;

public class evento {
    private String hora;
    private String fecha;
    private String duelo;
    private String juego;
    private String desc;
    private byte[] image;

    /*public evento(String duelo, String hora, String juego, String desc, byte[] image){
        this.desc = desc;
        this.hora = hora;
        this.image = image;
        this.juego = juego;
        this.duelo = duelo;
    }*/

    public String getJuego() {
        return juego;
    }
    public void setJuego(String juego) {
        this.juego = juego;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getHora() { return hora; }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDuelo() { return duelo; }
    public void setDuelo(String duelo) {
        this.duelo = duelo;
    }

}
