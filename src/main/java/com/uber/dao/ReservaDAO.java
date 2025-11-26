package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.EstadoReserva;
import com.uber.model.Reserva;
import com.uber.model.Usuario;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String SELECT_ALL = "SELECT * FROM Reserva";

    private static final String SELECT_BY_ID = "SELECT * FROM Reserva WHERE id_reserva = ?";

    private static final String INSERT = "INSERT INTO Reserva (id_usuario, id_vehiculo, fecha_hora_inicio, fecha_hora_fin, coste, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE Reserva SET id_usuario = ?, id_vehiculo = ?, fecha_hora_inicio = ?, fecha_hora_fin = ?, coste = ?, estado = ? " +
            "WHERE id_reserva = ?";

    private static final String DELETE = "DELETE FROM Reserva WHERE id_reserva = ?";

    // JOIN con Usuario y Vehiculo
    private static final String SELECT_RESERVA_COMPLETA = "SELECT r.*, u.nombre AS usuarioNombre, v.marca AS vehiculoMarca " +
            "FROM Reserva r " +
            "JOIN Usuario u ON r.id_usuario = u.id_usuario " +
            "JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
            "WHERE r.id_reserva = ?";

    // Consulta para obtener reservas de un usuario específico
    private static final String SELECT_BY_USUARIO =
            "SELECT r.*, v.marca, v.modelo, v.tipo " +
                    "FROM Reserva r " +
                    "JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
                    "WHERE r.id_usuario = ? " +
                    "ORDER BY r.fecha_hora_inicio DESC";
    // ================================================================
    private final Connection conn;

    public ReservaDAO() {
        conn = ConnectionBD.getConnection();
    }

    // ================================================================
    // Mapear Reserva
    // ================================================================
    private Reserva mapReserva(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();

        r.setIdReserva(rs.getInt("id_reserva"));

        // ID de relaciones (luego DAOs externos pueden traer objetos completos)
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        r.setUsuario(usuario);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(rs.getInt("id_vehiculo"));
        r.setVehiculo(vehiculo);

        Timestamp inicio = rs.getTimestamp("fecha_hora_inicio");
        Timestamp fin = rs.getTimestamp("fecha_hora_fin");

        r.setFechaHoraInicio(inicio != null ? inicio.toLocalDateTime() : null);
        r.setFechaHoraFin(fin != null ? fin.toLocalDateTime() : null);

        r.setCoste(rs.getDouble("coste"));

        // Convertir String a Enum de forma segura
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            r.setEstado(EstadoReserva.valueOf(estadoStr));
        }

        return r;
    }

    // ================================================================
    // SELECT ALL
    // ================================================================
    public List<Reserva> getAll() {
        List<Reserva> lista = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                lista.add(mapReserva(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ================================================================
    // SELECT BY ID
    // ================================================================
    public Reserva getById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapReserva(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================================================================
    // INSERT (CON TRANSACCIÓN) -> AQUI ESTABA EL ERROR
    // ================================================================
    public boolean crearReserva(Reserva r) {

        try {
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. INSERT reserva
            PreparedStatement ps = conn.prepareStatement(INSERT);

            ps.setInt(1, r.getUsuario().getIdUsuario());
            ps.setInt(2, r.getVehiculo().getIdVehiculo());

            // Control de nulos para fechas
            if (r.getFechaHoraInicio() != null)
                ps.setTimestamp(3, Timestamp.valueOf(r.getFechaHoraInicio()));
            else
                ps.setNull(3, Types.TIMESTAMP);

            if (r.getFechaHoraFin() != null)
                ps.setTimestamp(4, Timestamp.valueOf(r.getFechaHoraFin()));
            else
                ps.setNull(4, Types.TIMESTAMP);

            ps.setDouble(5, r.getCoste());
            ps.setString(6, r.getEstado().name());

            ps.executeUpdate();

            // 2. ACTUALIZAR ESTADO VEHICULO
            // CORREGIDO: Cambiado 'OCUPADO' por 'EN_USO' que es el valor correcto en la BD
            String sqlUpdateVehiculo = "UPDATE Vehiculo SET estado_vehiculo = 'EN_USO' WHERE id_vehiculo = ?";

            PreparedStatement psVehiculo = conn.prepareStatement(sqlUpdateVehiculo);
            psVehiculo.setInt(1, r.getVehiculo().getIdVehiculo());
            psVehiculo.executeUpdate();

            conn.commit(); // FIN TRANSACCIÓN ÉXITO
            return true;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    // ================================================================
    // UPDATE
    // ================================================================
    public boolean update(Reserva r) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setInt(1, r.getUsuario().getIdUsuario());
            ps.setInt(2, r.getVehiculo().getIdVehiculo());

            if (r.getFechaHoraInicio() != null) ps.setTimestamp(3, Timestamp.valueOf(r.getFechaHoraInicio()));
            else ps.setNull(3, Types.TIMESTAMP);

            if (r.getFechaHoraFin() != null) ps.setTimestamp(4, Timestamp.valueOf(r.getFechaHoraFin()));
            else ps.setNull(4, Types.TIMESTAMP);

            ps.setDouble(5, r.getCoste());
            ps.setString(6, r.getEstado().name());
            ps.setInt(7, r.getIdReserva());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    // DELETE
    // ================================================================
    public boolean delete(int idReserva) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    // CONSULTA AVANZADA (JOIN)
    // ================================================================
    public void mostrarReservaCompleta(int idReserva) {

        try (PreparedStatement ps = conn.prepareStatement(SELECT_RESERVA_COMPLETA)) {

            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Reserva " + idReserva);
                System.out.println("Usuario: " + rs.getString("usuarioNombre"));
                System.out.println("Vehículo: " + rs.getString("vehiculoMarca"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Reserva> getReservasPorUsuario(int idUsuario) {
        List<Reserva> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_USUARIO)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setIdReserva(rs.getInt("id_reserva"));
                r.setFechaHoraInicio(rs.getTimestamp("fecha_hora_inicio").toLocalDateTime());

                Timestamp fin = rs.getTimestamp("fecha_hora_fin");
                if (fin != null) r.setFechaHoraFin(fin.toLocalDateTime());

                r.setCoste(rs.getDouble("coste"));
                r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                // Mapeamos el vehículo básico para mostrar marca/modelo en la tarjeta
                Vehiculo v = new Vehiculo();
                v.setIdVehiculo(rs.getInt("id_vehiculo"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));
                // Importante: Asignar tipo para saber si poner icono de moto o coche
                v.setTipo(com.uber.enums.TipoVehiculo.valueOf(rs.getString("tipo")));
                r.setVehiculo(v);

                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}