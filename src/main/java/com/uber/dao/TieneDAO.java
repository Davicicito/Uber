package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.TipoMantenimiento;
import com.uber.model.Mantenimiento;
import com.uber.model.Tiene;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TieneDAO {

    private static final String SELECT_ALL = "SELECT * FROM Tiene";

    private static final String SELECT_BY_VEHICULO =
            "SELECT t.*, m.nombre_mantenimiento, m.descripcion " +
                    "FROM Tiene t INNER JOIN Mantenimiento m ON t.id_mantenimiento = m.id_mantenimiento " +
                    "WHERE t.id_vehiculo = ?";

    private static final String INSERT =
            "INSERT INTO Tiene (id_vehiculo, id_mantenimiento, fecha_hora, coste, notas) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE Tiene SET fecha_hora = ?, coste = ?, notas = ? WHERE id_vehiculo = ? AND id_mantenimiento = ?";

    private static final String DELETE =
            "DELETE FROM Tiene WHERE id_vehiculo = ? AND id_mantenimiento = ?";

    private static final String COUNT_MANTENIMIENTOS =
            "SELECT COUNT(*) AS total FROM Tiene WHERE id_vehiculo = ?";

    private static final String SELECT_ULTIMO_MANTENIMIENTO =
            "SELECT t.*, m.nombre_mantenimiento, m.descripcion " +
                    "FROM Tiene t INNER JOIN Mantenimiento m ON t.id_mantenimiento = m.id_mantenimiento " +
                    "WHERE t.id_vehiculo = ? ORDER BY t.fecha_hora DESC LIMIT 1";

    private final Connection conn;

    public TieneDAO() {
        conn = ConnectionBD.getConnection();
    }

    /**
     * Convierte una fila del ResultSet en un objeto Tiene.
     * Solo mapea las columnas principales; los datos extra del mantenimiento
     * se añaden en los métodos que hacen JOIN.
     *
     * @param rs Resultado de la consulta
     * @return objeto Tiene con datos cargados
     * @throws SQLException si ocurre un error leyendo datos
     */
    private Tiene map(ResultSet rs) throws SQLException {
        Tiene t = new Tiene();

        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        t.setVehiculo(v);

        Mantenimiento m = new Mantenimiento();
        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
        t.setMantenimiento(m);

        Timestamp ts = rs.getTimestamp("fecha_hora");
        t.setFechaHora(ts != null ? ts.toLocalDateTime() : null);

        t.setCoste(rs.getDouble("coste"));
        t.setNotas(rs.getString("notas"));

        return t;
    }

    /**
     * Devuelve todos los registros de la tabla Tiene.
     *
     * @return lista completa de mantenimientos aplicados
     */
    public List<Tiene> getAll() {
        List<Tiene> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene el historial completo de un vehículo,
     * incluyendo el nombre y la descripción del mantenimiento (JOIN).
     *
     * @param idVehiculo id del vehículo
     * @return lista de mantenimientos que ha tenido
     */
    public List<Tiene> getHistorialVehiculo(int idVehiculo) {
        List<Tiene> historial = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_VEHICULO)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tiene t = map(rs);

                String nombreMant = rs.getString("nombre_mantenimiento");
                if (nombreMant != null) {
                    t.getMantenimiento().setTipo(TipoMantenimiento.valueOf(nombreMant));
                }

                t.getMantenimiento().setDescripcion(rs.getString("descripcion"));

                historial.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historial;
    }

    /**
     * Inserta un registro en la tabla Tiene.
     *
     * @param t objeto con los datos a guardar
     * @return true si se insertó correctamente
     */
    public boolean insert(Tiene t) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {

            ps.setInt(1, t.getVehiculo().getIdVehiculo());
            ps.setInt(2, t.getMantenimiento().getIdMantenimiento());
            ps.setTimestamp(3, Timestamp.valueOf(t.getFechaHora()));
            ps.setDouble(4, t.getCoste());
            ps.setString(5, t.getNotas());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un registro de la tabla Tiene.
     *
     * @param t objeto con la información editada
     * @return true si se actualizó bien
     */
    public boolean update(Tiene t) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setTimestamp(1, Timestamp.valueOf(t.getFechaHora()));
            ps.setDouble(2, t.getCoste());
            ps.setString(3, t.getNotas());
            ps.setInt(4, t.getVehiculo().getIdVehiculo());
            ps.setInt(5, t.getMantenimiento().getIdMantenimiento());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un mantenimiento concreto aplicado a un vehículo.
     *
     * @param idVehiculo id del vehículo
     * @param idMantenimiento id del mantenimiento
     * @return true si se borró correctamente
     */
    public boolean delete(int idVehiculo, int idMantenimiento) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, idVehiculo);
            ps.setInt(2, idMantenimiento);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Devuelve cuántos mantenimientos tiene registrados un vehículo.
     *
     * @param idVehiculo id del vehículo
     * @return número total de mantenimientos
     */
    public int contarMantenimientos(int idVehiculo) {
        try (PreparedStatement ps = conn.prepareStatement(COUNT_MANTENIMIENTOS)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Obtiene el último mantenimiento realizado a un vehículo.
     * Incluye datos del mantenimiento gracias al JOIN.
     *
     * @param idVehiculo id del vehículo
     * @return objeto Tiene con el último mantenimiento o null si no tiene
     */
    public Tiene getUltimoMantenimiento(int idVehiculo) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ULTIMO_MANTENIMIENTO)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Tiene t = map(rs);

                String nombreMant = rs.getString("nombre_mantenimiento");
                if (nombreMant != null) {
                    t.getMantenimiento().setTipo(TipoMantenimiento.valueOf(nombreMant));
                }

                t.getMantenimiento().setDescripcion(rs.getString("descripcion"));

                return t;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
