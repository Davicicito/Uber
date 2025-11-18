package com.uber.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBD {

    private static final String FILE = "connection.xml";
    private static Connection connection = null;

    private ConnectionBD() {}

    public static Connection getConnection() {

        if (connection == null) {
            try {
                XMLManager xmlManager = new XMLManager();

                ConnectionProperties properties =
                        xmlManager.readXML(new ConnectionProperties(), FILE);

                connection = DriverManager.getConnection(
                        properties.getURL(),
                        properties.getUser(),
                        properties.getPassword()
                );

                System.out.println("✔ Conexión establecida con la base de datos Uber");

            } catch (SQLException e) {
                System.err.println("Error de conexión: " + e.getMessage());
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
