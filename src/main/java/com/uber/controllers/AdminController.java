package com.uber.controllers;

import com.uber.dao.*;
import com.uber.enums.EstadoCuenta;
import com.uber.enums.EstadoVehiculo;
import com.uber.enums.Rol;
import com.uber.enums.TipoVehiculo;
import com.uber.model.Estacion;
import com.uber.model.Usuario;
import com.uber.model.Vehiculo;
import com.uber.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la vista del administrador.
 * Gestiona navegación, carga de datos y acciones CRUD sobre vehículos,
 * estaciones, usuarios y registros de mantenimiento.
 */
public class AdminController {

    /* ============================
       VISTAS (Pestañas principales)
       ============================ */
    @FXML private VBox vistaResumen;
    @FXML private VBox vistaVehiculos;
    @FXML private VBox vistaEstaciones;
    @FXML private VBox vistaMantenimiento;
    @FXML private VBox vistaUsuarios;

    /* ============================
       CONTENEDORES Y SECCIONES
       ============================ */
    @FXML private FlowPane contenedorEstaciones;
    @FXML private VBox contenedorMantenimiento;
    @FXML private VBox contenedorAlertas;

    /* ============================
       BOTONES DEL MENÚ LATERAL
       ============================ */
    @FXML private Button btnNavResumen;
    @FXML private Button btnNavVehiculos;
    @FXML private Button btnNavEstaciones;
    @FXML private Button btnNavMantenimiento;
    @FXML private Button btnNavUsuarios;

    /* ============================
       ETIQUETAS DEL RESUMEN
       ============================ */
    @FXML private Label lblTotalVehiculos, lblVehiculosDisponibles, lblTotalUsuarios, lblTotalEstaciones;
    @FXML private Label badgeDisponibles, badgeEnUso, badgeMantenimiento;

    /* ============================
       TABLA DE VEHÍCULOS
       ============================ */
    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colId;
    @FXML private TableColumn<Vehiculo, String> colTipo;
    @FXML private TableColumn<Vehiculo, String> colMarcaModelo;
    @FXML private TableColumn<Vehiculo, EstadoVehiculo> colEstado;
    @FXML private TableColumn<Vehiculo, Double> colBateria;
    @FXML private TableColumn<Vehiculo, Double> colKm;
    @FXML private TableColumn<Vehiculo, String> colEstacion;
    @FXML private TableColumn<Vehiculo, Void> colAcciones;

    /* ============================
       TABLA DE USUARIOS
       ============================ */
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colUserId;
    @FXML private TableColumn<Usuario, String> colUserNombre;
    @FXML private TableColumn<Usuario, String> colUserEmail;
    @FXML private TableColumn<Usuario, String> colUserTelefono;
    @FXML private TableColumn<Usuario, Double> colUserSaldo;
    @FXML private TableColumn<Usuario, EstadoCuenta> colUserEstado;
    @FXML private TableColumn<Usuario, Rol> colUserRol;
    @FXML private TableColumn<Usuario, Void> colUserAcciones;

    /* ============================
       ACCESO A DATOS
       ============================ */
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstacionDAO estacionDAO = new EstacionDAO();
    private final TieneDAO tieneDAO = new TieneDAO();
    private final MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();

    /**
     * Inicializa la interfaz al cargarse la vista.
     * Configura tablas y abre la pestaña principal.
     */
    @FXML
    public void initialize() {
        configurarTablaVehiculos();
        configurarTablaUsuarios();
        mostrarResumen();
    }

    /* ================================================================
       NAVEGACIÓN ENTRE PESTAÑAS
       ================================================================ */

    /** Oculta todas las vistas. */
    private void ocultarTodasLasVistas() {
        vistaResumen.setVisible(false);
        vistaVehiculos.setVisible(false);
        vistaEstaciones.setVisible(false);
        vistaMantenimiento.setVisible(false);
        vistaUsuarios.setVisible(false);
    }

