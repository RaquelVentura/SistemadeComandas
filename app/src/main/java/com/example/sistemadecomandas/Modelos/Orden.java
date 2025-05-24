package com.example.sistemadecomandas.Modelos;

public class Orden {
    private String producto;
    private String cantidad;
    private String nota;
    private String totalPagar;
    private String mesero;
    private String estadoPlatillo;

    public Orden() {}

    public Orden(String producto, String cantidad, String nota, String totalPagar, String mesero, String estadoPlatillo) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.nota = nota;
        this.totalPagar = totalPagar;
        this.mesero = mesero;
        this.estadoPlatillo = estadoPlatillo;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(String totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getEstadoPlatillo() {
        return estadoPlatillo;
    }

    public void setEstadoPlatillo(String estadoPlatillo) {
        this.estadoPlatillo = estadoPlatillo;
    }
}
