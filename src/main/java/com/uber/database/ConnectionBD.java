package com.uber.database;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBD {

    private static Connection connection = null;

    private ConnectionBD() {}

    public static Connection getConnection() {

        if (connection == null) {
            try {

                // ---------------------------------------------
                // 🔥 AHORA USAMOS LA BD EMBEBIDA H2
                // ---------------------------------------------
                connection = ConnectionBDH2.getConnection();
                System.out.println("✔ Conexión establecida con H2");

            } catch (Exception e) {
                System.err.println("Error de conexión a H2: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✔ Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión");
        }
    }
}
/*
package com.uber.database;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBD {

    private static Connection connection = null;

    private ConnectionBD() {}

    public static Connection getConnection() {

        if (connection == null) {
            try {

                // ---------------------------------------------
                // 🔥 AHORA USAMOS LA BD EMBEBIDA H2
                // ---------------------------------------------
                connection = ConnectionBDH2.getConnection();
                System.out.println("✔ Conexión establecida con H2");

            } catch (Exception e) {
                System.err.println("Error de conexión a H2: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✔ Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión");
        }
    }
}*/
