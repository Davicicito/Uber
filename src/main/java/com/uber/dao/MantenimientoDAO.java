package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.TipoMantenimiento;
import com.uber.model.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar los registros de la tabla Mantenimiento.
 * Permite realizar operaciones CRUD sobre los tipos de mantenimiento.
 */
public class MantenimientoDAO {

    // Consultas SQL actualizadas
    private static final String SELECT_ALL = "SELECT * FROM Mantenimiento";
    private static final String SELECT_BY_ID = "SELECT * FROM Mantenimiento WHERE id_mantenimiento = ?";
    private static final String INSERT = "INSERT INTO Mantenimiento (tipo, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE Mantenimiento SET tipo = ?, descripcion = ? WHERE id_mantenimiento = ?";
    private static final String DELETE = "DELETE FROM Mantenimiento WHERE id_mantenimiento = ?";

    private final Connection conn;

    /**
     * Constructor que establece la conexión con la base de datos.
     */
    public MantenimientoDAO() {
        conn = ConnectionBD.getConnection();
    }

    // ================================================================
    //   MÉTODOS AUXILIARES
    // ================================================================

    /**
     * Convierte un registro del ResultSet en un objeto Mantenimiento.
     *
     * @param rs ResultSet con los datos obtenidos de la BD.
     * @return Un objeto Mantenimiento con los datos del registro.
     * @throws SQLException Si ocurre un problema al leer las columnas.
     */
    private Mantenimiento mapMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento m = new Mantenimiento();

        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));

        // Convertimos el valor String de la BD al Enum correspondiente
        String tipoStr = rs.getString("tipo");
        try {
            m.setTipo(TipoMantenimiento.valueOf(tipoStr));
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ Tipo de mantenimiento desconocido: " + tipoStr);
            m.setTipo(null);
        }

        m.setDescripcion(rs.getString("descripcion"));

        return m;
    }

    // ================================================================
    //   OPERACIONES CRUD
    // ================================================================

    /**
     * Obtiene todos los tipos de mantenimiento almacenados en la BD.
     *
     * @return Lista de objetos Mantenimiento.
     */
    public List<Mantenimiento> getAll() {
        List<Mantenimiento> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(mapMantenimiento(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener mantenimientos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Busca un registro de mantenimiento por su ID.
     *
     * @param id Identificador único del mantenimiento.
     * @return El objeto Mantenimiento correspondiente, o null si no existe.
     */
    public Mantenimiento getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapMantenimiento(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar mantenimiento por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Inserta un nuevo mantenimiento en la BD.
     *
     * @param m Objeto Mantenimiento con los datos a insertar.
     * @return true si se insertó correctamente, false en caso contrario.
     */
    public boolean insert(Mantenimiento m) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {

            ps.setString(1, m.getTipo().name());
            ps.setString(2, m.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar mantenimiento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un mantenimiento existente.
     *
     * @param m Objeto Mantenimiento con la información actualizada.
     * @return true si se actualizó correctamente, false si hubo error.
     */
    public boolean update(Mantenimiento m) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, m.getTipo().name());
            ps.setString(2, m.getDescripcion());
            ps.setInt(3, m.getIdMantenimiento());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar mantenimiento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de mantenimiento según su ID.
     *
     * @param id ID del mantenimiento a eliminar.
     * @return true si se eliminó correctamente, false si falló.
     */
    public boolean delete(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar mantenimiento: " + e.getMessage());
            return false;
        }
    }
}
