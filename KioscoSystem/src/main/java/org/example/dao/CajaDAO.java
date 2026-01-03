package org.example.dao;

import org.example.model.MovimientoCaja;
import org.example.util.ConexionDB;

import java.sql.*;

public class CajaDAO {

    // 1. Registrar un movimiento manual (No venta)
    public void registrarMovimiento(MovimientoCaja movimiento) {
        String sql = "INSERT INTO movimientos_caja (monto, tipo_movimiento, descripcion) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, movimiento.getMonto());
            pstmt.setString(2, movimiento.getTipoMovimiento());
            pstmt.setString(3, movimiento.getDescripcion());

            pstmt.executeUpdate();
            System.out.println("Movimiento de caja registrado: " + movimiento.getDescripcion());

        } catch (SQLException e) {
            System.out.println("Error al registrar en caja: " + e.getMessage());
        }
    }

    // 2. Calcular CUÁNTA PLATA DEBERÍA HABER (La "Arqueo de Caja")
    // Suma Ventas + Ingresos de Caja - Egresos de Caja
    public double obtenerSaldoTotal() {
        double saldo = 0.0;

        // A. Sumar todas las ventas históricas
        String sqlVentas = "SELECT SUM(total) FROM ventas";
        // B. Sumar entradas manuales (Cambio inicial, etc)
        String sqlEntradas = "SELECT SUM(monto) FROM movimientos_caja WHERE tipo_movimiento = 'INGRESO'";
        // C. Sumar salidas manuales (Pago proveedores, retiros)
        String sqlSalidas = "SELECT SUM(monto) FROM movimientos_caja WHERE tipo_movimiento = 'EGRESO'";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement()) {

            // Ejecutar A (Ventas)
            ResultSet rs = stmt.executeQuery(sqlVentas);
            if (rs.next()) saldo += rs.getDouble(1);

            // Ejecutar B (Ingresos Manuales)
            rs = stmt.executeQuery(sqlEntradas);
            if (rs.next()) saldo += rs.getDouble(1);

            // Ejecutar C (Restar Salidas)
            rs = stmt.executeQuery(sqlSalidas);
            if (rs.next()) saldo -= rs.getDouble(1);

        } catch (SQLException e) {
            System.out.println("Error al calcular saldo: " + e.getMessage());
        }
        return saldo;
    }
}