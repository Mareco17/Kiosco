package org.example.view;

import org.example.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

// Esta clase solo se dedica a pintar. Nada más.
public class StockSemaforoRenderer extends DefaultTableCellRenderer {

    private List<Producto> listaProductos;

    // Necesitamos que alguien nos pase la lista actualizada para saber los stocks
    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            // Buscamos el producto por su ID (que está en la columna 0)
            int idProducto = Integer.parseInt(table.getValueAt(row, 0).toString());

            Producto p = null;
            if (listaProductos != null) {
                for (Producto prod : listaProductos) {
                    if (prod.getId() == idProducto) {
                        p = prod;
                        break;
                    }
                }
            }

            // Aplicamos colores
            if (p != null) {
                if (p.getStockActual() == 0) {
                    c.setBackground(Color.LIGHT_GRAY);
                    c.setForeground(Color.DARK_GRAY);
                } else if (p.getStockActual() <= p.getStockMinimo()) {
                    c.setBackground(new Color(255, 200, 200)); // Rojo
                    c.setForeground(Color.RED.darker());
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setBackground(Color.WHITE);
            }

            // Mantenemos el azul de selección
            if (isSelected) {
                c.setBackground(new Color(184, 207, 229));
                c.setForeground(Color.BLACK);
            }

        } catch (Exception e) {
            c.setBackground(Color.WHITE);
        }

        return c;
    }
}