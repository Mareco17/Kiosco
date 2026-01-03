package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/kiosco_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Mareco17"; // <--- ¡No olvides poner tu clave!

    // Método estático para obtener la conexión
    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}