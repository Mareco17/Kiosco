package org.example.view;

import org.example.dao.CajaDAO;
import org.example.model.MovimientoCaja;

import javax.swing.*;
import java.awt.*;

public class PantallaCaja extends JFrame {

    private JLabel lblSaldo;
    private JTextField txtMonto, txtDescripcion;
    private CajaDAO cajaDAO;

    public PantallaCaja() {
        cajaDAO = new CajaDAO();

        setTitle("Control de Caja");
        setSize(400, 350);
        setLayout(new GridLayout(6, 1, 10, 10)); // 6 filas
        setLocationRelativeTo(null);

        // 1. Mostrar Saldo Actual (Gigante)
        lblSaldo = new JLabel("Calculando...");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 30));
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSaldo.setForeground(new Color(0, 100, 0)); // Verde oscuro
        add(lblSaldo);

        // 2. Campos para registrar movimientos
        add(new JLabel("  Monto ($):"));
        txtMonto = new JTextField();
        add(txtMonto);

        add(new JLabel("  Descripción (Ej: Pago Luz, Cambio inicial):"));
        txtDescripcion = new JTextField();
        add(txtDescripcion);

        // 3. Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnIngreso = new JButton("Ingresar Dinero (+)");
        btnIngreso.setBackground(Color.GREEN);

        JButton btnEgreso = new JButton("Retirar Dinero (-)");
        btnEgreso.setBackground(Color.RED);
        btnEgreso.setForeground(Color.WHITE);

        panelBotones.add(btnIngreso);
        panelBotones.add(btnEgreso);
        add(panelBotones);

        // Acciones
        btnIngreso.addActionListener(e -> registrarMovimiento("INGRESO"));
        btnEgreso.addActionListener(e -> registrarMovimiento("EGRESO"));

        // Cargar saldo al iniciar
        actualizarSaldo();
    }

    private void actualizarSaldo() {
        double saldo = cajaDAO.obtenerSaldoTotal();
        lblSaldo.setText("$ " + saldo);
    }

    private void registrarMovimiento(String tipo) {
        try {
            double monto = Double.parseDouble(txtMonto.getText());
            String descripcion = txtDescripcion.getText();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Escribe una descripción.");
                return;
            }

            // Crear objeto y guardar
            MovimientoCaja mov = new MovimientoCaja(monto, tipo, descripcion);
            cajaDAO.registrarMovimiento(mov);

            // Avisar y limpiar
            JOptionPane.showMessageDialog(this, "Movimiento registrado: " + tipo);
            txtMonto.setText("");
            txtDescripcion.setText("");

            // Actualizar el número gigante
            actualizarSaldo();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número.");
        }
    }
}