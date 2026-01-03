package org.example;

import org.example.view.PantallaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Esto asegura que la ventana se inicie en el hilo correcto de gráficos
        SwingUtilities.invokeLater(() -> {
            PantallaPrincipal pantalla = new PantallaPrincipal();
            pantalla.setVisible(true);
        });
    }
}