    /**
     * Marca el botón del menú correspondiente a la sección activa.
     */
    private void resaltarBoton(Button botonActivo) {
        btnNavResumen.getStyleClass().remove("admin-nav-selected");
        btnNavVehiculos.getStyleClass().remove("admin-nav-selected");
        btnNavEstaciones.getStyleClass().remove("admin-nav-selected");
        btnNavMantenimiento.getStyleClass().remove("admin-nav-selected");
        if (btnNavUsuarios != null) btnNavUsuarios.getStyleClass().remove("admin-nav-selected");

        botonActivo.getStyleClass().add("admin-nav-selected");
    }

    @FXML
    void mostrarResumen() {
        ocultarTodasLasVistas();
        vistaResumen.setVisible(true);
        resaltarBoton(btnNavResumen);
        cargarDatosResumen();
    }

    @FXML
    void mostrarVehiculos() {
        ocultarTodasLasVistas();
        vistaVehiculos.setVisible(true);
        resaltarBoton(btnNavVehiculos);
        cargarTablaVehiculos();
    }

    @FXML
    void mostrarEstaciones() {
        ocultarTodasLasVistas();
        vistaEstaciones.setVisible(true);
        resaltarBoton(btnNavEstaciones);
        cargarEstaciones();
    }

    @FXML
    void mostrarMantenimiento() {
        ocultarTodasLasVistas();
        vistaMantenimiento.setVisible(true);
        resaltarBoton(btnNavMantenimiento);
        cargarHistorialMantenimiento();
    }

    @FXML
    void mostrarUsuarios() {
        ocultarTodasLasVistas();
        vistaUsuarios.setVisible(true);
        resaltarBoton(btnNavUsuarios);
        cargarTablaUsuarios();
    }

    /* ================================================================
       RESUMEN / DASHBOARD
       ================================================================ */

