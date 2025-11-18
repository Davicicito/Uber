package com.uber.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String server;
    private String port;
    private String database;
    private String user;
    private String password;

    public ConnectionProperties() {}

    public ConnectionProperties(String server, String port, String database, String user, String password) {
        this.server = server;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getUser() { return user; }
    public String getPassword() { return password; }

    public String getURL() {
        return "jdbc:mysql://" + server + ":" + port + "/" + database
                + "?useSSL=false&serverTimezone=UTC";
    }
}
