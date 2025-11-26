package com.uber.controllers;

import com.uber.dao.ReservaDAO;
import com.uber.dao.VehiculoDAO;
import com.uber.enums.EstadoReserva;
import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoVehiculo;
import com.uber.model.Reserva;
import com.uber.model.Usuario;
import com.uber.model.Vehiculo;
import com.uber.utils.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteController {

    // --- ELEMENTOS FXML ---
    @FXML private Label lblSaludo;
    @FXML private Label lblSaldo;
    @FXML private ScrollPane vistaPerfil;
    @FXML private Label lblIniciales, lblNombrePerfil, lblEmailPerfil, lblTelefonoPerfil, lblMetodoPago, lblSaldoPerfil;

    // Contenedores principales
    @FXML private ScrollPane vistaVehiculos;
    @FXML private ScrollPane vistaReservas;
    @FXML private FlowPane contenedorVehiculos; // Para las tarjetas de coches
    @FXML private VBox contenedorReservas;      // Para las tarjetas de reservas

    // Barra de navegación y filtros
    @FXML private HBox panelFiltros;
    @FXML private Button btnNavVehiculos;
    @FXML private Button btnNavReservas;
    @FXML private Button btnNavPerfil;

    // --- DAOS Y DATOS ---
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    private List<Vehiculo> listaVehiculosCompleta; // Para filtrar sin recargar BD

    // ================================================================
    // 1. INICIALIZACIÓN
    // ================================================================
    @FXML
    public void initialize() {
        // Cargar datos del usuario en el header
        Usuario usuario = Sesion.getInstancia().getUsuarioLogueado();
        if (usuario != null) {
            lblSaludo.setText("Hola, " + usuario.getNombre());
            lblSaldo.setText(String.format("%.2f€", usuario.getSaldo()));
        }

        // Cargar vehículos de la BD
        listaVehiculosCompleta = vehiculoDAO.getAll();

        // Mostrar la vista de vehículos por defecto
        mostrarVistaVehiculos();
        cargarVehiculos(listaVehiculosCompleta);
    }

    // ================================================================
    // 2. NAVEGACIÓN ENTRE PESTAÑAS
    // ================================================================
    @FXML
    void mostrarVistaVehiculos() {
        vistaVehiculos.setVisible(true);
        vistaReservas.setVisible(false);
        panelFiltros.setVisible(true); // Mostrar filtros solo en vehículos

        actualizarEstiloBotones(btnNavVehiculos);
    }

    @FXML
    void mostrarVistaReservas() {
        vistaVehiculos.setVisible(false);
        vistaReservas.setVisible(true);
        panelFiltros.setVisible(false); // Ocultar filtros

        actualizarEstiloBotones(btnNavReservas);

        // Recargar reservas cada vez que entramos aquí para ver cambios
        cargarReservas();
    }

    private void actualizarEstiloBotones(Button botonActivo) {
        // Quitamos la clase de "seleccionado" a todos
        btnNavVehiculos.getStyleClass().remove("nav-button-selected");
        btnNavReservas.getStyleClass().remove("nav-button-selected");
        btnNavPerfil.getStyleClass().remove("nav-button-selected");

        // Se la ponemos solo al que hemos pulsado
        botonActivo.getStyleClass().add("nav-button-selected");
    }

    // ================================================================
    // 3. LÓGICA DE VEHÍCULOS (Tarjetas y Filtros)
    // ================================================================

    private void cargarVehiculos(List<Vehiculo> lista) {
        contenedorVehiculos.getChildren().clear();
        for (Vehiculo v : lista) {
            contenedorVehiculos.getChildren().add(crearTarjetaVehiculo(v));
        }
    }

    // --- Filtros ---
    @FXML void filtrarTodos() {
        cargarVehiculos(listaVehiculosCompleta);
    }

    @FXML void filtrarCoches() {
        cargarVehiculos(listaVehiculosCompleta.stream()
                .filter(v -> v.getTipo() == TipoVehiculo.COCHE)
                .collect(Collectors.toList()));
    }

    @FXML void filtrarMotos() {
        cargarVehiculos(listaVehiculosCompleta.stream()
                .filter(v -> v.getTipo() == TipoVehiculo.MOTO)
                .collect(Collectors.toList()));
    }

    @FXML void filtrarPatinetes() {
        cargarVehiculos(listaVehiculosCompleta.stream()
                .filter(v -> v.getTipo() == TipoVehiculo.PATINETE)
                .collect(Collectors.toList()));
    }

    // --- Creador de Tarjeta de Vehículo ---
    private VBox crearTarjetaVehiculo(Vehiculo v) {
        VBox card = new VBox();
        card.getStyleClass().add("vehicle-card");
        card.setSpacing(10);

        // Cabecera: Icono y Estado
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        Label badge = new Label(v.getEstadoVehiculo().name());
        badge.getStyleClass().add("badge-disponible");

        // Si no está disponible, cambiamos el color del badge visualmente
        if (v.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            badge.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: #555;");
        }
        header.getChildren().add(badge);

        // Info Vehículo
        Label lblMarca = new Label(v.getMarca() + " " + v.getModelo());
        lblMarca.getStyleClass().add("card-title");

        Label lblTipo = new Label(v.getTipo().toString());
        lblTipo.getStyleClass().add("card-subtitle");

        Label lblUbi = new Label("📍 " + v.getEstacion().getNombreEstacion() + ", " + v.getEstacion().getCiudad());
        lblUbi.getStyleClass().add("card-location");

        // Batería
        HBox batteryBox = new HBox(10);
        batteryBox.setAlignment(Pos.CENTER_LEFT);
        Label lblBat = new Label("Batería");
        ProgressBar pb = new ProgressBar(v.getNivelBateria() / 100.0);
        pb.setPrefWidth(100);
        Label lblPorc = new Label((int)v.getNivelBateria() + "%");
        batteryBox.getChildren().addAll(lblBat, pb, lblPorc);

        // Botón Reservar
        Button btnReservar = new Button("Reservar Ahora");
        btnReservar.setMaxWidth(Double.MAX_VALUE);
        btnReservar.getStyleClass().add("btn-reservar");

        // Acción: Reservar (FASE SIGUIENTE)
        btnReservar.setOnAction(e -> {
            System.out.println("Reservando: " + v.getMarca());
            // Aquí abriremos el diálogo de reserva
        });

        // Deshabilitar si no está disponible
        if (v.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            btnReservar.setDisable(true);
            btnReservar.setText("No disponible");
        }

        card.getChildren().addAll(header, lblMarca, lblTipo, lblUbi, new Separator(), batteryBox, btnReservar);
        return card;
    }

    // ================================================================
    // 4. LÓGICA DE RESERVAS (Listado y Acciones)
    // ================================================================

    private void cargarReservas() {
        contenedorReservas.getChildren().clear();
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();

        // Obtenemos las reservas DEL USUARIO (usando el método nuevo del DAO)
        List<Reserva> reservas = reservaDAO.getReservasPorUsuario(u.getIdUsuario());

        if (reservas == null || reservas.isEmpty()) {
            Label vacio = new Label("No tienes reservas registradas.");
            vacio.setStyle("-fx-font-size: 16px; -fx-text-fill: #777; -fx-padding: 20;");
            contenedorReservas.getChildren().add(vacio);
        } else {
            for (Reserva r : reservas) {
                contenedorReservas.getChildren().add(crearTarjetaReserva(r));
            }
        }
    }

    private VBox crearTarjetaReserva(Reserva r) {
        VBox card = new VBox();
        card.getStyleClass().add("reserva-card");
        card.setSpacing(15);

        // --- FILA 1: Cabecera (Icono, Nombre, ID, Estado) ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        // Icono según tipo (Emoji simple por ahora)
        String emoji = "🚗";
        if (r.getVehiculo().getTipo() == TipoVehiculo.MOTO) emoji = "🛵";
        if (r.getVehiculo().getTipo() == TipoVehiculo.PATINETE) emoji = "⚡";

        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 24px; -fx-padding: 5; -fx-background-color: #F5F5F5; -fx-background-radius: 8;");

        // Datos del vehículo y ID Reserva
        VBox infoBox = new VBox();
        Label lblModelo = new Label(r.getVehiculo().getMarca() + " " + r.getVehiculo().getModelo());
        lblModelo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");

        Label lblId = new Label("ID: #" + r.getIdReserva());
        lblId.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");

        infoBox.getChildren().addAll(lblModelo, lblId);

        // Espaciador para empujar el estado a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Badge de Estado (Activa / Finalizada)
        Label badge = new Label(r.getEstado().name());
        if (r.getEstado() == EstadoReserva.ACTIVA) {
            badge.getStyleClass().add("badge-activa");
        } else {
            badge.getStyleClass().add("badge-finalizada");
        }

        header.getChildren().addAll(icon, infoBox, spacer, badge);

        // --- FILA 2: Fechas ---
        HBox datesBox = new HBox();
        datesBox.setSpacing(30);

        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        // Fecha Inicio
        Label lblFecha = new Label("📅 " + r.getFechaHoraInicio().format(fmtFecha));
        lblFecha.getStyleClass().add("reserva-info-text");
        Label lblHora = new Label("🕒 " + r.getFechaHoraInicio().format(fmtHora));
        lblHora.getStyleClass().add("reserva-info-text");

        datesBox.getChildren().addAll(lblFecha, lblHora);

        // --- FILA 3: Acción (Finalizar) o Coste (Si ya acabó) ---
        VBox bottomBox = new VBox();

        if (r.getEstado() == EstadoReserva.ACTIVA) {
            // Si está ACTIVA -> Botón Rojo gigante para finalizar
            Button btnFinalizar = new Button("Finalizar Reserva");
            btnFinalizar.setMaxWidth(Double.MAX_VALUE);
            btnFinalizar.getStyleClass().add("btn-finalizar");

            btnFinalizar.setOnAction(e -> finalizarReserva(r));

            bottomBox.getChildren().add(btnFinalizar); // Solo botón

        } else {
            // Si está FINALIZADA -> Mostrar Coste
            Separator sep = new Separator();
            sep.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

            HBox costeBox = new HBox();
            Label lblTextoCoste = new Label("Coste total");
            lblTextoCoste.getStyleClass().add("coste-label");

            Region spCoste = new Region();
            HBox.setHgrow(spCoste, Priority.ALWAYS);

            Label lblValorCoste = new Label(String.format("%.2f€", r.getCoste()));
            lblValorCoste.getStyleClass().add("coste-valor");

            costeBox.getChildren().addAll(lblTextoCoste, spCoste, lblValorCoste);
            bottomBox.getChildren().addAll(sep, costeBox);
        }

        card.getChildren().addAll(header, datesBox, bottomBox);
        return card;
    }

    private void finalizarReserva(Reserva r) {
        // Lógica simple para cambiar estado (Fase 3 completa)
        System.out.println("Finalizando reserva #" + r.getIdReserva());

        // 1. Calcular coste (Ejemplo: 0.15€ por minuto)
        // En un caso real usaríamos Duration.between(inicio, ahora)
        r.setEstado(EstadoReserva.FINALIZADA);
        r.setFechaHoraFin(LocalDateTime.now());
        r.setCoste(12.50); // Valor de ejemplo, luego lo calcularemos real

        // 2. Actualizar en BD
        boolean ok = reservaDAO.update(r);

        if (ok) {
            // 3. También liberar el vehículo (Ponerlo DISPONIBLE)
            // Necesitarías un método en VehiculoDAO para setEstado(id, DISPONIBLE)
            // o hacerlo manualmente aquí si tienes acceso.

            // 4. Recargar la lista para que se vea el cambio
            cargarReservas();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reserva Finalizada");
            alert.setHeaderText(null);
            alert.setContentText("El viaje ha finalizado. Coste: " + r.getCoste() + "€");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo finalizar la reserva.");
            alert.show();
        }
    }
    @FXML
    void mostrarVistaPerfil() {
        vistaVehiculos.setVisible(false);
        vistaReservas.setVisible(false);
        vistaPerfil.setVisible(true);
        panelFiltros.setVisible(false); // Ocultar filtros

        actualizarEstiloBotones(btnNavPerfil);

        cargarDatosPerfil();
    }
    private void cargarDatosPerfil() {
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();
        if (u != null) {
            // Textos básicos
            lblNombrePerfil.setText(u.getNombre() + " " + u.getApellidos());
            lblEmailPerfil.setText(u.getEmail());
            // Si el teléfono es null, ponemos vacío
            lblTelefonoPerfil.setText(u.getTelefono() != null ? u.getTelefono() : "---");
            lblMetodoPago.setText(u.getMetodoPago() != null ? u.getMetodoPago() : "SIN DEFINIR");
            lblSaldoPerfil.setText(String.format("%.2f€", u.getSaldo()));

            // Iniciales (Primera letra nombre + Primera letra apellido)
            String iniciales = "";
            if (u.getNombre() != null && !u.getNombre().isEmpty())
                iniciales += u.getNombre().charAt(0);
            if (u.getApellidos() != null && !u.getApellidos().isEmpty())
                iniciales += u.getApellidos().charAt(0);

            lblIniciales.setText(iniciales.toUpperCase());
        }
    }

    // ================================================================
    // 5. CERRAR SESIÓN
    // ================================================================
    @FXML
    private void onCerrarSesion() {
        Sesion.getInstancia().logOut();
        try {
            Stage stage = (Stage) lblSaludo.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}