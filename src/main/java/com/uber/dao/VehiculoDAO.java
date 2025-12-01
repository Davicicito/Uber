package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoVehiculo;
import com.uber.model.Estacion;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    private static final String SELECT_ALL = "SELECT v.*, e.id_estacion, e.nombre_estacion, e.direccion, e.capacidad, e.ciudad " +
            "FROM Vehiculo v INNER JOIN Estacion e ON v.id_estacion = e.id_estacion";

    private static final String SELECT_BY_ID = "SELECT v.*, e.id_estacion, e.nombre_estacion, e.direccion, e.capacidad, e.ciudad " +
            "FROM Vehiculo v INNER JOIN Estacion e ON v.id_estacion = e.id_estacion " +
            "WHERE v.id_vehiculo = ?";

    // ✅ CORREGIDO: Añadidos nivel_bateria y kilometraje al INSERT
    private static final String INSERT = "INSERT INTO Vehiculo (tipo, marca, modelo, estado_vehiculo, id_estacion, nivel_bateria, kilometraje) VALUES (?, ?, ?, ?, ?, ?, ?)";

    // ✅ CORREGIDO: Añadidos nivel_bateria y kilometraje al UPDATE
    private static final String UPDATE = "UPDATE Vehiculo SET tipo = ?, marca = ?, modelo = ?, estado_vehiculo = ?, id_estacion = ?, nivel_bateria = ?, kilometraje = ? " +
            "WHERE id_vehiculo = ?";

    private static final String DELETE = "DELETE FROM Vehiculo WHERE id_vehiculo = ?";

    private static final String SELECT_VEHICULO_RESERVAS = "SELECT v.id_vehiculo, r.id_reserva, r.fecha_hora_inicio, r.fecha_hora_fin, r.estado " +
            "FROM Vehiculo v LEFT JOIN Reserva r ON v.id_vehiculo = r.id_vehiculo " +
            "WHERE v.id_vehiculo = ?";

    private final Connection conn;

    public VehiculoDAO() {
        this.conn = ConnectionBD.getConnection();
    }

    private Estacion mapEstacion(ResultSet rs) throws SQLException {
        Estacion e = new Estacion();
        e.setIdEstacion(rs.getInt("id_estacion"));
        e.setNombreEstacion(rs.getString("nombre_estacion"));
        e.setDireccion(rs.getString("direccion"));
        e.setCapacidad(rs.getInt("capacidad"));
        e.setCiudad(rs.getString("ciudad"));
        return e;
    }

    private Vehiculo mapVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();

        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setTipo(TipoVehiculo.valueOf(rs.getString("tipo")));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setEstadoVehiculo(EstadoVehiculo.valueOf(rs.getString("estado_vehiculo")));
        v.setNivelBateria(rs.getDouble("nivel_bateria"));
        v.setKilometraje(rs.getDouble("kilometraje"));

        v.setEstacion(mapEstacion(rs));

        return v;
    }

    public List<Vehiculo> getAll() {
        List<Vehiculo> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                lista.add(mapVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Vehiculo getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapVehiculo(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Vehiculo v) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setString(1, v.getTipo().name());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getEstadoVehiculo().name());
            ps.setInt(5, v.getEstacion().getIdEstacion());
            ps.setDouble(6, v.getNivelBateria());
            ps.setDouble(7, v.getKilometraje());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Vehiculo v) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, v.getTipo().name());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getEstadoVehiculo().name());
            ps.setInt(5, v.getEstacion().getIdEstacion());
            ps.setDouble(6, v.getNivelBateria());
            ps.setDouble(7, v.getKilometraje());
            ps.setInt(8, v.getIdVehiculo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void mostrarReservasDelVehiculo(int idVehiculo) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_VEHICULO_RESERVAS)) {
            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Reservas del Vehículo ---");
            while (rs.next()) {
                int idReserva = rs.getInt("id_reserva");
                if (idReserva == 0) {
                    System.out.println("Este vehículo no tiene reservas.");
                    return;
                }
                System.out.println("Reserva: " + idReserva +
                        " | Inicio: " + rs.getString("fecha_hora_inicio") +
                        " | Fin: " + rs.getString("fecha_hora_fin") +
                        " | Estado: " + rs.getString("estado"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}