package com.uber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBDH2 {

    // Datos de conexión a la BD H2 en modo archivo local
    private static final String URL = "jdbc:h2:./bd/uber;AUTO_SERVER=TRUE;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection conn;

    /**
     * Devuelve la conexión a la base de datos H2.
     * Si no existe, se crea una nueva conexión.
     *
     * @return conexión activa a la BD H2
     */
    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conectado a H2 (embebido)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}