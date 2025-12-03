package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.Rol;
import com.uber.model.Usuario;
import com.uber.enums.EstadoCuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String SELECT_ALL = "SELECT * FROM Usuario";
    private static final String SELECT_BY_ID = "SELECT * FROM Usuario WHERE id_usuario = ?";

    private static final String INSERT =
            "INSERT INTO Usuario (nombre, apellidos, email, contrasena, telefono, metodo_pago, saldo, estado_cuenta, rol) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE Usuario SET nombre = ?, apellidos = ?, email = ?, contrasena = ?, telefono = ?, " +
                    "metodo_pago = ?, saldo = ?, estado_cuenta = ?, rol = ? WHERE id_usuario = ?";

    private static final String DELETE = "DELETE FROM Usuario WHERE id_usuario = ?";

    private static final String SELECT_USUARIO_RESERVAS =
            "SELECT u.*, r.id_reserva, r.fecha_hora_inicio, r.estado " +
                    "FROM Usuario u LEFT JOIN Reserva r ON u.id_usuario = r.id_usuario WHERE u.id_usuario = ?";

    private static final String LOGIN =
            "SELECT * FROM Usuario WHERE email = ? AND contrasena = ?";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM Usuario WHERE email = ?";

    private static final String UPDATE_SALDO =
            "UPDATE Usuario SET saldo = ? WHERE id_usuario = ?";

    private Connection conn;

    public UsuarioDAO() {
        conn = ConnectionBD.getConnection();
    }

    /**
     * Convierte una fila del ResultSet en un objeto Usuario.
     * @param rs fila obtenida de la consulta SQL
     * @return usuario con los datos cargados
     * @throws SQLException si ocurre un error leyendo los datos
     */
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();

        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setEmail(rs.getString("email"));
        u.setContrasena(rs.getString("contrasena"));
        u.setTelefono(rs.getString("telefono"));
        u.setMetodoPago(rs.getString("metodo_pago"));
        u.setSaldo(rs.getDouble("saldo"));
        u.setEstadoCuenta(EstadoCuenta.valueOf(rs.getString("estado_cuenta")));
        u.setRol(Rol.valueOf(rs.getString("rol")));

        return u;
    }

    /**
     * Devuelve todos los usuarios registrados.
     * @return lista completa de usuarios
     */
    public List<Usuario> getAll() {
        List<Usuario> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) lista.add(mapUsuario(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Busca un usuario por su ID.
     * @param id id del usuario
     * @return usuario encontrado o null si no existe
     */
    public Usuario getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapUsuario(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param u usuario a registrar
     * @return true si se insertó correctamente
     */
    public boolean insert(Usuario u) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getMetodoPago());
            ps.setDouble(7, u.getSaldo());
            ps.setString(8, u.getEstadoCuenta().name());
            ps.setString(9, u.getRol().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * @param u objeto usuario con la información editada
     * @return true si se actualizó correctamente
     */
    public boolean update(Usuario u) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getMetodoPago());
            ps.setDouble(7, u.getSaldo());
            ps.setString(8, u.getEstadoCuenta().name());
            ps.setString(9, u.getRol().name());
            ps.setInt(10, u.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un usuario por su ID.
     * @param id usuario a borrar
     * @return true si se borró con éxito
     */
    public boolean delete(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Muestra por consola un usuario junto con sus reservas.
     * Se usa para la parte de consultas avanzadas de la rúbrica.
     * @param idUsuario id del usuario
     */
    public void mostrarUsuarioConReservas(int idUsuario) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_USUARIO_RESERVAS)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Usuario y sus reservas ---");

            while (rs.next()) {
                System.out.println("Usuario: " + rs.getString("nombre") + " " + rs.getString("apellidos"));
                System.out.println("Reserva: " + rs.getInt("id_reserva") +
                        " | Inicio: " + rs.getString("fecha_hora_inicio") +
                        " | Estado: " + rs.getString("estado"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comprueba si existe un usuario con ese email y contraseña.
     * @param email correo introducido
     * @param pass contraseña escrita
     * @return usuario si es correcto, null si falla el login
     */
    public Usuario login(String email, String pass) {
        try (PreparedStatement ps = conn.prepareStatement(LOGIN)) {

            ps.setString(1, email);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapUsuario(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Comprueba si un email ya está registrado.
     * @param email correo a comprobar
     * @return true si ya existe en la base de datos
     */
    public boolean emailExiste(String email) {
        try (PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza el saldo de un usuario.
     * @param idUsuario id del usuario
     * @param nuevoSaldo saldo final calculado
     * @return true si se guardó correctamente
     */
    public boolean actualizarSaldo(int idUsuario, double nuevoSaldo) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SALDO)) {

            ps.setDouble(1, nuevoSaldo);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
