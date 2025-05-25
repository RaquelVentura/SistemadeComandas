package com.example.sistemadecomandas.Modelos;


import java.util.List;

public class Comanda {
    private String fecha;
    private String nombreCliente;
    private String codigoComanda;
    private List<Platillo> platillos;
    private String estadoComanda;
    private String mesero;
    private String nota;
    private String totalPagar;
    public Comanda() {}

    public Comanda(String fecha, String nombreCliente, String codigoComanda, List<Platillo> platillos, String estadoComanda, String mesero, String nota, String totalPagar) {
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.codigoComanda = codigoComanda;
        this.platillos = platillos;
        this.estadoComanda = estadoComanda;
        this.mesero = mesero;
        this.nota = nota;
        this.totalPagar = totalPagar;
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

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public List<Platillo> getPlatillos() {
        return platillos;
    }

    public void setPlatillos(List<Platillo> platillos) {
        this.platillos = platillos;
    }

    public String getEstadoComanda() {
        return estadoComanda;
    }

    public void setEstadoComanda(String estadoComanda) {
        this.estadoComanda = estadoComanda;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(String totalPagar) {
        this.totalPagar = totalPagar;
    }
}
