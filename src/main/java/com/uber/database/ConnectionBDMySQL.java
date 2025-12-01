package com.uber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBDMySQL {

    /**
     * Abre una conexión a una base de datos MySQL usando los datos proporcionados.
     * Se carga el driver manualmente para evitar problemas en algunos entornos.
     *
     * @param url      URL completa de la BD (jdbc:mysql://...)
     * @param user     usuario de MySQL
     * @param password contraseña del usuario
     * @return conexión abierta o null si falla
     */
    public static Connection getConnection(String url, String user, String password) {
        Connection conn = null;

        try {
            // Cargar el driver manualmente (necesario en algunos servidores)
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado a MySQL (XAMPP)");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error conectando a MySQL: " + e.getMessage());
            e.printStackTrace();
        }

        return conn;
    }
}
