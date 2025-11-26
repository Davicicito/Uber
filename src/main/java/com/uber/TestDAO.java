package com.uber;

import com.uber.dao.*;
import com.uber.enums.EstadoReserva;
import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoMantenimiento;
import com.uber.enums.TipoVehiculo;
import com.uber.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println("=== TEST DAO - Inicio ===");

        try {
            // Instanciar DAOs (usan ConnectionBD internamente)
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            EstacionDAO estacionDAO = new EstacionDAO();
            VehiculoDAO vehiculoDAO = new VehiculoDAO();
            MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();
            TieneDAO tieneDAO = new TieneDAO();
            ReservaDAO reservaDAO = new ReservaDAO();

            // 1) LISTAR todos los usuarios (SELECT *)
            System.out.println("\n-- Usuarios (getAll) --");
            List<Usuario> usuarios = usuarioDAO.getAll();
            System.out.println("Total usuarios: " + usuarios.size());
            usuarios.stream().limit(5).forEach(u -> System.out.println("  " + u));

            // 2) INSERTAR un usuario de prueba
            System.out.println("\n-- Insertar Usuario de prueba --");
            Usuario nuevo = new Usuario();
            nuevo.setNombre("Prueba");
            nuevo.setApellidos("Alumno");
            nuevo.setEmail("prueba.alumno@example.com");
            nuevo.setContrasena("pw");
            nuevo.setTelefono("600000000");
            nuevo.setMetodoPago("Tarjeta");
            nuevo.setSaldo(5.0);
            nuevo.setEstadoCuenta(com.uber.enums.EstadoCuenta.ACTIVO);

            boolean inserted = usuarioDAO.insert(nuevo);
            System.out.println("Insertado: " + inserted);

            // Comprobar que aparece en la lista
            usuarios = usuarioDAO.getAll();
            System.out.println("Total usuarios tras insert: " + usuarios.size());

            // 3) Borrar el usuario insertado (buscar por email para obtener id)
            System.out.println("\n-- Borrar Usuario de prueba --");
            Usuario encontrado = usuarios.stream()
                    .filter(u -> "prueba.alumno@example.com".equals(u.getEmail()))
                    .findFirst().orElse(null);

            if (encontrado != null) {
                boolean borrado = usuarioDAO.delete(encontrado.getIdUsuario());
                System.out.println("Borrado id=" + encontrado.getIdUsuario() + " -> " + borrado);
            } else {
                System.out.println("Usuario de prueba no encontrado (posible duplicado ya presente)");
            }

            // 4) Listar estaciones y mostrar una (EAGER test via VehiculoDAO)
            System.out.println("\n-- Estaciones (getAll) --");
            estacionDAO.getAll().forEach(e -> System.out.println("  " + e));

            // 5) VEHÍCULO EAGER: getById devuelve Vehiculo con Estacion cargada
            System.out.println("\n-- Vehiculo EAGER (getById) --");
            List<Vehiculo> vehs = vehiculoDAO.getAll();
            if (!vehs.isEmpty()) {
                Vehiculo v = vehs.get(0);
                System.out.println("Vehiculo listado: " + v);
                Vehiculo vById = vehiculoDAO.getById(v.getIdVehiculo());
                System.out.println("Vehiculo por id (debería incluir estacion): " + vById);
                if (vById.getEstacion() != null) {
                    System.out.println("  - Estación asociada: " + vById.getEstacion());
                } else {
                    System.out.println("  - Estación NO cargada (revisa DAO)");
                }
            } else {
                System.out.println("No hay vehículos para probar.");
            }

            // 6) TIENE (insert + contar + ultimo)
            System.out.println("\n-- Tiene (insert, contar, ultimo) --");
            // Para insertar necesitamos un Vehiculo y un Mantenimiento existentes:
            List<Mantenimiento> mantAll = mantenimientoDAO.getAll();
            vehs = vehiculoDAO.getAll();
            if (!mantAll.isEmpty() && !vehs.isEmpty()) {
                Tiene nuevoTiene = new Tiene();
                nuevoTiene.setVehiculo(vehs.get(0));
                nuevoTiene.setMantenimiento(mantAll.get(0));
                nuevoTiene.setFechaHora(LocalDateTime.now());
                nuevoTiene.setCoste(55.0);
                nuevoTiene.setNotas("Prueba inserción mantenimiento");

                boolean insTiene = tieneDAO.insert(nuevoTiene);
                System.out.println("Tiene insertado: " + insTiene);

                int total = tieneDAO.contarMantenimientos(vehs.get(0).getIdVehiculo());
                System.out.println("Total mantenimientos vehículo id " + vehs.get(0).getIdVehiculo() + ": " + total);

                Tiene ultimo = tieneDAO.getUltimoMantenimiento(vehs.get(0).getIdVehiculo());
                System.out.println("Último mantenimiento: " + (ultimo != null ? ultimo : "null"));
            } else {
                System.out.println("No hay mantenimiento o vehículo disponible para insertar Tiene.");
            }

            // 7) RESERVA con transacción (crearReserva cambia estado del vehículo)
            System.out.println("\n-- Reserva con transacción (crearReserva) --");
            // elegir usuario y vehiculo para la prueba
            usuarios = usuarioDAO.getAll();
            vehs = vehiculoDAO.getAll();
            if (!usuarios.isEmpty() && !vehs.isEmpty()) {
                Usuario u = usuarios.get(0);
                Vehiculo veh = vehs.get(0);

                Reserva r = new Reserva();
                r.setUsuario(u);
                r.setVehiculo(veh);
                r.setFechaHoraInicio(LocalDateTime.now());
                r.setFechaHoraFin(LocalDateTime.now().plusHours(1));
                r.setCoste(7.5);
                r.setEstado(EstadoReserva.ACTIVA);

                boolean creada = reservaDAO.crearReserva(r);
                System.out.println("Reserva creada (transacción): " + creada);

                // Leer la reserva más reciente (lista) para comprobar
                List<Reserva> reservasUsuario = reservaDAO.getAll();
                System.out.println("Total reservas tras crear: " + reservasUsuario.size());
            } else {
                System.out.println("No hay usuarios o vehículos para crear reserva.");
            }

            // 8) Lazy VS Eager demonstration:
            System.out.println("\n-- Lazy vs Eager demo --");
            // Reserva.getById en nuestro DAO mapea solo IDs (lazy),
            // mientras que mostrarReservaCompleta usa JOIN (eager-ish).
            if (!reservaDAO.getAll().isEmpty()) {
                Reserva sample = reservaDAO.getAll().get(0);
                System.out.println("Reserva (lazy mapping): " + sample);
                System.out.println("Mostrar reserva completa (JOIN):");
                reservaDAO.mostrarReservaCompleta(sample.getIdReserva());
            }

            System.out.println("\n=== TEST DAO - Fin ===");

        } catch (Exception ex) {
            System.err.println("Error en pruebas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

