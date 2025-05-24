package com.example.sistemadecomandas.Modelos;


import java.util.List;

public class Comanda {
    private String fecha;
    private String nombreCliente;
    private String codigoComanda;
    private List<Orden> orden;
    private String estadoComanda;
    public Comanda() {}

    public Comanda(String fecha, String nombreCliente, String codigoComanda, List<Orden> orden, String estadoComanda) {
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.codigoComanda = codigoComanda;
        this.orden = orden;
        this.estadoComanda = estadoComanda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCodigoComanda() {
        return codigoComanda;
    }

    public void setCodigoComanda(String codigoComanda) {
        this.codigoComanda = codigoComanda;
    }

    public List<Orden> getOrden() {
        return orden;
    }

    public void setOrden(List<Orden> orden) {
        this.orden = orden;
    }

    public String getEstadoComanda() {
        return estadoComanda;
    }

    public void setEstadoComanda(String estadoComanda) {
        this.estadoComanda = estadoComanda;
    }
}
