package org.example.view;

import org.example.dao.ProductoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelBuscador extends JPanel {

    // Solo los 3 elementos de control
    private JTextField txtBuscar;
    private JComboBox<String> comboCategorias;
    private JCheckBox chkStockBajo;

    private PantallaPrincipal pantallaPrincipal;
    private ProductoDAO productoDAO;

    public PanelBuscador(PantallaPrincipal pantallaPrincipal) {
        this.pantallaPrincipal = pantallaPrincipal;
        this.productoDAO = new ProductoDAO();

        // Diseño limpio, alineado a la izquierda
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); // 15px de separación
        setBackground(new Color(230, 240, 250));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        initComponentes();
    }

    private void initComponentes() {
        // --- 1. BUSQUEDA (Texto) ---
        add(new JLabel("🔍 Buscar:"));
        txtBuscar = new JTextField(15);
        add(txtBuscar);

        // --- 2. CATEGORÍA (Desplegable) ---
        add(new JLabel("Categoría:"));
        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("Todas"); // Opción por defecto

        // Cargar categorías de la BD
        for (String cat : productoDAO.listarCategorias()) {
            comboCategorias.addItem(cat);
        }
        add(comboCategorias);

        // --- 3. STOCK BAJO (Casilla) ---
        chkStockBajo = new JCheckBox("⚠️ Solo Stock Bajo");
        chkStockBajo.setBackground(new Color(230, 240, 250));
        chkStockBajo.setFocusPainted(false); // Quitar borde feo al hacer clic
        add(chkStockBajo);

        // --- MAGIA AUTOMÁTICA (Sin botones) ---
        // Creamos una acción común: "Cuando pase algo, FILTRAR"
        ActionListener accion = e -> ejecutarFiltros();

        txtBuscar.addActionListener(accion);      // Se activa al dar ENTER
        comboCategorias.addActionListener(accion); // Se activa al cambiar de OPCIÓN
        chkStockBajo.addActionListener(accion);    // Se activa al hacer CLIC
    }

    private void ejecutarFiltros() {
        // Recogemos los 3 datos en tiempo real
        String texto = txtBuscar.getText();
        String categoria = (String) comboCategorias.getSelectedItem();
        boolean stockBajo = chkStockBajo.isSelected();

        // Le mandamos todo junto a la pantalla principal
        pantallaPrincipal.aplicarFiltros(texto, categoria, stockBajo);
    }
}