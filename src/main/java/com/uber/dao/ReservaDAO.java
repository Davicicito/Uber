package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.EstadoReserva;
import com.uber.enums.TipoVehiculo; // Asegúrate de tener este import
import com.uber.model.Reserva;
import com.uber.model.Usuario;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    // ================================================================
    //  1. CONSULTAS SQL (CONSTANTES)
    // ================================================================

    private static final String SELECT_ALL = "SELECT * FROM Reserva";

    private static final String SELECT_BY_ID = "SELECT * FROM Reserva WHERE id_reserva = ?";

    private static final String INSERT = "INSERT INTO Reserva (id_usuario, id_vehiculo, fecha_hora_inicio, fecha_hora_fin, coste, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE Reserva SET id_usuario = ?, id_vehiculo = ?, fecha_hora_inicio = ?, fecha_hora_fin = ?, coste = ?, estado = ? " +
            "WHERE id_reserva = ?";

    private static final String DELETE = "DELETE FROM Reserva WHERE id_reserva = ?";

    // Consulta avanzada con JOIN para ver detalles
    private static final String SELECT_RESERVA_COMPLETA = "SELECT r.*, u.nombre AS usuarioNombre, v.marca AS vehiculoMarca " +
            "FROM Reserva r " +
            "JOIN Usuario u ON r.id_usuario = u.id_usuario " +
            "JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
            "WHERE r.id_reserva = ?";

    // Consulta para "Mis Reservas" (JOIN con Vehículo)
    private static final String SELECT_BY_USUARIO =
            "SELECT r.*, v.marca, v.modelo, v.tipo " +
                    "FROM Reserva r " +
                    "JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo " +
                    "WHERE r.id_usuario = ? " +
                    "ORDER BY r.fecha_hora_inicio DESC";

    // Consultas para CANCELAR (Transacción)
    private static final String CANCEL_RESERVA_SQL = "UPDATE Reserva SET estado = 'CANCELADA', fecha_hora_fin = NOW() WHERE id_reserva = ?";
    private static final String RELEASE_VEHICULO_SQL = "UPDATE Vehiculo SET estado_vehiculo = 'DISPONIBLE' WHERE id_vehiculo = ?";

    // Consulta para CREAR RESERVA (Parte de transacción) - Actualizar coche a ocupado
    private static final String UPDATE_VEHICULO_OCUPADO = "UPDATE Vehiculo SET estado_vehiculo = 'EN_USO' WHERE id_vehiculo = ?";

    // Consulta para FINALIZAR (Actualiza estado, fecha fin, coste y libera vehículo)
    private static final String FINALIZE_RESERVA_SQL = "UPDATE Reserva SET estado = 'FINALIZADA', fecha_hora_fin = NOW(), coste = ? WHERE id_reserva = ?";


    // ================================================================
    //  2. CONEXIÓN Y MAPEO
    // ================================================================
    private final Connection conn;

    public ReservaDAO() {
        conn = ConnectionBD.getConnection();
    }

    private Reserva mapReserva(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("id_reserva"));

        // Mapeo básico de relaciones (solo IDs)
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

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            r.setEstado(EstadoReserva.valueOf(estadoStr));
        }

        return r;
    }

    // ================================================================
    //  3. MÉTODOS CRUD ESTÁNDAR
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
    //  4. MÉTODOS TRANSACCIONALES Y AVANZADOS
    // ================================================================

    /**
     * Crea una nueva reserva y marca el vehículo como EN_USO en una sola transacción.
     */
    public boolean crearReserva(Reserva r) {
        try {
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Insertar la reserva
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setInt(1, r.getUsuario().getIdUsuario());
            ps.setInt(2, r.getVehiculo().getIdVehiculo());

            if (r.getFechaHoraInicio() != null) ps.setTimestamp(3, Timestamp.valueOf(r.getFechaHoraInicio()));
            else ps.setNull(3, Types.TIMESTAMP);

            if (r.getFechaHoraFin() != null) ps.setTimestamp(4, Timestamp.valueOf(r.getFechaHoraFin()));
            else ps.setNull(4, Types.TIMESTAMP);

            ps.setDouble(5, r.getCoste());
            ps.setString(6, r.getEstado().name());

            ps.executeUpdate();

            // 2. Actualizar vehículo a 'EN_USO'
            PreparedStatement psVehiculo = conn.prepareStatement(UPDATE_VEHICULO_OCUPADO);
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

    /**
     * Cancela una reserva y libera el vehículo (DISPONIBLE) en una sola transacción.
     */
    public boolean cancelarReserva(int idReserva, int idVehiculo) {
        try {
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Pasar reserva a CANCELADA
            PreparedStatement psReserva = conn.prepareStatement(CANCEL_RESERVA_SQL);
            psReserva.setInt(1, idReserva);
            psReserva.executeUpdate();

            // 2. Liberar vehículo (DISPONIBLE)
            PreparedStatement psVehiculo = conn.prepareStatement(RELEASE_VEHICULO_SQL);
            psVehiculo.setInt(1, idVehiculo);
            psVehiculo.executeUpdate();

            conn.commit(); // FIN TRANSACCIÓN ÉXITO
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Obtiene las reservas de un usuario concreto (incluyendo datos del vehículo).
     */
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

                // Mapeamos datos extra del vehículo gracias al JOIN
                Vehiculo v = new Vehiculo();
                v.setIdVehiculo(rs.getInt("id_vehiculo"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));
                // Necesario para saber qué icono mostrar (Coche/Moto/Patinete)
                try {
                    v.setTipo(TipoVehiculo.valueOf(rs.getString("tipo")));
                } catch (Exception e) {
                    v.setTipo(TipoVehiculo.COCHE); // Valor por defecto si falla
                }

                r.setVehiculo(v);
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método extra para depuración
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

    /**
     * Finaliza una reserva: establece coste, fecha fin, estado FINALIZADA y libera el vehículo.
     * Todo en una sola transacción ACID.
     */
    public boolean finalizarReserva(int idReserva, int idVehiculo, double costeFinal) {
        try {
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Actualizar Reserva (Poner FINALIZADA y guardar el COSTE)
            PreparedStatement psReserva = conn.prepareStatement(FINALIZE_RESERVA_SQL);
            psReserva.setDouble(1, costeFinal);
            psReserva.setInt(2, idReserva);
            psReserva.executeUpdate();

            // 2. Liberar Vehículo (Poner DISPONIBLE) - Reusamos la SQL de cancelar
            PreparedStatement psVehiculo = conn.prepareStatement(RELEASE_VEHICULO_SQL);
            psVehiculo.setInt(1, idVehiculo);
            psVehiculo.executeUpdate();

            conn.commit(); // FIN TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}