    /**
     * Carga estadísticas generales y avisos de estado del sistema.
     */
    private void cargarDatosResumen() {
        List<Vehiculo> listaVehiculos = vehiculoDAO.getAll();

        long disponibles = listaVehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.DISPONIBLE).count();
        long enUso = listaVehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.EN_USO).count();
        long mantenimiento = listaVehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.MANTENIMIENTO).count();

        lblTotalVehiculos.setText(String.valueOf(listaVehiculos.size()));
        lblVehiculosDisponibles.setText(String.valueOf(disponibles));
        lblTotalUsuarios.setText(String.valueOf(usuarioDAO.getAll().size()));
        lblTotalEstaciones.setText(String.valueOf(estacionDAO.getAll().size()));

        badgeDisponibles.setText(String.valueOf(disponibles));
        badgeEnUso.setText(String.valueOf(enUso));
        badgeMantenimiento.setText(String.valueOf(mantenimiento));

        contenedorAlertas.getChildren().clear();
        boolean hayAlertas = false;

        for (Vehiculo v : listaVehiculos) {
            if (v.getNivelBateria() < 20) {
                crearAlerta("Batería baja", v.getMarca() + " (" + v.getNivelBateria() + "%)", "alert-warning-box");
                hayAlertas = true;
            }
            if (v.getEstadoVehiculo() == EstadoVehiculo.INACTIVO) {
                crearAlerta("Vehículo inactivo", "El vehículo " + v.getMarca() + " no funciona", "alert-danger-box");
                hayAlertas = true;
            }
        }

        if (!hayAlertas) {
            Label ok = new Label("✅ Todo correcto");
            ok.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-padding: 10;");
            contenedorAlertas.getChildren().add(ok);
        }
    }

    /**
     * Crea un elemento visual de alerta dentro del panel de avisos.
     */
    private void crearAlerta(String titulo, String desc, String estilo) {
        VBox card = new VBox(2);
        card.getStyleClass().addAll("alert-item", estilo);

        Label t = new Label(titulo);
        t.getStyleClass().add("alert-title");

        Label d = new Label(desc);
        d.getStyleClass().add("alert-desc");

        card.getChildren().addAll(t, d);
        contenedorAlertas.getChildren().add(card);
    }

    /* ================================================================
       VEHÍCULOS
       ================================================================ */

    /**
     * Configura columnas, celdas personalizadas y acciones de la tabla.
     */
    private void configurarTablaVehiculos() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVehiculo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colMarcaModelo.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getMarca() + " " + cd.getValue().getModelo()));

        colKm.setCellValueFactory(new PropertyValueFactory<>("kilometraje"));
        colKm.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double km, boolean empty) {
                super.updateItem(km, empty);
                setText(empty || km == null ? null : String.format("%,.0f km", km).replace(",", "."));
            }
        });

        colEstacion.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getEstacion().getNombreEstacion()));

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoVehiculo"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoVehiculo estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(estado.name());
                    badge.getStyleClass().add("status-badge");

                    switch (estado) {
                        case DISPONIBLE -> badge.getStyleClass().add("status-disponible");
                        case EN_USO -> badge.getStyleClass().add("status-en-uso");
                        case MANTENIMIENTO -> badge.getStyleClass().add("status-mantenimiento");
                        default -> badge.getStyleClass().add("status-inactivo");
                    }

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colBateria.setCellValueFactory(new PropertyValueFactory<>("nivelBateria"));
        colBateria.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double bateria, boolean empty) {
                super.updateItem(bateria, empty);
                if (empty || bateria == null) {
                    setGraphic(null);
                } else {
                    ProgressBar pb = new ProgressBar(bateria / 100.0);
                    pb.setPrefWidth(70);
                    pb.setStyle("-fx-accent: #10B981;");

                    Label lbl = new Label(bateria.intValue() + "%");
                    lbl.setStyle("-fx-font-size: 11px;");

                    HBox box = new HBox(8, pb, lbl);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");

            {
                btnEdit.getStyleClass().addAll("action-btn", "btn-edit");
                btnDelete.getStyleClass().addAll("action-btn", "btn-delete");

                btnEdit.setOnAction(e ->
                        editarVehiculo(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e ->
                        borrarVehiculo(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    /** Carga los vehículos en la tabla. */
    private void cargarTablaVehiculos() {
        tablaVehiculos.setItems(FXCollections.observableArrayList(vehiculoDAO.getAll()));
    }

    @FXML
    private void onAddVehiculo() {
        mostrarFormularioVehiculo(null);
    }

    /**
     * Abre formulario para editar un vehículo concreto.
     */
    private void editarVehiculo(Vehiculo v) {
        mostrarFormularioVehiculo(v);
    }

    /**
     * Muestra el formulario de creación o edición de vehículo.
     */
    private void mostrarFormularioVehiculo(Vehiculo vEditar) {
        boolean esEdicion = (vEditar != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Vehículo" : "Añadir Vehículo");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox header = new VBox(5);
        header.getStyleClass().add("custom-dialog-header");

        Label lblTitulo = new Label(esEdicion ? "Editar Vehículo" : "Nuevo Vehículo");
        lblTitulo.getStyleClass().add("dialog-title-text");

        Label lblSubtitulo = new Label(esEdicion
                ? "Modifica los datos del vehículo"
                : "Registra un nuevo vehículo en la flota");
        lblSubtitulo.getStyleClass().add("dialog-subtitle-text");

        header.getChildren().addAll(lblTitulo, lblSubtitulo);

        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");

        ComboBox<TipoVehiculo> comboTipo =
                new ComboBox<>(FXCollections.observableArrayList(TipoVehiculo.values()));
        comboTipo.setMaxWidth(Double.MAX_VALUE);
        comboTipo.setPromptText("Tipo de vehículo...");
        comboTipo.getStyleClass().add("dialog-textfield");

        TextField txtMarca = new TextField();
        txtMarca.setPromptText("Marca");
        txtMarca.getStyleClass().add("dialog-textfield");

        TextField txtModelo = new TextField();
        txtModelo.setPromptText("Modelo");
        txtModelo.getStyleClass().add("dialog-textfield");

        ComboBox<Estacion> comboEstacion =
                new ComboBox<>(FXCollections.observableArrayList(estacionDAO.getAll()));
        comboEstacion.setMaxWidth(Double.MAX_VALUE);
        comboEstacion.setPromptText("Estación...");
        comboEstacion.getStyleClass().add("dialog-textfield");

        Callback<ListView<Estacion>, ListCell<Estacion>> factoryEstacion = lv -> new ListCell<>() {
            @Override protected void updateItem(Estacion e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null :
                        "📍 " + e.getNombreEstacion() + " (" + e.getCiudad() + ")");
            }
        };

        comboEstacion.setCellFactory(factoryEstacion);
        comboEstacion.setButtonCell(factoryEstacion.call(null));

        ComboBox<EstadoVehiculo> comboEstado =
                new ComboBox<>(FXCollections.observableArrayList(EstadoVehiculo.values()));
        comboEstado.setMaxWidth(Double.MAX_VALUE);
        comboEstado.getStyleClass().add("dialog-textfield");

        TextField txtBateria = new TextField();
        txtBateria.getStyleClass().add("dialog-textfield");

        TextField txtKm = new TextField();
        txtKm.getStyleClass().add("dialog-textfield");

        if (esEdicion) {
            comboTipo.setValue(vEditar.getTipo());
            txtMarca.setText(vEditar.getMarca());
            txtModelo.setText(vEditar.getModelo());

            for (Estacion e : comboEstacion.getItems()) {
                if (e.getIdEstacion() == vEditar.getEstacion().getIdEstacion()) {
                    comboEstacion.setValue(e);
                    break;
                }
            }

            comboEstado.setValue(vEditar.getEstadoVehiculo());
            txtBateria.setText(String.valueOf(vEditar.getNivelBateria()));
            txtKm.setText(String.valueOf(vEditar.getKilometraje()));
        } else {
            comboTipo.getSelectionModel().selectFirst();
        }

        content.getChildren().addAll(
                crearCampo("Tipo", comboTipo),
                crearCampo("Marca", txtMarca),
                crearCampo("Modelo", txtModelo),
                crearCampo("Estación de Origen", comboEstacion)
        );

        if (esEdicion) {
            content.getChildren().addAll(
                    new Separator(),
                    crearCampo("Estado Actual", comboEstado),
                    crearCampo("Nivel de Batería (%)", txtBateria),
                    crearCampo("Kilometraje", txtKm)
            );
        }

        VBox root = new VBox(header, content);
        dialog.getDialogPane().setContent(root);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardar);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.getStyleClass().add("btn-reservar");

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
                if (txtMarca.getText().isEmpty() || txtModelo.getText().isEmpty()
                        || comboEstacion.getValue() == null) {
                    mostrarError("Marca, modelo y estación son obligatorios.");
                    return;
                }

                try {
                    Vehiculo v = esEdicion ? vEditar : new Vehiculo();
                    v.setTipo(comboTipo.getValue());
                    v.setMarca(txtMarca.getText());
                    v.setModelo(txtModelo.getText());
                    v.setEstacion(comboEstacion.getValue());

                    if (esEdicion) {
                        v.setEstadoVehiculo(comboEstado.getValue());
                        v.setNivelBateria(Double.parseDouble(txtBateria.getText()));
                        v.setKilometraje(Double.parseDouble(txtKm.getText()));
                        vehiculoDAO.update(v);
                    } else {
                        v.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
                        v.setNivelBateria(100.0);
                        v.setKilometraje(0.0);
                        vehiculoDAO.insert(v);
                    }

                    cargarTablaVehiculos();
                    cargarDatosResumen();
                    mostrarExito("Vehículo guardado correctamente.");

                } catch (Exception e) {
                    mostrarError("Error al guardar. Revisa que los valores numéricos sean válidos.");
                }
            }
        });
    }

    /**
     * Elimina un vehículo tras confirmación del usuario.
     */
    private void borrarVehiculo(Vehiculo v) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Vehículo");
        alert.setHeaderText("¿Seguro que quieres borrar este vehículo?");
        alert.setContentText(v.getMarca() + " " + v.getModelo());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (vehiculoDAO.delete(v.getIdVehiculo())) {
                cargarTablaVehiculos();
                cargarDatosResumen();
                mostrarMensaje("Vehículo eliminado.");
            } else {
                mostrarError("No se pudo borrar. Puede tener reservas asociadas.");
            }
        }
    }

    /* ================================================================
       ESTACIONES
       ================================================================ */

    /** Carga todas las estaciones con tarjetas informativas. */
    private void cargarEstaciones() {
        contenedorEstaciones.getChildren().clear();

        List<Estacion> lista = estacionDAO.getAll();
        List<Vehiculo> vehiculos = vehiculoDAO.getAll();

        for (Estacion e : lista) {
            long numVehiculos = vehiculos.stream()
                    .filter(v -> v.getEstacion().getIdEstacion() == e.getIdEstacion())
                    .count();

            contenedorEstaciones.getChildren().add(crearTarjetaEstacion(e, numVehiculos));
        }
    }

    /**
     * Construye la tarjeta visual de una estación.
     */
    private VBox crearTarjetaEstacion(Estacion e, long numVehiculos) {
        VBox card = new VBox(10);
        card.getStyleClass().add("station-card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📍");
        icon.setStyle("-fx-font-size: 20px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✎");
        Button btnDel = new Button("🗑");

        btnEdit.getStyleClass().addAll("action-btn", "btn-edit");
        btnDel.getStyleClass().addAll("action-btn", "btn-delete");

        btnEdit.setOnAction(ev -> mostrarFormularioEstacion(e));
        btnDel.setOnAction(ev -> borrarEstacion(e));

        header.getChildren().addAll(icon, spacer, btnEdit, btnDel);

        Label lblNombre = new Label(e.getNombreEstacion());
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label lblInfo = new Label(e.getCiudad() + " - " + e.getDireccion());

        Label badge = new Label(numVehiculos + " vehículos");
        badge.setStyle("-fx-background-color: #0E402D; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");

        card.getChildren().addAll(header, lblNombre, lblInfo, badge);

        return card;
    }

    @FXML
    private void onAddEstacion() {
        mostrarFormularioEstacion(null);
    }

    /**
     * Muestra el formulario para crear o editar una estación.
     */
    private void mostrarFormularioEstacion(Estacion estEditar) {
        boolean esEdicion = estEditar != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Estación" : "Nueva Estación");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox header = new VBox(5);
        header.getStyleClass().add("custom-dialog-header");

        Label lblTitulo = new Label(esEdicion ? "Editar Estación" : "Nueva Estación");
        lblTitulo.getStyleClass().add("dialog-title-text");

        Label lblSubtitulo = new Label("Gestiona los puntos de aparcamiento");
        lblSubtitulo.getStyleClass().add("dialog-subtitle-text");

        header.getChildren().addAll(lblTitulo, lblSubtitulo);

        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");

        TextField txtNombre = new TextField(esEdicion ? estEditar.getNombreEstacion() : "");
        txtNombre.setPromptText("Nombre de la estación");
        txtNombre.getStyleClass().add("dialog-textfield");

        TextField txtCiudad = new TextField(esEdicion ? estEditar.getCiudad() : "");
        txtCiudad.setPromptText("Ciudad");
        txtCiudad.getStyleClass().add("dialog-textfield");

        TextField txtDir = new TextField(esEdicion ? estEditar.getDireccion() : "");
        txtDir.setPromptText("Dirección completa");
        txtDir.getStyleClass().add("dialog-textfield");

        Spinner<Integer> spinCap = new Spinner<>(1, 100, esEdicion ? estEditar.getCapacidad() : 10);
        spinCap.setEditable(true);
        spinCap.setMaxWidth(Double.MAX_VALUE);
        spinCap.getStyleClass().add("dialog-textfield");

        content.getChildren().addAll(
                crearCampo("Nombre", txtNombre),
                crearCampo("Ciudad", txtCiudad),
                crearCampo("Dirección", txtDir),
                crearCampo("Capacidad", spinCap)
        );

        VBox root = new VBox(header, content);
        dialog.getDialogPane().setContent(root);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardar);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.getStyleClass().add("btn-reservar");

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
                if (txtNombre.getText().isEmpty() || txtCiudad.getText().isEmpty()) {
                    mostrarError("El nombre y la ciudad son obligatorios.");
                    return;
                }

                Estacion e = esEdicion ? estEditar : new Estacion();
                e.setNombreEstacion(txtNombre.getText());
                e.setCiudad(txtCiudad.getText());
                e.setDireccion(txtDir.getText());
                e.setCapacidad(spinCap.getValue());

                if (esEdicion) {
                    estacionDAO.update(e);
                    mostrarExito("Estación actualizada.");
                } else {
                    estacionDAO.insert(e);
                    mostrarExito("Nueva estación creada.");
                }

                cargarEstaciones();
                cargarDatosResumen();
            }
        });
    }

    /**
     * Elimina una estación si es posible.
     */
    private void borrarEstacion(Estacion e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Estación");
        alert.setContentText("¿Eliminar " + e.getNombreEstacion() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (estacionDAO.delete(e.getIdEstacion())) {
                cargarEstaciones();
                cargarDatosResumen();
            } else {
                mostrarError("No se puede borrar si tiene vehículos asociados.");
            }
        }
    }

    /* ================================================================
       MANTENIMIENTO
       ================================================================ */

    /** Carga el historial completo ordenado por fecha. */
    private void cargarHistorialMantenimiento() {
        if (contenedorMantenimiento == null) return;

        contenedorMantenimiento.getChildren().clear();
        List<com.uber.model.Tiene> historial = tieneDAO.getAll();

        if (historial.isEmpty()) {
            contenedorMantenimiento.getChildren().add(new Label("No hay registros."));
            return;
        }

        historial.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));

        for (com.uber.model.Tiene t : historial) {
            contenedorMantenimiento.getChildren().add(crearTarjetaMantenimiento(t));
        }
    }

    /**
     * Construye la tarjeta visual de un registro de mantenimiento.
     */
    private VBox crearTarjetaMantenimiento(com.uber.model.Tiene t) {
        VBox card = new VBox(10);
        card.getStyleClass().add("maintenance-card");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        String nombreCoche = "Vehículo #" + t.getVehiculo().getIdVehiculo();
        Vehiculo vFull = vehiculoDAO.getById(t.getVehiculo().getIdVehiculo());
        if (vFull != null) nombreCoche = vFull.getMarca() + " " + vFull.getModelo();

        Label lblCoche = new Label(nombreCoche);
        lblCoche.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String tipoMant = t.getMantenimiento().getTipo() != null
                ? t.getMantenimiento().getTipo().toString()
                : "MANTENIMIENTO";

        Label badge = new Label(tipoMant);
        badge.setStyle("-fx-background-color: #FFC107; -fx-padding: 5 10; -fx-background-radius: 15;");

        header.getChildren().addAll(lblCoche, spacer, badge);

        Label lblDetalles = new Label(
                "Fecha: " + t.getFechaHora().toLocalDate()
                        + " | Coste: " + t.getCoste() + "€");

        Label lblNotas = new Label("Notas: " + (t.getNotas() != null ? t.getNotas() : "-"));

        card.getChildren().addAll(header, lblDetalles, lblNotas);
        return card;
    }

    @FXML
    private void onAddMantenimiento() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Mantenimiento");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox header = new VBox(5);
        header.getStyleClass().add("custom-dialog-header");

        Label lblTitulo = new Label("Nuevo Mantenimiento");
        lblTitulo.getStyleClass().add("dialog-title-text");

        Label lblSubtitulo = new Label("Selecciona el vehículo y el tipo de intervención");
        lblSubtitulo.getStyleClass().add("dialog-subtitle-text");

        header.getChildren().addAll(lblTitulo, lblSubtitulo);

        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");

        ComboBox<Vehiculo> comboVehiculo =
                new ComboBox<>(FXCollections.observableArrayList(vehiculoDAO.getAll()));
        comboVehiculo.setMaxWidth(Double.MAX_VALUE);
        comboVehiculo.setPromptText("Vehículo...");
        comboVehiculo.getStyleClass().add("dialog-textfield");

        ComboBox<com.uber.model.Mantenimiento> comboTipo =
                new ComboBox<>(FXCollections.observableArrayList(mantenimientoDAO.getAll()));
        comboTipo.setMaxWidth(Double.MAX_VALUE);
        comboTipo.setPromptText("Tipo de mantenimiento...");
        comboTipo.getStyleClass().add("dialog-textfield");

        TextField txtCoste = new TextField();
        txtCoste.setPromptText("0.00");
        txtCoste.getStyleClass().add("dialog-textfield");

        TextArea txtNotas = new TextArea();
        txtNotas.setPromptText("Detalles adicionales...");
        txtNotas.setPrefRowCount(3);
        txtNotas.getStyleClass().add("dialog-textfield");

        Callback<ListView<Vehiculo>, ListCell<Vehiculo>> factoryVehiculo = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Vehiculo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getMarca() + " " + item.getModelo()
                                + " - " + item.getEstacion().getNombreEstacion());
            }
        };

        comboVehiculo.setCellFactory(factoryVehiculo);
        comboVehiculo.setButtonCell(factoryVehiculo.call(null));

        Callback<ListView<com.uber.model.Mantenimiento>, ListCell<com.uber.model.Mantenimiento>> factoryMant =
                lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(com.uber.model.Mantenimiento item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null :
                                item.getTipo() + " - " + item.getDescripcion());
                    }
                };

        comboTipo.setCellFactory(factoryMant);
        comboTipo.setButtonCell(factoryMant.call(null));

        content.getChildren().addAll(
                crearCampo("Vehículo", comboVehiculo),
                crearCampo("Tipo de Mantenimiento", comboTipo),
                crearCampo("Coste (€)", txtCoste),
                crearCampo("Notas", txtNotas)
        );

        VBox root = new VBox(header, content);
        dialog.getDialogPane().setContent(root);

        ButtonType btnGuardar = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardar);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.getStyleClass().add("btn-reservar");

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
                if (comboVehiculo.getValue() == null
                        || comboTipo.getValue() == null
                        || txtCoste.getText().isEmpty()) {
                    mostrarError("Debes rellenar los campos obligatorios.");
                    return;
                }

                try {
                    com.uber.model.Tiene registro = new com.uber.model.Tiene();
                    registro.setVehiculo(comboVehiculo.getValue());
                    registro.setMantenimiento(comboTipo.getValue());
                    registro.setFechaHora(java.time.LocalDateTime.now());
                    registro.setCoste(Double.parseDouble(txtCoste.getText()));
                    registro.setNotas(txtNotas.getText());

                    if (tieneDAO.insert(registro)) {
                        Vehiculo v = comboVehiculo.getValue();
                        v.setEstadoVehiculo(EstadoVehiculo.MANTENIMIENTO);
                        vehiculoDAO.update(v);

                        mostrarExito("Mantenimiento registrado.");
                        cargarHistorialMantenimiento();
                        cargarDatosResumen();
                    }

                } catch (NumberFormatException e) {
                    mostrarError("El coste debe ser un número válido.");
                }
            }
        });
    }

    /**
     * Crea un bloque simple con una etiqueta y un campo de formulario.
     */
    private VBox crearCampo(String titulo, javafx.scene.Node campo) {
        VBox box = new VBox(5);
        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("input-label");
        box.getChildren().addAll(lbl, campo);
        return box;
    }

    /* ================================================================
       USUARIOS
       ================================================================ */

    /** Configura las columnas y estilos de la tabla de usuarios. */
    private void configurarTablaUsuarios() {
        if (tablaUsuarios == null) return;

        colUserId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

        colUserNombre.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getNombre() + " "
                        + cd.getValue().getApellidos()));

        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        colUserSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colUserSaldo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double saldo, boolean empty) {
                super.updateItem(saldo, empty);
                if (empty || saldo == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f€", saldo));
                    getStyleClass().add("table-balance-text");
                }
            }
        });

        colUserEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCuenta"));
        colUserEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoCuenta estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(estado.name());
                    badge.getStyleClass().add("status-badge");

                    switch (estado) {
                        case ACTIVO -> badge.getStyleClass().add("user-status-activo");
                        case SUSPENDIDO -> badge.getStyleClass().add("user-status-suspendido");
                        case BLOQUEADO -> badge.getStyleClass().add("user-status-bloqueado");
                    }

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colUserRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colUserAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");

            {
                btnEdit.getStyleClass().addAll("action-btn", "btn-edit");
                btnDelete.getStyleClass().addAll("action-btn", "btn-delete");

                btnEdit.setOnAction(e -> onEditarUsuario(
                        getTableView().getItems().get(getIndex())));

                btnDelete.setOnAction(e -> onBorrarUsuario(
                        getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    /** Carga los usuarios registrados, excluyendo administradores. */
    private void cargarTablaUsuarios() {
        if (tablaUsuarios == null) return;

        List<Usuario> todos = usuarioDAO.getAll();
        List<Usuario> soloClientes = todos.stream()
                .filter(u -> u.getRol() != Rol.ADMIN)
                .collect(Collectors.toList());

        tablaUsuarios.setItems(FXCollections.observableArrayList(soloClientes));
    }

    /**
     * Cambia el estado de un usuario.
     */
    private void onEditarUsuario(Usuario u) {
        List<EstadoCuenta> estados = List.of(EstadoCuenta.values());

        ChoiceDialog<EstadoCuenta> dialog =
                new ChoiceDialog<>(u.getEstadoCuenta(), estados);
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Cambiar estado de " + u.getNombre());

        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/uber/css/style.css").toExternalForm());

        dialog.showAndWait().ifPresent(nuevoEstado -> {
            u.setEstadoCuenta(nuevoEstado);
            if (usuarioDAO.update(u)) {
                mostrarExito("Estado actualizado.");
                cargarTablaUsuarios();
            } else {
                mostrarError("No se pudo actualizar.");
            }
        });
    }

    /**
     * Elimina un usuario si no tiene restricciones.
     */
    private void onBorrarUsuario(Usuario u) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Usuario");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminará a: " + u.getNombre());

        alert.getDialogPane().getStylesheets()
                .add(getClass().getResource("/com/uber/css/style.css").toExternalForm());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (usuarioDAO.delete(u.getIdUsuario())) {
                mostrarExito("Usuario eliminado.");
                cargarTablaUsuarios();
                cargarDatosResumen();
            } else {
                mostrarError("No se pudo borrar. Puede tener reservas asociadas.");
            }
        }
    }

    /* ================================================================
       SESIÓN / UTILIDADES
       ================================================================ */

    /**
     * Cierra la sesión actual y vuelve al login.
     */
    @FXML
    private void onCerrarSesion() {
        Sesion.getInstancia().logOut();

        try {
            Stage stage = (Stage) lblTotalVehiculos.getScene().getWindow();
            Scene scene = new Scene(
                    FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Muestra un mensaje informativo. */
    private void mostrarMensaje(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }

    /** Muestra un mensaje de éxito. */
    private void mostrarExito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    /** Muestra un mensaje de error. */
    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
