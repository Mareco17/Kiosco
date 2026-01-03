package org.example.view;

import org.example.dao.ProductoDAO;
import org.example.dao.VentaDAO;
import org.example.model.DetalleVenta;
import org.example.model.Producto;
import org.example.model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp; // Importante para la fecha
import java.util.ArrayList;
import java.util.List;

public class PantallaVenta extends JFrame {

    private JTextField txtIdProducto, txtCantidad;
    private JLabel lblTotal, lblProductoEncontrado;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;

    // Aquí guardaremos las cosas MIENTRAS se está haciendo la venta (el "carrito")
    private List<DetalleVenta> carrito = new ArrayList<>();
    private ProductoDAO productoDAO = new ProductoDAO();
    private VentaDAO ventaDAO = new VentaDAO();
    private double totalVenta = 0.0;

    public PantallaVenta() {
        setTitle("Nueva Venta - Caja");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // --- PANEL SUPERIOR: Búsqueda ---
        JPanel panelSuperior = new JPanel(new FlowLayout());

        panelSuperior.add(new JLabel("ID Producto:"));
        txtIdProducto = new JTextField(5);
        panelSuperior.add(txtIdProducto);

        // --- NUEVO BOTÓN DE BÚSQUEDA ---
        JButton btnBuscar = new JButton("🔍"); // Lupa
        panelSuperior.add(btnBuscar);

        // Acción: Al tocar la lupa
        btnBuscar.addActionListener(e -> abrirBuscador());

        panelSuperior.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("1", 3);
        panelSuperior.add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar al Carrito");
        panelSuperior.add(btnAgregar);

        lblProductoEncontrado = new JLabel("---");
        lblProductoEncontrado.setForeground(Color.BLUE);
        panelSuperior.add(lblProductoEncontrado);

        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Tabla del Carrito ---
        String[] columnas = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDetalles = new JTable(modeloTabla);
        add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        // --- PANEL INFERIOR: Total y Pagar ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        lblTotal = new JLabel("TOTAL A PAGAR: $0.0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        panelInferior.add(lblTotal);

        JButton btnFinalizar = new JButton("COBRAR ($)");
        btnFinalizar.setBackground(Color.GREEN);
        btnFinalizar.setForeground(Color.BLACK);
        panelInferior.add(btnFinalizar);

        add(panelInferior, BorderLayout.SOUTH);

        // --- ACCIONES DE LOS BOTONES ---
        btnAgregar.addActionListener(e -> agregarProducto());
        btnFinalizar.addActionListener(e -> finalizarVenta());
    }

    private void agregarProducto() {
        try {
            int id = Integer.parseInt(txtIdProducto.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());

            // 1. Buscamos si existe
            Producto p = productoDAO.buscarPorId(id);

            if (p != null) {
                // 2. Verificar Stock
                if (p.getStockActual() >= cantidad) {

                    // Calcular subtotal
                    double subtotal = p.getPrecioVenta() * cantidad;

                    // Agregar a la tabla visual
                    modeloTabla.addRow(new Object[]{p.getNombre(), cantidad, p.getPrecioVenta(), subtotal});

                    // Agregar a la lista lógica (Carrito)
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdProducto(p.getId());
                    detalle.setCantidad(cantidad);
                    detalle.setPrecioUnitario(p.getPrecioVenta());
                    carrito.add(detalle);

                    // Actualizar Total General
                    totalVenta += subtotal;
                    lblTotal.setText("TOTAL A PAGAR: $" + totalVenta);

                    // Limpiar casillas para el siguiente
                    txtIdProducto.setText("");
                    txtCantidad.setText("1");
                    txtIdProducto.requestFocus(); // Volver el foco al input

                } else {
                    JOptionPane.showMessageDialog(this, "¡No hay suficiente stock! Quedan: " + p.getStockActual());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese números válidos.");
        }
    }

    private void finalizarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        // 1. Crear el objeto Venta (Cabecera)
        Venta venta = new Venta();
        venta.setTotal(totalVenta);
        // La fecha actual se pone sola con esto:
        venta.setFecha(new Timestamp(System.currentTimeMillis()));

        // 2. Guardar todo en la BD
        boolean exito = ventaDAO.registrarVenta(venta, carrito);

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Venta Registrada con Éxito!");
            dispose(); // Cierra la ventana
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la venta.");
        }
    }

    private void abrirBuscador() {
        // 1. Pedir al usuario qué quiere buscar
        String texto = JOptionPane.showInputDialog(this, "¿Qué producto buscas? (Ej: alfajor)");

        if (texto != null && !texto.isEmpty()) {
            // 2. Buscar en la base de datos
            List<Producto> resultados = productoDAO.buscarPorNombre(texto);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos.");
                return;
            }

            // 3. Crear una lista simple para que el usuario elija
            // Convertimos la lista de objetos a un arreglo de Strings bonitos
            Producto[] opciones = resultados.toArray(new Producto[0]);

            // Mostrar menú de selección
            Producto seleccionado = (Producto) JOptionPane.showInputDialog(
                    this,
                    "Selecciona el producto:",
                    "Resultados de Búsqueda",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]); // El primero seleccionado por defecto

            // 4. Si eligió uno, llenamos el campo ID automáticamente
            if (seleccionado != null) {
                txtIdProducto.setText(String.valueOf(seleccionado.getId()));
                lblProductoEncontrado.setText(seleccionado.getNombre());
                txtCantidad.requestFocus(); // Pasamos el foco a la cantidad
            }
        }
    }
}