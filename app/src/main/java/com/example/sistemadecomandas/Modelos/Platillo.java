package com.example.sistemadecomandas.Modelos;

public class Platillo {
    private String idPlatillo;
    private String imagenPlatillo;
    private String nombrePlatillo;
    private String categoria;
    private String precio;
    private String descripcion;

    public Platillo() {
    }

    public Platillo(String idPlatillo, String imagenPlatillo, String nombrePlatillo, String categoria, String precio, String descripcion) {
        this.idPlatillo = idPlatillo;
        this.imagenPlatillo = imagenPlatillo;
        this.nombrePlatillo = nombrePlatillo;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public String getIdPlatillo() {
        return idPlatillo;
    }

    public void setIdPlatillo(String idPlatillo) {
        this.idPlatillo = idPlatillo;
    }

    public String getImagenPlatillo() {
        return imagenPlatillo;
    }

    public void setImagenPlatillo(String imagenPlatillo) {
        this.imagenPlatillo = imagenPlatillo;
    }

    public String getnombrePlatillo() {
        return nombrePlatillo;
    }

    public void setnombrePlatillo(String nombrePlatillo) {
        this.nombrePlatillo = nombrePlatillo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
