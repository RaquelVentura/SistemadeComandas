package com.example.sistemadecomandas.Modelos;

public class Usuarios {
    private String nombre;
    private String rol;
    public Usuarios() {}
    public Usuarios(String nombre, String rol) {
        this.nombre = nombre;
        this.rol = rol;
    }
    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getRol() {return rol;}

    public void setRol(String rol) {this.rol = rol;}
}
