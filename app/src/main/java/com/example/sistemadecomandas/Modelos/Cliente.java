package com.example.sistemadecomandas.Modelos;

public class Cliente {
    private String codigoComanda;
    private String nombreCliente;
    public Cliente() {}
    public Cliente(String codigoComanda, String nombreCliente) {
        this.codigoComanda = codigoComanda;
        this.nombreCliente = nombreCliente;
    }

    public String getCodigoComanda() {
        return codigoComanda;
    }

    public void setCodigoComanda(String codigoComanda) {
        this.codigoComanda = codigoComanda;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}
