package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.TipoMantenimiento;
import com.uber.model.Mantenimiento;
import com.uber.model.Tiene;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TieneDAO {

    private static final String SELECT_ALL = "SELECT * FROM Tiene";

    private static final String SELECT_BY_VEHICULO = "SELECT t.*, m.tipo, m.descripcion " +
                    "FROM Tiene t INNER JOIN Mantenimiento m ON t.id_mantenimiento = m.id_mantenimiento " +
                    "WHERE t.id_vehiculo = ?";

    private static final String INSERT = "INSERT INTO Tiene (id_vehiculo, id_mantenimiento, fecha_hora, coste, notas) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE Tiene SET fecha_hora = ?, coste = ?, notas = ? " +
                    "WHERE id_vehiculo = ? AND id_mantenimiento = ?";

    private static final String DELETE = "DELETE FROM Tiene WHERE id_vehiculo = ? AND id_mantenimiento = ?";

    private static final String COUNT_MANTENIMIENTOS = "SELECT COUNT(*) AS total FROM Tiene WHERE id_vehiculo = ?";

    private static final String SELECT_ULTIMO_MANTENIMIENTO = "SELECT * FROM Tiene WHERE id_vehiculo = ? ORDER BY fecha_hora DESC LIMIT 1";

    // ================================================================
    private final Connection conn;

    public TieneDAO() {
        conn = ConnectionBD.getConnection();
    }

    // ================================================================
    // MAPEO
    // ================================================================
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

    // ================================================================
    // SELECT ALL
    // ================================================================
    public List<Tiene> getAll() {
        List<Tiene> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ================================================================
    // HISTORIAL VEHÍCULO (JOIN con Mantenimiento)
    // ================================================================
    public List<Tiene> getHistorialVehiculo(int idVehiculo) {
        List<Tiene> historial = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_VEHICULO)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tiene t = map(rs);

                // COMPLETAR LOS DATOS DEL MANTENIMIENTO
                t.getMantenimiento().setTipo(
                        TipoMantenimiento.valueOf(rs.getString("tipo"))
                );
                t.getMantenimiento().setDescripcion(rs.getString("descripcion"));

                historial.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historial;
    }

    // ================================================================
    // INSERT
    // ================================================================
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

    // ================================================================
    // UPDATE
    // ================================================================
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

    // ================================================================
    // DELETE
    // ================================================================
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

    // ================================================================
    // CONSULTAS AVANZADAS (para subir nota)
    // ================================================================

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

    public Tiene getUltimoMantenimiento(int idVehiculo) {

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ULTIMO_MANTENIMIENTO)) {

            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}