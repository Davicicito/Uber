package com.uber.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Clase que representa los datos necesarios para conectarse a MySQL.
 * Se usa cuando la configuración viene desde un XML.
 */
@XmlRootElement(name="connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    // Parámetros básicos de conexión
    private String server;
    private String port;
    private String database;
    private String user;
    private String password;

    /**
     * Constructor vacío necesario para la carga con JAXB.
     */
    public ConnectionProperties() {}

    /**
     * Constructor para inicializar todas las propiedades de conexión.
     *
     * @param server   IP o nombre del servidor MySQL
     * @param port     puerto de MySQL
     * @param database nombre de la base de datos
     * @param user     usuario con permisos
     * @param password contraseña del usuario
     */
    public ConnectionProperties(String server, String port, String database, String user, String password) {
        this.server = server;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getUser() { return user; }

    public String getPassword() { return password; }

    /**
     * Genera la URL JDBC completa para conectar a MySQL.
     *
     * @return cadena URL lista para usar en DriverManager.getConnection(...)
     */
    public String getURL() {
        return "jdbc:mysql://" + server + ":" + port + "/" + database
                + "?useSSL=false&serverTimezone=UTC";
    }
}
