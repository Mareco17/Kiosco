package org.example.model;

import java.sql.Timestamp;

public class Venta {
    private int id;
    private Timestamp fecha; // Usamos Timestamp para guardar día y hora exacta
    private double total;

    public Venta() {
    }

    public Venta(int id, Timestamp fecha, double total) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}