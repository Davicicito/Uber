package com.uber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBDMySQL {

    public static Connection getConnection(String url, String user, String password) {
        Connection conn = null;
        try {
            // Cargar driver explícitamente para asegurar compatibilidad
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("🚀 Conectado a MySQL (XAMPP)");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Error conectando a MySQL: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}