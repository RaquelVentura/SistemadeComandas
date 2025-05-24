package com.example.sistemadecomandas.Modelos;

public class Usuario{
    private String id;
    private String imagen;
    private String nombre;
    private String rol;
    public Usuario() {}

    public Usuario(String id, String imagen, String nombre, String rol) {
        this.id = id;
        this.imagen = imagen;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
