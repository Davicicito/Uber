package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.model.Estacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionDAO {
    private static final String SELECT_ALL = "SELECT * FROM Estacion";

    private static final String SELECT_BY_ID = "SELECT * FROM Estacion WHERE id_estacion = ?";

    private static final String INSERT = "INSERT INTO Estacion (nombre_estacion, direccion, capacidad, ciudad) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE Estacion SET nombre_estacion = ?, direccion = ?, capacidad = ?, ciudad = ? " +
                    "WHERE id_estacion = ?";

    private static final String DELETE = "DELETE FROM Estacion WHERE id_estacion = ?";

    // JOIN: Mostrar cantidad de vehículos por estación
    private static final String SELECT_ESTACION_CON_VEHICULOS = "SELECT e.id_estacion, e.nombre_estacion, COUNT(v.id_vehiculo) AS totalVehiculos " +
                    "FROM Estacion e LEFT JOIN Vehiculo v ON e.id_estacion = v.id_estacion " +
                    "WHERE e.id_estacion = ? GROUP BY e.id_estacion";

    // ================================================================
    private final Connection conn;

    public EstacionDAO() {
        this.conn = ConnectionBD.getConnection();
    }

    // ================================================================
    //   Método auxiliar: convertir ResultSet → Estacion
    // ================================================================
    private Estacion mapEstacion(ResultSet rs) throws SQLException {
        Estacion e = new Estacion();

        e.setIdEstacion(rs.getInt("id_estacion"));
        e.setNombreEstacion(rs.getString("nombre_estacion"));
        e.setDireccion(rs.getString("direccion"));
        e.setCapacidad(rs.getInt("capacidad"));
        e.setCiudad(rs.getString("ciudad"));

        return e;
    }

    // ================================================================
    //   SELECT ALL
    // ================================================================
    public List<Estacion> getAll() {
        List<Estacion> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(mapEstacion(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ================================================================
    //   SELECT BY ID
    // ================================================================
    public Estacion getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapEstacion(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================================================
    //   INSERT
    // ================================================================
    public boolean insert(Estacion e) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {

            ps.setString(1, e.getNombreEstacion());
            ps.setString(2, e.getDireccion());
            ps.setInt(3, e.getCapacidad());
            ps.setString(4, e.getCiudad());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ================================================================
    //   UPDATE
    // ================================================================
    public boolean update(Estacion e) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, e.getNombreEstacion());
            ps.setString(2, e.getDireccion());
            ps.setInt(3, e.getCapacidad());
            ps.setString(4, e.getCiudad());
            ps.setInt(5, e.getIdEstacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
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

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ================================================================
    //   CONSULTA AVANZADA (JOIN)
    // ================================================================
    public void mostrarEstacionConCantidadVehiculos(int idEstacion) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ESTACION_CON_VEHICULOS)) {

            ps.setInt(1, idEstacion);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Estación: " + rs.getString("nombre_estacion"));
                System.out.println("Vehículos en esta estación: " + rs.getInt("totalVehiculos"));
            } else {
                System.out.println("No existe la estación con ID " + idEstacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}