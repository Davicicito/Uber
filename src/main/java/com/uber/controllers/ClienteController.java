package com.uber.controllers;

import com.uber.dao.ReservaDAO;
import com.uber.dao.UsuarioDAO;
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

/**
 * Controlador principal para la vista del Cliente.
 * Gestiona la reserva de vehículos, visualización de historial y perfil.
 */
public class ClienteController {

    // --- ELEMENTOS FXML ---
    @FXML private Label lblSaludo;
    @FXML private Label lblSaldo;

    // Contenedores principales (Vistas)
    @FXML private ScrollPane vistaVehiculos;
    @FXML private ScrollPane vistaReservas;
    @FXML private ScrollPane vistaPerfil;

    // Contenido dinámico
    @FXML private FlowPane contenedorVehiculos;
    @FXML private VBox contenedorReservas;

    // Barra de navegación y filtros
    @FXML private HBox panelFiltros;
    @FXML private Button btnNavVehiculos;
    @FXML private Button btnNavReservas;
    @FXML private Button btnNavPerfil;

    // Elementos del Perfil
    @FXML private Label lblIniciales, lblNombrePerfil, lblEmailPerfil, lblTelefonoPerfil, lblMetodoPago, lblSaldoPerfil;

    // --- DAOS Y DATOS ---
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Necesario para editar perfil

    private List<Vehiculo> listaVehiculosCompleta; // Para filtrar sin recargar BD

    /**
     * Método de inicialización. Carga los datos del usuario y la lista de vehículos.
     */
    @FXML
    public void initialize() {
        Usuario usuario = Sesion.getInstancia().getUsuarioLogueado();
        if (usuario != null) {
            actualizarHeader(usuario);
        }

        // Cargar vehículos iniciales
        listaVehiculosCompleta = vehiculoDAO.getAll();

        // Mostrar vista inicial
        mostrarVistaVehiculos();
        cargarVehiculos(listaVehiculosCompleta);
    }

    /**
     * Actualiza el saludo y el saldo en la barra superior.
     * @param u El usuario logueado.
     */
    private void actualizarHeader(Usuario u) {
        lblSaludo.setText("Hola, " + u.getNombre());
        lblSaldo.setText(String.format("%.2f€", u.getSaldo()));
    }

    // ================================================================
    // 2. NAVEGACIÓN ENTRE PESTAÑAS
    // ================================================================

    /**
     * Muestra la vista de vehículos y oculta las demás.
     */
    @FXML
    void mostrarVistaVehiculos() {
        vistaVehiculos.setVisible(true);
        vistaReservas.setVisible(false);
        vistaPerfil.setVisible(false);

        panelFiltros.setVisible(true);
        actualizarEstiloBotones(btnNavVehiculos);

        // Refrescar lista por si un coche se ha liberado
        listaVehiculosCompleta = vehiculoDAO.getAll();
        filtrarTodos();
    }

    /**
     * Muestra la vista de reservas y oculta las demás.
     */
    @FXML
    void mostrarVistaReservas() {
        vistaVehiculos.setVisible(false);
        vistaReservas.setVisible(true);
        vistaPerfil.setVisible(false);

        panelFiltros.setVisible(false);
        actualizarEstiloBotones(btnNavReservas);

        cargarReservas();
    }

    /**
     * Muestra la vista de perfil y oculta las demás.
     */
    @FXML
    void mostrarVistaPerfil() {
        vistaVehiculos.setVisible(false);
        vistaReservas.setVisible(false);
        vistaPerfil.setVisible(true);

        panelFiltros.setVisible(false);
        actualizarEstiloBotones(btnNavPerfil);

        cargarDatosPerfil();
    }

    /**
     * Gestiona el estilo visual de los botones del menú.
     * @param botonActivo El botón que se acaba de pulsar.
     */
    private void actualizarEstiloBotones(Button botonActivo) {
        btnNavVehiculos.getStyleClass().remove("nav-button-selected");
        btnNavReservas.getStyleClass().remove("nav-button-selected");
        btnNavPerfil.getStyleClass().remove("nav-button-selected");

        botonActivo.getStyleClass().add("nav-button-selected");
    }

