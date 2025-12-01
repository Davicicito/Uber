package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.TipoMantenimiento;
import com.uber.model.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar las operaciones CRUD de la tabla 'Mantenimiento'.
 * Permite crear, leer, actualizar y borrar tipos de mantenimiento.
 */
public class MantenimientoDAO {

    // Consultas SQL
    private static final String SELECT_ALL = "SELECT * FROM Mantenimiento";
    private static final String SELECT_BY_ID = "SELECT * FROM Mantenimiento WHERE id_mantenimiento = ?";
    private static final String INSERT = "INSERT INTO Mantenimiento (nombre_mantenimiento, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE Mantenimiento SET nombre_mantenimiento = ?, descripcion = ? WHERE id_mantenimiento = ?";
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
     * Convierte un resultado de la base de datos en un objeto Mantenimiento.
     * @param rs ResultSet con los datos.
     * @return Objeto Mantenimiento relleno.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private Mantenimiento mapMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento m = new Mantenimiento();

        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));

        // Convertimos el string de la BD al Enum correspondiente
        String tipoStr = rs.getString("nombre_mantenimiento");
        try {
            m.setTipo(TipoMantenimiento.valueOf(tipoStr));
        } catch (IllegalArgumentException e) {
            // Si el tipo no existe en el Enum, lo dejamos null o manejamos el error
            System.out.println("Tipo de mantenimiento desconocido: " + tipoStr);
        }

        m.setDescripcion(rs.getString("descripcion"));

        return m;
    }

    // ================================================================
    //   OPERACIONES CRUD
    // ================================================================

    /**
     * Obtiene todos los tipos de mantenimiento registrados.
     * @return Lista de mantenimientos.
     */
    public List<Mantenimiento> getAll() {
        List<Mantenimiento> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(mapMantenimiento(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar mantenimientos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Busca un mantenimiento por su ID.
     * @param id Identificador del mantenimiento.
     * @return El objeto Mantenimiento o null si no existe.
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
     * Guarda un nuevo tipo de mantenimiento en la base de datos.
     * @param m El objeto mantenimiento a guardar.
     * @return true si se guardó correctamente, false si falló.
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
     * @param m El objeto mantenimiento con los nuevos datos.
     * @return true si se actualizó correctamente.
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
     * Elimina un mantenimiento de la base de datos.
     * @param id El ID del mantenimiento a borrar.
     * @return true si se eliminó correctamente.
     */
    public boolean delete(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al borrar mantenimiento: " + e.getMessage());
            return false;
        }
    }
}