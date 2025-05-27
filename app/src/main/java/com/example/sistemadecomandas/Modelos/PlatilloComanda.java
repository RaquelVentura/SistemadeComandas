package com.example.sistemadecomandas.Modelos;

    public class PlatilloComanda {
        private Platillo platillo;
        private int cantidad;

        public PlatilloComanda() {}

        public PlatilloComanda(Platillo platillo, int cantidad) {
            this.platillo = platillo;
            this.cantidad = cantidad;
        }

        public Platillo getPlatillo() { return platillo; }
        public void setPlatillo(Platillo platillo) { this.platillo = platillo; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