    // ================================================================
    // 3. LÓGICA DE VEHÍCULOS (Tarjetas y Filtros)
    // ================================================================

    /**
     * Carga las tarjetas de vehículos en el contenedor.
     * @param lista La lista de vehículos a mostrar.
     */
    private void cargarVehiculos(List<Vehiculo> lista) {
        contenedorVehiculos.getChildren().clear();
        for (Vehiculo v : lista) {
            contenedorVehiculos.getChildren().add(crearTarjetaVehiculo(v));
        }
    }

    /**
     * Filtra y muestra todos los vehículos.
     */
    @FXML void filtrarTodos() { cargarVehiculos(listaVehiculosCompleta); }

    /**
     * Filtra y muestra solo los coches.
     */
    @FXML void filtrarCoches() { cargarVehiculos(listaVehiculosCompleta.stream().filter(v -> v.getTipo() == TipoVehiculo.COCHE).collect(Collectors.toList())); }

    /**
     * Filtra y muestra solo las motos.
     */
    @FXML void filtrarMotos() { cargarVehiculos(listaVehiculosCompleta.stream().filter(v -> v.getTipo() == TipoVehiculo.MOTO).collect(Collectors.toList())); }

    /**
     * Filtra y muestra solo los patinetes.
     */
    @FXML void filtrarPatinetes() { cargarVehiculos(listaVehiculosCompleta.stream().filter(v -> v.getTipo() == TipoVehiculo.PATINETE).collect(Collectors.toList())); }

    /**
     * Crea una tarjeta visual para un vehículo.
     * @param v El vehículo a mostrar.
     * @return Un VBox con la tarjeta del vehículo.
     */
    private VBox crearTarjetaVehiculo(Vehiculo v) {
        VBox card = new VBox();
        card.getStyleClass().add("vehicle-card");
        card.setSpacing(10);

        // Cabecera
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        Label badge = new Label(v.getEstadoVehiculo().name());
        badge.getStyleClass().add("badge-disponible");

        if (v.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            badge.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: #555;");
        }
        header.getChildren().add(badge);

        // Info
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

        // Botón
        Button btnReservar = new Button("Reservar Ahora");
        btnReservar.setMaxWidth(Double.MAX_VALUE);
        btnReservar.getStyleClass().add("btn-reservar");
        btnReservar.setOnAction(e -> mostrarDialogoReserva(v));

        if (v.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            btnReservar.setDisable(true);
            btnReservar.setText("No disponible");
        }

        card.getChildren().addAll(header, lblMarca, lblTipo, lblUbi, new Separator(), batteryBox, btnReservar);
        return card;
    }

    // ================================================================
    // 4. LÓGICA DE CREAR RESERVA
    // ================================================================

    /**
     * Muestra el diálogo para confirmar una reserva.
     * @param v El vehículo que se quiere reservar.
     */
    private void mostrarDialogoReserva(Vehiculo v) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Reserva");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Header Verde
        VBox header = new VBox(5);
        header.getStyleClass().add("custom-dialog-header");
        Label lblTitle = new Label("Reservar " + v.getMarca() + " " + v.getModelo());
        lblTitle.getStyleClass().add("dialog-title-text");
        Label lblSub = new Label("📍 " + v.getEstacion().getNombreEstacion() + " (" + v.getEstacion().getCiudad() + ")");
        lblSub.getStyleClass().add("dialog-subtitle-text");
        header.getChildren().addAll(lblTitle, lblSub);

        // Contenido
        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");

        VBox boxFecha = new VBox(5);
        Label lblFecha = new Label("Fecha de inicio");
        lblFecha.getStyleClass().add("input-label");
        DatePicker dateInicio = new DatePicker(java.time.LocalDate.now());
        dateInicio.setMaxWidth(Double.MAX_VALUE);
        boxFecha.getChildren().addAll(lblFecha, dateInicio);

        VBox boxHoras = new VBox(5);
        Label lblHoras = new Label("¿Cuántas horas lo necesitas?");
        lblHoras.getStyleClass().add("input-label");
        Spinner<Integer> spinnerHoras = new Spinner<>(1, 24, 1);
        spinnerHoras.setMaxWidth(Double.MAX_VALUE);
        spinnerHoras.setEditable(true);
        boxHoras.getChildren().addAll(lblHoras, spinnerHoras);

