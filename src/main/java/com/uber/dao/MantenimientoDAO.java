package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.TipoMantenimiento;
import com.uber.model.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    // IMPORTANTE: Usamos nombre_mantenimiento (tu tabla H2)
    private static final String SELECT_ALL = "SELECT * FROM Mantenimiento";
    private static final String SELECT_BY_ID = "SELECT * FROM Mantenimiento WHERE id_mantenimiento = ?";
    private static final String INSERT = "INSERT INTO Mantenimiento (nombre_mantenimiento, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE Mantenimiento SET nombre_mantenimiento = ?, descripcion = ? WHERE id_mantenimiento = ?";
    private static final String DELETE = "DELETE FROM Mantenimiento WHERE id_mantenimiento = ?";

    private final Connection conn;

    public MantenimientoDAO() {
        conn = ConnectionBD.getConnection();
    }

    // ================================================================
    //   Mapear Mantenimiento (conversión desde BD a objeto Java)
    // ================================================================
    private Mantenimiento mapMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento m = new Mantenimiento();

        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));

        // ESTA ES LA ÚNICA FORMA CORRECTA
        String tipoStr = rs.getString("nombre_mantenimiento");
        m.setTipo(TipoMantenimiento.valueOf(tipoStr));

        m.setDescripcion(rs.getString("descripcion"));

        return m;
    }



    // ================================================================
    //   SELECT ALL
    // ================================================================
    public List<Mantenimiento> getAll() {
        List<Mantenimiento> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(mapMantenimiento(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ================================================================
    //   SELECT BY ID
    // ================================================================
    public Mantenimiento getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapMantenimiento(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================================================
    //   INSERT
    // ================================================================
    public boolean insert(Mantenimiento m) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {

            ps.setString(1, m.getTipo().name());  // Guardamos el enum como texto
            ps.setString(2, m.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    //   UPDATE
    // ================================================================
    public boolean update(Mantenimiento m) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, m.getTipo().name());
            ps.setString(2, m.getDescripcion());
            ps.setInt(3, m.getIdMantenimiento());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    //   DELETE
    // ================================================================
    public boolean delete(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
