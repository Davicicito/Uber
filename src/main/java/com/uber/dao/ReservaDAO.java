package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.enums.EstadoReserva;
import com.uber.enums.TipoVehiculo;
import com.uber.model.Reserva;
import com.uber.model.Usuario;
import com.uber.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String SELECT_ALL = "SELECT * FROM Reserva";
    private static final String SELECT_BY_ID = "SELECT * FROM Reserva WHERE id_reserva = ?";
    private static final String INSERT = "INSERT INTO Reserva (id_usuario, id_vehiculo, fecha_hora_inicio, fecha_hora_fin, coste, estado) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE Reserva SET id_usuario = ?, id_vehiculo = ?, fecha_hora_inicio = ?, fecha_hora_fin = ?, coste = ?, estado = ? WHERE id_reserva = ?";
    private static final String DELETE = "DELETE FROM Reserva WHERE id_reserva = ?";
    private static final String SELECT_RESERVA_COMPLETA = "SELECT r.*, u.nombre AS usuarioNombre, v.marca AS vehiculoMarca FROM Reserva r JOIN Usuario u ON r.id_usuario = u.id_usuario JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo WHERE r.id_reserva = ?";
    private static final String SELECT_BY_USUARIO = "SELECT r.*, v.marca, v.modelo, v.tipo FROM Reserva r JOIN Vehiculo v ON r.id_vehiculo = v.id_vehiculo WHERE r.id_usuario = ? ORDER BY r.fecha_hora_inicio DESC";

    private static final String CANCEL_RESERVA_SQL = "UPDATE Reserva SET estado = 'CANCELADA', fecha_hora_fin = NOW() WHERE id_reserva = ?";
    private static final String RELEASE_VEHICULO_SQL = "UPDATE Vehiculo SET estado_vehiculo = 'DISPONIBLE' WHERE id_vehiculo = ?";
    private static final String UPDATE_VEHICULO_OCUPADO = "UPDATE Vehiculo SET estado_vehiculo = 'EN_USO' WHERE id_vehiculo = ?";
    private static final String FINALIZE_RESERVA_SQL = "UPDATE Reserva SET estado = 'FINALIZADA', fecha_hora_fin = NOW(), coste = ? WHERE id_reserva = ?";
    private static final String DEDUCT_SALDO_SQL = "UPDATE Usuario SET saldo = saldo - ? WHERE id_usuario = ?";

    private final Connection conn;

    public ReservaDAO() {
        conn = ConnectionBD.getConnection();
    }

    /**
     * Convierte una fila del ResultSet en un objeto Reserva.
     * Solo carga los IDs del usuario y vehículo para mantenerlo simple.
     *
     * @param rs fila del resultado de la consulta
     * @return objeto Reserva con los datos cargados
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Reserva mapReserva(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("id_reserva"));

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

    /**
     * Obtiene todas las reservas registradas.
     *
     * @return lista completa de reservas
     */
    public List<Reserva> getAll() {
        List<Reserva> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL)) {

            while (rs.next()) lista.add(mapReserva(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca una reserva según su ID.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada o null si no existe
     */
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

    /**
     * Modifica los datos de una reserva ya existente.
     *
     * @param r reserva con los nuevos valores
     * @return true si se actualizó correctamente
     */
    public boolean update(Reserva r) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setInt(1, r.getUsuario().getIdUsuario());
            ps.setInt(2, r.getVehiculo().getIdVehiculo());

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
            ps.setInt(7, r.getIdReserva());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Borra una reserva por su ID.
     *
     * @param idReserva id de la reserva a eliminar
     * @return true si se eliminó con éxito
     */
    public boolean delete(int idReserva) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserta una nueva reserva y marca el vehículo como en uso.
     * Todo se hace dentro de una transacción.
     *
     * @param r reserva a registrar
     * @return true si todo funcionó correctamente
     */
    public boolean crearReserva(Reserva r) {
        try {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setInt(1, r.getUsuario().getIdUsuario());
            ps.setInt(2, r.getVehiculo().getIdVehiculo());

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

            PreparedStatement psVehiculo = conn.prepareStatement(UPDATE_VEHICULO_OCUPADO);
            psVehiculo.setInt(1, r.getVehiculo().getIdVehiculo());
            psVehiculo.executeUpdate();

            conn.commit();
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
     * Cambia una reserva a cancelada y vuelve a marcar el vehículo como disponible.
     *
     * @param idReserva id de la reserva a cancelar
     * @param idVehiculo vehículo implicado
     * @return true si se canceló correctamente
     */
    public boolean cancelarReserva(int idReserva, int idVehiculo) {
        try {
            conn.setAutoCommit(false);

            PreparedStatement psReserva = conn.prepareStatement(CANCEL_RESERVA_SQL);
            psReserva.setInt(1, idReserva);
            psReserva.executeUpdate();

            PreparedStatement psVehiculo = conn.prepareStatement(RELEASE_VEHICULO_SQL);
            psVehiculo.setInt(1, idVehiculo);
            psVehiculo.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;

        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /**
     * Obtiene todas las reservas hechas por un usuario concreto.
     * Incluye datos del vehículo gracias al JOIN.
     *
     * @param idUsuario id del usuario
     * @return lista de reservas asociadas a ese usuario
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

                Vehiculo v = new Vehiculo();
                v.setIdVehiculo(rs.getInt("id_vehiculo"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));

                try {
                    v.setTipo(TipoVehiculo.valueOf(rs.getString("tipo")));
                } catch (Exception e) {
                    v.setTipo(TipoVehiculo.COCHE);
                }

                r.setVehiculo(v);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Muestra por consola información detallada de una reserva.
     * (Solo para pruebas.)
     *
     * @param idReserva id de la reserva
     */
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
     * Marca una reserva como finalizada, guarda el coste
     * y libera el vehículo.
     *
     * @param idReserva id de la reserva
     * @param idVehiculo id del vehículo asociado
     * @param costeFinal coste calculado al terminar
     * @return true si se completó correctamente
     */
    public boolean finalizarReserva(int idReserva, int idVehiculo, int idUsuario, double costeFinal) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement psReserva = conn.prepareStatement(FINALIZE_RESERVA_SQL);
            psReserva.setDouble(1, costeFinal);
            psReserva.setInt(2, idReserva);
            psReserva.executeUpdate();

            PreparedStatement psVehiculo = conn.prepareStatement(RELEASE_VEHICULO_SQL);
            psVehiculo.setInt(1, idVehiculo);
            psVehiculo.executeUpdate();

            PreparedStatement psSaldo = conn.prepareStatement(DEDUCT_SALDO_SQL);
            psSaldo.setDouble(1, costeFinal);
            psSaldo.setInt(2, idUsuario);
            psSaldo.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;

        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}
