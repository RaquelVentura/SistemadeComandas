package com.example.sistemadecomandas.Modelos;

public class EstadisticaPlatillo {
    private PlatilloComanda platilloComanda;
    private int cantidadVendida;
    private double totalGenerado;

    public EstadisticaPlatillo(PlatilloComanda platilloComanda, int cantidadVendida, double totalGenerado) {
        this.platilloComanda = platilloComanda;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado = totalGenerado;
    }

    public PlatilloComanda getPlatilloComanda() {
        return platilloComanda;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public double getTotalGenerado() {
        return totalGenerado;
    }
}
