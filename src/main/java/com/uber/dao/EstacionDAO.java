package com.uber.dao;

import com.uber.database.ConnectionBD;
import com.uber.model.Estacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Estacion.
 * Se encarga de realizar las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * sobre la tabla 'Estacion' en la base de datos.
 */
public class EstacionDAO {

    // Consultas SQL predefinidas
    private static final String SELECT_ALL = "SELECT * FROM Estacion";

    private static final String SELECT_BY_ID = "SELECT * FROM Estacion WHERE id_estacion = ?";

    private static final String INSERT = "INSERT INTO Estacion (nombre_estacion, direccion, capacidad, ciudad) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE Estacion SET nombre_estacion = ?, direccion = ?, capacidad = ?, ciudad = ? " +
            "WHERE id_estacion = ?";

    private static final String DELETE = "DELETE FROM Estacion WHERE id_estacion = ?";

    // Consulta avanzada con JOIN y agrupación
    private static final String SELECT_ESTACION_CON_VEHICULOS = "SELECT e.id_estacion, e.nombre_estacion, COUNT(v.id_vehiculo) AS totalVehiculos " +
            "FROM Estacion e LEFT JOIN Vehiculo v ON e.id_estacion = v.id_estacion " +
            "WHERE e.id_estacion = ? GROUP BY e.id_estacion";

    // Objeto de conexión a la base de datos
    private final Connection conn;

    /**
     * Constructor de la clase.
     * Obtiene la instancia de conexión a la base de datos.
     */
    public EstacionDAO() {
        this.conn = ConnectionBD.getConnection();
    }

    // ================================================================
    //   MÉTODOS PRIVADOS (AUXILIARES)
    // ================================================================

    /**
     * Convierte una fila de un ResultSet en un objeto Estacion.
     * @param rs El ResultSet con los datos de la consulta.
     * @return Un objeto Estacion con los datos de la fila actual.
     * @throws SQLException Si hay un error al acceder a los datos.
     */
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
    //   MÉTODOS PÚBLICOS (CRUD)
    // ================================================================

    /**
     * Obtiene todas las estaciones registradas en la base de datos.
     * @return Una lista de objetos Estacion.
     */
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

    /**
     * Busca una estación por su identificador único.
     * @param id El ID de la estación a buscar.
     * @return El objeto Estacion si se encuentra, o null si no existe.
     */
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

    /**
     * Inserta una nueva estación en la base de datos.
     * @param e El objeto Estacion con los datos a guardar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
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

    /**
     * Actualiza los datos de una estación existente.
     * @param e El objeto Estacion con los datos modificados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
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

    /**
     * Elimina una estación de la base de datos por su ID.
     * @param id El ID de la estación a borrar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
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
    //   CONSULTAS AVANZADAS
    // ================================================================

    /**
     * Muestra por consola el nombre de la estación y la cantidad de vehículos
     * que tiene asignados actualmente.
     * @param idEstacion El ID de la estación a consultar.
     */
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