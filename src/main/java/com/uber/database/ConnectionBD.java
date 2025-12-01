package com.uber.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionBD {

    // Conexión única reutilizable
    private static Connection connection = null;

    private ConnectionBD() {}

    /**
     * Devuelve la conexión activa a la base de datos.
     * Si no existe o está cerrada, se crea una nueva.
     *
     * @return conexión a la base de datos
     */
    public static Connection getConnection() {
        try {
            // Si la conexión no existe o está cerrada, volvemos a abrirla
            if (connection == null || connection.isClosed()) {

                Properties props = loadProperties();
                String type = props.getProperty("db.type");

                // Si en db.properties pone "mysql", abrimos MySQL
                if ("mysql".equalsIgnoreCase(type)) {

                    connection = ConnectionBDMySQL.getConnection(
                            props.getProperty("mysql.url"),
                            props.getProperty("mysql.user"),
                            props.getProperty("mysql.password")
                    );

                } else {
                    // Cualquier otro valor carga la BD interna H2
                    connection = ConnectionBDH2.getConnection();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Carga el archivo db.properties del directorio resources.
     * Si no se encuentra, por defecto se utilizará H2.
     *
     * @return propiedades con la configuración de la BD
     */
    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input =
                     ConnectionBD.class.getClassLoader().getResourceAsStream("db.properties")) {

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

    /**
     * Cierra la conexión actual y la deja en null.
     * Se usa al cerrar la aplicación.
     */
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

