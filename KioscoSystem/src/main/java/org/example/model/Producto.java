package org.example.model;

import java.sql.Date;

public class Producto {
    private int id;
    private String codigoBarra;
    private String nombre;
    private double precioCompra;
    private int precioVenta;
    private int stockActual;
    private int stockMinimo;
    private String categoria;
    private java.sql.Date fechaVencimiento;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public int getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(int precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        categoria = categoria;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Producto() {
    }

    public Producto(int id, String codigoBarra, String nombre, double precioCompra, int precioVenta, int stockActual, int stockMinimo, String categoria, Date fechaVencimiento) {
        this.id = id;
        this.codigoBarra = codigoBarra;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        categoria = categoria;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Esto hace que al imprimir el objeto, salga su nombre y precio
    @Override
    public String toString() {
        return nombre + " ($" + precioVenta + ")";
    }
}