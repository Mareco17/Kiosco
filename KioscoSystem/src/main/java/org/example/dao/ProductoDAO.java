package org.example.dao;

import org.example.model.Producto;
import org.example.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO { // <--- ¡ESTO ES LO QUE FALTABA!

    // 1. LISTAR TODOS
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    // 2. LISTAR CATEGORÍAS ÚNICAS
    public List<String> listarCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM productos WHERE categoria IS NOT NULL AND categoria != ''";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }
        return categorias;
    }

    // 3. MÉTODO MAESTRO: FILTRAR AVANZADO
    public List<Producto> filtrarAvanzado(String texto, String categoria, boolean soloStockBajo) {
        List<Producto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM productos WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (texto != null && !texto.isEmpty()) {
            sql.append(" AND (nombre LIKE ? OR codigo_barra LIKE ?)");
            parametros.add("%" + texto + "%");
            parametros.add("%" + texto + "%");
        }
        if (categoria != null && !categoria.equals("Todas")) {
            sql.append(" AND categoria = ?");
            parametros.add(categoria);
        }
        if (soloStockBajo) {
            sql.append(" AND stock_actual <= stock_minimo");
        }

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error filtro avanzado: " + e.getMessage());
        }
        return lista;
    }

    // 4. GUARDAR
    public void guardar(Producto p) {
        String sql = "INSERT INTO productos (nombre, codigo_barra, precio_costo, precio_venta, stock_actual, stock_minimo, categoria) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getCodigoBarra());
            pstmt.setDouble(3, p.getPrecioCompra());
            pstmt.setInt(4, p.getPrecioVenta());
            pstmt.setInt(5, p.getStockActual());
            pstmt.setInt(6, p.getStockMinimo());
            pstmt.setString(7, p.getCategoria());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    // 5. ACTUALIZAR
    public void actualizar(Producto p) {
        String sql = "UPDATE productos SET nombre=?, codigo_barra=?, precio_venta=?, stock_actual=?, stock_minimo=?, categoria=? WHERE id=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getCodigoBarra());
            pstmt.setInt(3, p.getPrecioVenta());
            pstmt.setInt(4, p.getStockActual());
            pstmt.setInt(5, p.getStockMinimo());
            pstmt.setString(6, p.getCategoria());
            pstmt.setInt(7, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }

    // 6. ELIMINAR
    public void eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    // 7. BUSCAR POR ID
    public Producto buscarPorId(int idBuscado) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        Producto p = null;
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBuscado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                p = mapResultSetToProducto(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar por ID: " + e.getMessage());
        }
        return p;
    }

    // AUXILIAR
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setCodigoBarra(rs.getString("codigo_barra"));
        p.setPrecioCompra(rs.getDouble("precio_costo"));
        p.setPrecioVenta(rs.getInt("precio_venta"));
        p.setStockActual(rs.getInt("stock_actual"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setCategoria(rs.getString("categoria"));
        return p;
    }

} // <--- ESTA LLAVE CIERRA LA CLASE. ¡ES FUNDAMENTAL!