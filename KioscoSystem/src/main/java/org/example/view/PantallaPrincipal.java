package org.example.view;

import org.example.dao.ProductoDAO;
import org.example.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PantallaPrincipal extends JFrame {

    // Componentes Globales
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private ProductoDAO productoDAO;
    private List<Producto> listaProductos;

    // Nuestro pintor separado
    private StockSemaforoRenderer pintorSemaforo;

    public PantallaPrincipal() {
        productoDAO = new ProductoDAO();
        pintorSemaforo = new StockSemaforoRenderer(); // Instanciamos al pintor

        // Configuración básica de la ventana
        configurarVentana();

        // 1. Armar la interfaz visual (dividir y vencerás)
        construirMenuSuperior();
        JPanel panelContenido = new JPanel(new BorderLayout());

        panelContenido.add(construirPanelHerramientas(), BorderLayout.NORTH);
        panelContenido.add(construirTabla(), BorderLayout.CENTER);

        add(panelContenido);

        // 2. Configurar Atajos y Eventos
        configurarAtajosTeclado();

        // 3. Carga inicial
        cargarDatos();
    }

    // --- SECCIÓN 1: MÉTODOS DE CONSTRUCCIÓN VISUAL ---

    private void configurarVentana() {
        setTitle("Sistema de Gestión - Kiosco");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAdmin = new JMenu("Administración");

        JMenuItem itemCaja = new JMenuItem("Ver Dinero / Caja");
        itemCaja.addActionListener(e -> { PantallaCaja caja = new PantallaCaja(); caja.setVisible(true); });

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuAdmin.add(itemCaja);
        menuAdmin.addSeparator();
        menuAdmin.add(itemSalir);
        menuBar.add(menuAdmin);
        setJMenuBar(menuBar);
    }

    private JPanel construirPanelHerramientas() {
        JPanel panelContenedor = new JPanel();
        panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));

        // A. Barra de Iconos (ToolBar) - ESTO YA LO TIENES IGUAL
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        // ... (tus botones de Venta, Nuevo, etc. déjalos igual) ...
        // ...

        // --- CAMBIO AQUÍ ---
        // Instanciamos el nuevo panel buscador
        PanelBuscador panelBuscador = new PanelBuscador(this);

        // IMPORTANTE: Le damos un nombre a la variable para poder recargarla despues si hace falta
        // (Por ahora lo dejamos así simple)

        panelContenedor.add(toolBar);
        panelContenedor.add(panelBuscador); // <--- AGREGAMOS EL BUSCADOR

        return panelContenedor;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"ID", "Nombre", "Precio Venta", "Stock", "Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(25);
        tablaProductos.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // AQUÍ USAMOS NUESTRA CLASE SEPARADA
        tablaProductos.setDefaultRenderer(Object.class, pintorSemaforo);

        // Evento Doble Clic
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarMenuContextual(e);
                }
            }
        });

        return new JScrollPane(tablaProductos);
    }

    // --- SECCIÓN 2: LÓGICA Y EVENTOS ---

    private void mostrarMenuContextual(MouseEvent e) {
        int r = tablaProductos.rowAtPoint(e.getPoint());
        if (r >= 0) tablaProductos.setRowSelectionInterval(r, r);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("✏️ Editar (F2)");
        itemEditar.addActionListener(ev -> editarProductoSeleccionado());
        JMenuItem itemEliminar = new JMenuItem("🗑️ Eliminar (Supr)");
        itemEliminar.addActionListener(ev -> eliminarProductoSeleccionado());

        menu.add(itemEditar);
        menu.add(itemEliminar);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void configurarAtajosTeclado() {
        InputMap inputMap = tablaProductos.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = tablaProductos.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        actionMap.put("eliminar", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { eliminarProductoSeleccionado(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "editar");
        actionMap.put("editar", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { editarProductoSeleccionado(); } });

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "venta");
        contentPane.getActionMap().put("venta", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { abrirVenta(); } });
    }

    // --- SECCIÓN 3: NEGOCIO (CARGAR, GUARDAR, BORRAR) ---

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        listaProductos = productoDAO.listar();

        // LE AVISAMOS AL PINTOR QUE LA LISTA CAMBIÓ
        pintorSemaforo.setListaProductos(listaProductos);

        for (Producto p : listaProductos) {
            modeloTabla.addRow(new Object[]{ p.getId(), p.getNombre(), p.getPrecioVenta(), p.getStockActual(), p.getCategoria() });
        }
    }

    // --- MÉTODO FILTRAR (Necesario para el PanelBuscador) ---
    public void filtrar(String criterio, String valor) {
        modeloTabla.setRowCount(0);

        if (criterio.equals("Stock Bajo")) {
            listaProductos = productoDAO.filtrar("Stock Bajo", "");
        } else {
            listaProductos = productoDAO.filtrar(criterio, valor);
        }

        // Actualizamos el pintor con la nueva lista filtrada
        pintorSemaforo.setListaProductos(listaProductos);

        for (Producto p : listaProductos) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getPrecioVenta(), p.getStockActual(), p.getCategoria()
            });
        }
    }

    private void abrirVenta() {
        PantallaVenta venta = new PantallaVenta();
        venta.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) { cargarDatos(); }
        });
        venta.setVisible(true);
    }

    private void eliminarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Borrar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            productoDAO.eliminar(id);
            cargarDatos();
        }
    }

    private void editarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) return;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Producto p = productoDAO.buscarPorId(id);
        if (p != null) {
            new FormularioProducto(this, p).setVisible(true);
        }
    }

    // --- PEGA ESTO EN PANTALLAPRINCIPAL.JAVA (AL FINAL) ---

    public void aplicarFiltros(String texto, String categoria, boolean stockBajo) {
        // 1. Limpiamos la tabla
        modeloTabla.setRowCount(0);

        // 2. Le pedimos al DAO que busque con los 3 datos combinados
        listaProductos = productoDAO.filtrarAvanzado(texto, categoria, stockBajo);

        // 3. Le avisamos al "Pintor" que la lista cambió (para que calcule los colores nuevos)
        if (pintorSemaforo != null) {
            pintorSemaforo.setListaProductos(listaProductos);
        }

        // 4. Llenamos la tabla visual
        for (Producto p : listaProductos) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getPrecioVenta(), p.getStockActual(), p.getCategoria()
            });
        }

        // 5. Truco: Seleccionamos el primero para que puedas dar F2 rápido
        if (modeloTabla.getRowCount() > 0) {
            tablaProductos.setRowSelectionInterval(0, 0);
        }
    }
}