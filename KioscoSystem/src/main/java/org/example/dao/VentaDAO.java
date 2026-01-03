package org.example.dao;

import org.example.model.DetalleVenta;
import org.example.model.Venta;
import org.example.util.ConexionDB;

import java.sql.*;
import java.util.List;

public class VentaDAO {

    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection conn = ConexionDB.conectar();

        // Consultas SQL preparadas
        String sqlVenta = "INSERT INTO ventas (total) VALUES (?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id = ?";

        try {
            // 1. Iniciamos la Transacción (Desactivamos el guardado automático)
            // Esto significa: "No guardes nada en el disco hasta que yo te diga COMMIT"
            if (conn != null) {
                conn.setAutoCommit(false);

                // --- PASO A: Guardar la Venta (Cabecera) ---
                PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                pstmtVenta.setDouble(1, venta.getTotal());
                pstmtVenta.executeUpdate();

                // Recuperar el ID que la base de datos le asignó a esta venta (Ej: Ticket #1)
                ResultSet rs = pstmtVenta.getGeneratedKeys();
                int idVenta = 0;
                if (rs.next()) {
                    idVenta = rs.getInt(1);
                }

                // --- PASO B: Guardar los Detalles y Restar Stock ---
                PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
                PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock);

                for (DetalleVenta detalle : detalles) {
                    // Guardar detalle
                    pstmtDetalle.setInt(1, idVenta);
                    pstmtDetalle.setInt(2, detalle.getIdProducto());
                    pstmtDetalle.setInt(3, detalle.getCantidad());
                    pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                    pstmtDetalle.executeUpdate();

                    // Restar stock
                    pstmtStock.setInt(1, detalle.getCantidad()); // Cuánto resto
                    pstmtStock.setInt(2, detalle.getIdProducto()); // A qué producto
                    pstmtStock.executeUpdate();
                }

                // --- PASO C: Confirmar todo ---
                conn.commit(); // ¡Aquí es donde realmente se guarda todo!
                System.out.println("Venta registrada con éxito. Ticket Nº: " + idVenta);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar venta: " + e.getMessage());
            // Si algo falló, deshacemos todo lo que hicimos en memoria
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Cerramos la conexión manualmente porque no usamos try-with-resources aquí arriba
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}