        VBox boxPrecio = new VBox(2);
        boxPrecio.getStyleClass().add("price-container");
        Label lblEstimado = new Label("Total Estimado");
        lblEstimado.getStyleClass().add("price-label-small");
        Label lblPrecioFinal = new Label("5.00€");
        lblPrecioFinal.getStyleClass().add("price-value-big");
        boxPrecio.getChildren().addAll(lblEstimado, lblPrecioFinal);

        double precioPorHora = 5.0;
        spinnerHoras.valueProperty().addListener((obs, oldVal, newVal) -> {
            double total = newVal * precioPorHora;
            lblPrecioFinal.setText(String.format("%.2f€", total));
        });

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(header, content);
        content.getChildren().addAll(boxFecha, boxHoras, new Separator(), boxPrecio);
        dialog.getDialogPane().setContent(mainLayout);

        ButtonType btnConfirmar = new ButtonType("Confirmar y Pagar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnConfirmar);
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnConfirmar);
        btnOk.getStyleClass().add("btn-reservar");

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnConfirmar) {
                realizarReserva(v, dateInicio.getValue(), spinnerHoras.getValue());
            }
        });
    }

    /**
     * Realiza la reserva de un vehículo.
     * @param v El vehículo a reservar.
     * @param fecha La fecha de inicio.
     * @param horas La duración en horas.
     */
    private void realizarReserva(Vehiculo v, java.time.LocalDate fecha, int horas) {
        if (fecha == null || fecha.isBefore(java.time.LocalDate.now())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Fecha inválida", "La fecha no puede ser anterior a hoy.");
            return;
        }

        Usuario usuario = Sesion.getInstancia().getUsuarioLogueado();
        Reserva r = new Reserva();
        r.setUsuario(usuario);
        r.setVehiculo(v);

        LocalDateTime inicio = fecha.atTime(LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());
        if (fecha.isAfter(java.time.LocalDate.now())) {
            inicio = fecha.atTime(9, 0);
        }
        r.setFechaHoraInicio(inicio);
        r.setFechaHoraFin(inicio.plusHours(horas));

        double precioPorHora = 5.0;
        r.setCoste(horas * precioPorHora);
        r.setEstado(EstadoReserva.ACTIVA);

        if (reservaDAO.crearReserva(r)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Reserva Exitosa!", "Has reservado el vehículo por " + r.getCoste() + "€");
            listaVehiculosCompleta = vehiculoDAO.getAll();
            filtrarTodos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo realizar la reserva.");
        }
    }

    // ================================================================
    // 5. LÓGICA DE "MIS RESERVAS"
    // ================================================================

    /**
     * Carga las reservas del usuario en la vista.
     */
    private void cargarReservas() {
        contenedorReservas.getChildren().clear();
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();
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

    /**
     * Crea una tarjeta visual para una reserva.
     * @param r La reserva a mostrar.
     * @return Un VBox con la tarjeta de la reserva.
     */
    private VBox crearTarjetaReserva(Reserva r) {
        VBox card = new VBox();
        card.getStyleClass().add("reserva-card");
        card.setSpacing(15);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        String emoji = (r.getVehiculo().getTipo() == TipoVehiculo.COCHE) ? "🚗" : (r.getVehiculo().getTipo() == TipoVehiculo.MOTO ? "🛵" : "⚡");
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 24px; -fx-padding: 5; -fx-background-color: #F5F5F5; -fx-background-radius: 8;");

        VBox infoBox = new VBox();
        Label lblModelo = new Label(r.getVehiculo().getMarca() + " " + r.getVehiculo().getModelo());
        lblModelo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        Label lblId = new Label("ID: #" + r.getIdReserva());
        lblId.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
        infoBox.getChildren().addAll(lblModelo, lblId);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(r.getEstado().name());
        if (r.getEstado() == EstadoReserva.ACTIVA) {
            badge.getStyleClass().add("badge-activa");
        } else {
            badge.getStyleClass().add("badge-finalizada");
        }
        header.getChildren().addAll(icon, infoBox, spacer, badge);

        HBox datesBox = new HBox(30);
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
        Label lblFecha = new Label("📅 " + r.getFechaHoraInicio().format(fmtFecha));
        lblFecha.getStyleClass().add("reserva-info-text");
        Label lblHora = new Label("🕒 " + r.getFechaHoraInicio().format(fmtHora));
        lblHora.getStyleClass().add("reserva-info-text");
        datesBox.getChildren().addAll(lblFecha, lblHora);

        VBox bottomBox = new VBox(10);
        if (r.getEstado() == EstadoReserva.ACTIVA) {
            Button btnFinalizar = new Button("Finalizar y Pagar");
            btnFinalizar.setMaxWidth(Double.MAX_VALUE);
            btnFinalizar.getStyleClass().add("btn-finalizar");
            btnFinalizar.setOnAction(e -> finalizarReserva(r));

            Button btnCancelar = new Button("Cancelar Reserva");
            btnCancelar.setMaxWidth(Double.MAX_VALUE);
            btnCancelar.getStyleClass().add("btn-secondary-action");
            btnCancelar.setOnAction(e -> accionCancelar(r));

            bottomBox.getChildren().addAll(btnFinalizar, btnCancelar);
        } else {
            Separator sep = new Separator();
            sep.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));
            HBox costeBox = new HBox();
            Label lblTexto = new Label("Coste total");
            lblTexto.getStyleClass().add("coste-label");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label lblValor = new Label(String.format("%.2f€", r.getCoste()));
            lblValor.getStyleClass().add("coste-valor");
            costeBox.getChildren().addAll(lblTexto, sp, lblValor);
            bottomBox.getChildren().addAll(sep, costeBox);
        }

        card.getChildren().addAll(header, datesBox, bottomBox);
        return card;
    }

    /**
     * Finaliza una reserva activa.
     * @param r La reserva a finalizar.
     */
    private void finalizarReserva(Reserva r) {
        double costeFinal = r.getCoste();
        if (reservaDAO.finalizarReserva(r.getIdReserva(), r.getVehiculo().getIdVehiculo(), costeFinal)) {
            cargarReservas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Viaje Finalizado", "Gracias por viajar con nosotros.\nSe ha realizado el cobro de: " + String.format("%.2f€", costeFinal));
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo finalizar la reserva.");
        }
    }

    /**
     * Cancela una reserva activa.
     * @param r La reserva a cancelar.
     */
    private void accionCancelar(Reserva r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar Reserva");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("El vehículo quedará libre inmediatamente.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (reservaDAO.cancelarReserva(r.getIdReserva(), r.getVehiculo().getIdVehiculo())) {
                cargarReservas();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cancelada", "Reserva cancelada correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cancelar.");
            }
        }
    }

    // ================================================================
    // 6. PERFIL (LOGICA Y VALIDACIONES)
    // ================================================================

    /**
     * Carga y muestra los datos del perfil del usuario.
     */
    private void cargarDatosPerfil() {
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();
        if (u != null) {
            lblNombrePerfil.setText(u.getNombre() + " " + u.getApellidos());
            lblEmailPerfil.setText(u.getEmail());
            lblTelefonoPerfil.setText(u.getTelefono() != null ? u.getTelefono() : "---");
            lblMetodoPago.setText(u.getMetodoPago() != null ? u.getMetodoPago() : "SIN DEFINIR");
            lblSaldoPerfil.setText(String.format("%.2f€", u.getSaldo()));

            String iniciales = "";
            if (u.getNombre() != null && !u.getNombre().isEmpty()) iniciales += u.getNombre().charAt(0);
            if (u.getApellidos() != null && !u.getApellidos().isEmpty()) iniciales += u.getApellidos().charAt(0);
            lblIniciales.setText(iniciales.toUpperCase());
        }
    }

    /**
     * Abre el diálogo para editar los datos del perfil.
     */
    @FXML
    private void onEditarPerfil() {
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Perfil");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");

        TextField txtNombre = new TextField(u.getNombre());
        TextField txtApellidos = new TextField(u.getApellidos());
        TextField txtTelefono = new TextField(u.getTelefono());
        TextField txtEmail = new TextField(u.getEmail());

        // Estilos
        txtNombre.getStyleClass().add("dialog-textfield");
        txtApellidos.getStyleClass().add("dialog-textfield");
        txtTelefono.getStyleClass().add("dialog-textfield");
        txtEmail.getStyleClass().add("dialog-textfield");

        content.getChildren().addAll(
                crearCampoEdicion("Nombre", txtNombre),
                crearCampoEdicion("Apellidos", txtApellidos),
                crearCampoEdicion("Teléfono", txtTelefono),
                crearCampoEdicion("Email", txtEmail)
        );

        dialog.getDialogPane().setContent(content);

        ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardar);
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.getStyleClass().add("btn-reservar");

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnGuardar) {
                // ✅ VALIDACIÓN: TELÉFONO 9 DÍGITOS
                if (!txtTelefono.getText().matches("^\\d{9}$")) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato Incorrecto", "El teléfono debe tener 9 dígitos numéricos.");
                    return;
                }

                u.setNombre(txtNombre.getText());
                u.setApellidos(txtApellidos.getText());
                u.setTelefono(txtTelefono.getText());
                u.setEmail(txtEmail.getText());

                if (usuarioDAO.update(u)) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Perfil Actualizado", "Datos guardados correctamente.");
                    cargarDatosPerfil();
                    actualizarHeader(u);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se guardaron los cambios.");
                }
            }
        });
    }

    /**
     * Crea un campo de edición para el formulario.
     * @param label Etiqueta del campo.
     * @param field Campo de texto.
     * @return Un VBox con la etiqueta y el campo.
     */
    private VBox crearCampoEdicion(String label, TextField field) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("input-label");
        box.getChildren().addAll(lbl, field);
        return box;
    }

    /**
     * Abre el diálogo para añadir saldo a la cuenta.
     */
    @FXML
    private void onAnadirSaldo() {
        Usuario u = Sesion.getInstancia().getUsuarioLogueado();

        // NO DEJAR AÑADIR SALDO SIN MÉTODO DE PAGO
        if (u.getMetodoPago() == null || "SIN DEFINIR".equals(u.getMetodoPago()) || u.getMetodoPago().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Método de Pago Requerido",
                    "Debes establecer un método de pago antes de añadir saldo.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Añadir Saldo");
        dialog.setHeaderText("Recargar Monedero");
        dialog.setContentText("Cantidad a añadir (€):");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());

        dialog.showAndWait().ifPresent(cantidad -> {
            try {
                double monto = Double.parseDouble(cantidad);
                if (monto > 0) {
                    double nuevoSaldo = u.getSaldo() + monto;

                    if (usuarioDAO.actualizarSaldo(u.getIdUsuario(), nuevoSaldo)) {
                        u.setSaldo(nuevoSaldo);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Recarga Exitosa", "Se han añadido " + monto + "€ a tu cuenta.");
                        cargarDatosPerfil();
                        actualizarHeader(u);
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Fallo al actualizar saldo en BD.");
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Inválido", "Introduce una cantidad positiva.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Por favor, introduce un número válido.");
            }
        });
    }

    /**
     * Abre el diálogo para cambiar el método de pago.
     */
    @FXML
    private void onCambiarMetodoPago() {
        List<String> metodos = List.of("TARJETA", "PAYPAL", "BIZUM", "EFECTIVO");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("TARJETA", metodos);
        dialog.setTitle("Método de Pago");
        dialog.setHeaderText("Selecciona tu método de pago");
        dialog.setContentText("Método:");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());

        dialog.showAndWait().ifPresent(seleccion -> {
            Usuario u = Sesion.getInstancia().getUsuarioLogueado();
            u.setMetodoPago(seleccion);

            if (usuarioDAO.update(u)) {
                cargarDatosPerfil();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cambiar el método de pago.");
            }
        });
    }

    // ================================================================
    // 8. UTILIDADES
    // ================================================================

    /**
     * Cierra la sesión actual y vuelve a la pantalla de inicio de sesión.
     */
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

    /**
     * Muestra una ventana emergente de alerta.
     * @param tipo Tipo de alerta (INFORMACIÓN, ERROR, etc.).
     * @param titulo Título de la ventana.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}