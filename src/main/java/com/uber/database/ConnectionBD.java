package com.uber.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionBD {

    private static Connection connection = null;

    private ConnectionBD() {}

    public static Connection getConnection() {
        // Si la conexión está cerrada o es nula, la abrimos de nuevo
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = loadProperties();
                String type = props.getProperty("db.type");

                if ("mysql".equalsIgnoreCase(type)) {
                    connection = ConnectionBDMySQL.getConnection(
                            props.getProperty("mysql.url"),
                            props.getProperty("mysql.user"),
                            props.getProperty("mysql.password")
                    );
                } else {
                    // Por defecto o si pone 'h2', usamos la interna
                    connection = ConnectionBDH2.getConnection();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ConnectionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("❌ No se encuentra db.properties, usando H2 por defecto.");
                props.setProperty("db.type", "h2");
                return props;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("🔌 Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
