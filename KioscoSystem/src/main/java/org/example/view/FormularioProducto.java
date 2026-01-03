package org.example.view;

import org.example.dao.ProductoDAO;
import org.example.model.Producto;
import javax.swing.*;
import java.awt.*;

public class FormularioProducto extends JFrame {

    // Agregamos txtStockMinimo
    private JTextField txtNombre, txtCodigo, txtPrecio, txtStock, txtStockMinimo, txtCategoria;
    private ProductoDAO productoDAO;
    private PantallaPrincipal pantallaPrincipal;
    private Producto productoAEditar;

    public FormularioProducto(PantallaPrincipal pantallaPrincipal, Producto producto) {
        this.pantallaPrincipal = pantallaPrincipal;
        this.productoAEditar = producto;
        this.productoDAO = new ProductoDAO();

        if (producto == null) {
            setTitle("Nuevo Producto");
        } else {
            setTitle("Editar Producto: " + producto.getNombre());
        }

        setSize(400, 500); // Un poco más alto para que entre el nuevo campo
        setLayout(new GridLayout(8, 2, 10, 10)); // 8 filas ahora
        setLocationRelativeTo(null);

        // Campos
        add(new JLabel("  Nombre del Producto:"));
        txtNombre = new JTextField();
        add(txtNombre);

        add(new JLabel("  Código de Barras:"));
        txtCodigo = new JTextField();
        add(txtCodigo);

        add(new JLabel("  Precio de Venta:"));
        txtPrecio = new JTextField();
        add(txtPrecio);

        add(new JLabel("  Stock Actual:"));
        txtStock = new JTextField();
        add(txtStock);

        // --- NUEVO CAMPO ---
        add(new JLabel("  Stock Mínimo (Alerta):"));
        txtStockMinimo = new JTextField();
        add(txtStockMinimo);
        // -------------------

        add(new JLabel("  Categoría:"));
        txtCategoria = new JTextField();
        add(txtCategoria);

        // Rellenar si es EDITAR
        if (producto != null) {
            txtNombre.setText(producto.getNombre());
            txtCodigo.setText(producto.getCodigoBarra());
            txtPrecio.setText(String.valueOf(producto.getPrecioVenta()));
            txtStock.setText(String.valueOf(producto.getStockActual()));
            // Cargar el stock minimo que tenga guardado
            txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
            txtCategoria.setText(producto.getCategoria());

            // Truco del cursor
            SwingUtilities.invokeLater(() -> {
                if (txtNombre.getText().length() > 0) txtNombre.setCaretPosition(txtNombre.getText().length());
            });
        } else {
            // Si es NUEVO, poner un valor por defecto (ej: 5)
            txtStockMinimo.setText("5");
        }

        JButton btnGuardar = new JButton("Guardar Cambios");
        add(new JLabel(""));
        add(btnGuardar);

        btnGuardar.addActionListener(e -> guardar());
    }

    private void guardar() {
        try {
            String nombre = txtNombre.getText();
            String codigo = txtCodigo.getText();
            int precio = Integer.parseInt(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int stockMin = Integer.parseInt(txtStockMinimo.getText()); // Leer nuevo campo
            String categoria = txtCategoria.getText();

            if (productoAEditar == null) {
                // NUEVO
                Producto nuevo = new Producto();
                nuevo.setNombre(nombre);
                nuevo.setCodigoBarra(codigo);
                nuevo.setPrecioVenta(precio);
                nuevo.setStockActual(stock);
                nuevo.setStockMinimo(stockMin); // Guardar alerta personalizada
                nuevo.setCategoria(categoria);
                nuevo.setPrecioCompra(0.0);

                productoDAO.guardar(nuevo);
                JOptionPane.showMessageDialog(this, "¡Producto Creado!");
            } else {
                // EDITAR
                productoAEditar.setNombre(nombre);
                productoAEditar.setCodigoBarra(codigo);
                productoAEditar.setPrecioVenta(precio);
                productoAEditar.setStockActual(stock);
                productoAEditar.setStockMinimo(stockMin); // Actualizar alerta
                productoAEditar.setCategoria(categoria);

                productoDAO.actualizar(productoAEditar);
                JOptionPane.showMessageDialog(this, "¡Producto Actualizado!");
            }

            pantallaPrincipal.cargarDatos();
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Precio y Stocks deben ser números.");
        }
    }
}