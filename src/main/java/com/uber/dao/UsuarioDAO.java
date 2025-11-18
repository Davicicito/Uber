package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.model.Usuario;
import com.uber.enums.EstadoCuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {


    private Connection conn;

    public UsuarioDAO() {
        conn = ConnectionBD.getConnection();
    }

    // -------------------------------------------------------------------------
    // Obtener lista completa de usuarios (prueba ideal de conexión)
    // -------------------------------------------------------------------------
    public List<Usuario> getAllUsuarios() {
        List<Usuario> lista = new ArrayList<>();

        String query = "SELECT * FROM Usuario";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setEmail(rs.getString("email"));
                u.setContrasena(rs.getString("contrasena"));
                u.setTelefono(rs.getString("telefono"));
                u.setMetodoPago(rs.getString("metodo_pago"));
                u.setSaldo(rs.getDouble("saldo"));

                // Convertimos el String del enum en enum real
                u.setEstadoCuenta(
                        EstadoCuenta.valueOf(rs.getString("estado_cuenta"))
                );

                lista.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // -------------------------------------------------------------------------
    // Buscar usuario por id
    // -------------------------------------------------------------------------
    public Usuario getUsuarioById(int id) {
        Usuario u = null;

        String query = "SELECT * FROM Usuario WHERE id_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setEmail(rs.getString("email"));
                u.setContrasena(rs.getString("contrasena"));
                u.setTelefono(rs.getString("telefono"));
                u.setMetodoPago(rs.getString("metodo_pago"));
                u.setSaldo(rs.getDouble("saldo"));
                u.setEstadoCuenta(
                        EstadoCuenta.valueOf(rs.getString("estado_cuenta"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return u;
    }

    // -------------------------------------------------------------------------
    // Insertar usuario (para probar INSERT REAL)
    // -------------------------------------------------------------------------
    public boolean insertarUsuario(Usuario u) {
        String query = """
                INSERT INTO Usuario (nombre, apellidos, email, contrasena, telefono, metodo_pago, saldo, estado_cuenta)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getMetodoPago());
            ps.setDouble(7, u.getSaldo());
            ps.setString(8, u.getEstadoCuenta().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}