package org.example.model;

import java.sql.Timestamp;

public class MovimientoCaja {
    private int id;
    private Timestamp fecha;
    private double monto;
    private String tipoMovimiento; // "INGRESO" o "EGRESO" (Salida)
    private String descripcion;    // Ej: "Pago Proveedor", "Cambio Inicial"

    public MovimientoCaja() {
    }

    public MovimientoCaja(double monto, String tipoMovimiento, String descripcion) {
        this.monto = monto;
        this.tipoMovimiento = tipoMovimiento;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}