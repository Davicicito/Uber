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
 * Controlador principal para la vista del Administrador.
 * Gestiona las pestañas de Resumen, Vehículos, Estaciones, Mantenimiento y Usuarios.
 */
public class AdminController {

    // --- VISTAS (PESTAÑAS) ---
    @FXML private VBox vistaResumen;
    @FXML private VBox vistaVehiculos;
    @FXML private VBox vistaEstaciones;
    @FXML private VBox vistaMantenimiento;
    @FXML private VBox vistaUsuarios;

    // --- CONTENEDORES ---
    @FXML private FlowPane contenedorEstaciones;
    @FXML private VBox contenedorMantenimiento;
    @FXML private VBox contenedorAlertas;

    // --- BOTONES DE NAVEGACIÓN ---
    @FXML private Button btnNavResumen;
    @FXML private Button btnNavVehiculos;
    @FXML private Button btnNavEstaciones;
    @FXML private Button btnNavMantenimiento;
    @FXML private Button btnNavUsuarios;

    // --- ETIQUETAS DEL RESUMEN ---
    @FXML private Label lblTotalVehiculos, lblVehiculosDisponibles, lblTotalUsuarios, lblTotalEstaciones;
    @FXML private Label badgeDisponibles, badgeEnUso, badgeMantenimiento;

    // --- TABLA DE VEHÍCULOS ---
    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colId;
    @FXML private TableColumn<Vehiculo, String> colTipo;
    @FXML private TableColumn<Vehiculo, String> colMarcaModelo;
    @FXML private TableColumn<Vehiculo, EstadoVehiculo> colEstado;
    @FXML private TableColumn<Vehiculo, Double> colBateria;
    @FXML private TableColumn<Vehiculo, Double> colKm;
    @FXML private TableColumn<Vehiculo, String> colEstacion;
    @FXML private TableColumn<Vehiculo, Void> colAcciones;

    // --- TABLA DE USUARIOS ---
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colUserId;
    @FXML private TableColumn<Usuario, String> colUserNombre;
    @FXML private TableColumn<Usuario, String> colUserEmail;
    @FXML private TableColumn<Usuario, String> colUserTelefono;
    @FXML private TableColumn<Usuario, Double> colUserSaldo;
    @FXML private TableColumn<Usuario, EstadoCuenta> colUserEstado;
    @FXML private TableColumn<Usuario, Rol> colUserRol;
    @FXML private TableColumn<Usuario, Void> colUserAcciones;

    // --- OBJETOS DE ACCESO A DATOS (DAOs) ---
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstacionDAO estacionDAO = new EstacionDAO();
    private final TieneDAO tieneDAO = new TieneDAO();
    private final MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();

    /**
     * Método que se ejecuta al cargar la ventana.
     * Inicializa las tablas y muestra la pestaña principal.
     */
    @FXML
    public void initialize() {
        // Preparar las columnas de las tablas
        configurarTablaVehiculos();
        configurarTablaUsuarios();

        // Empezar mostrando el resumen
        mostrarResumen();
    }

    // ================================================================
    //  GESTIÓN DE PESTAÑAS (NAVEGACIÓN)
    // ================================================================

    /**
     * Oculta todos los paneles para poder mostrar solo el seleccionado.
     */
    private void ocultarTodasLasVistas() {
        vistaResumen.setVisible(false);
        vistaVehiculos.setVisible(false);
        vistaEstaciones.setVisible(false);
        vistaMantenimiento.setVisible(false);
        vistaUsuarios.setVisible(false);
    }

    /**
     * Cambia el estilo del botón del menú para saber en qué pestaña estamos.
     */
    private void resaltarBoton(Button botonActivo) {
        // Quitar estilo a todos
        btnNavResumen.getStyleClass().remove("admin-nav-selected");
        btnNavVehiculos.getStyleClass().remove("admin-nav-selected");
        btnNavEstaciones.getStyleClass().remove("admin-nav-selected");
        btnNavMantenimiento.getStyleClass().remove("admin-nav-selected");
        if(btnNavUsuarios != null) btnNavUsuarios.getStyleClass().remove("admin-nav-selected");

        // Poner estilo al activo
        botonActivo.getStyleClass().add("admin-nav-selected");
    }

    // --- Métodos para los botones del menú ---

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

    // ================================================================
    //  PESTAÑA 1: RESUMEN (DASHBOARD)
    // ================================================================

    /**
     * Carga los datos generales y comprueba si hay alertas.
     */
    private void cargarDatosResumen() {
        List<Vehiculo> listaVehiculos = vehiculoDAO.getAll();

        // Contamos los vehículos según su estado
        long disponibles = 0;
        long enUso = 0;
        long mantenimiento = 0;

        for (Vehiculo v : listaVehiculos) {
            if (v.getEstadoVehiculo() == EstadoVehiculo.DISPONIBLE) disponibles++;
            else if (v.getEstadoVehiculo() == EstadoVehiculo.EN_USO) enUso++;
            else if (v.getEstadoVehiculo() == EstadoVehiculo.MANTENIMIENTO) mantenimiento++;
        }

        // Actualizamos los textos de la pantalla
        lblTotalVehiculos.setText(String.valueOf(listaVehiculos.size()));
        lblVehiculosDisponibles.setText(String.valueOf(disponibles));
        lblTotalUsuarios.setText(String.valueOf(usuarioDAO.getAll().size()));
        lblTotalEstaciones.setText(String.valueOf(estacionDAO.getAll().size()));

        badgeDisponibles.setText(String.valueOf(disponibles));
        badgeEnUso.setText(String.valueOf(enUso));
        badgeMantenimiento.setText(String.valueOf(mantenimiento));

        // Comprobar alertas (batería baja o inactivos)
        contenedorAlertas.getChildren().clear();
        boolean hayAlertas = false;

        for (Vehiculo v : listaVehiculos) {
            if (v.getNivelBateria() < 20) {
                crearAlerta("⚠️ Batería baja", v.getMarca() + " (" + v.getNivelBateria() + "%)", "alert-warning-box");
                hayAlertas = true;
            }
            if (v.getEstadoVehiculo() == EstadoVehiculo.INACTIVO) {
                crearAlerta("⛔ Vehículo inactivo", "El vehículo " + v.getMarca() + " no funciona", "alert-danger-box");
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
     * Crea una cajita de alerta en el panel lateral.
     */
    private void crearAlerta(String titulo, String desc, String estilo) {
        VBox card = new VBox(2);
        card.getStyleClass().addAll("alert-item", estilo);
        Label t = new Label(titulo); t.getStyleClass().add("alert-title");
        Label d = new Label(desc); d.getStyleClass().add("alert-desc");
        card.getChildren().addAll(t, d);
        contenedorAlertas.getChildren().add(card);
    }

    // ================================================================
    //  PESTAÑA 2: VEHÍCULOS
    // ================================================================

    /**
     * Configura cómo se muestran los datos en la tabla de vehículos.
     */
    private void configurarTablaVehiculos() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVehiculo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Juntar marca y modelo
        colMarcaModelo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMarca() + " " + cellData.getValue().getModelo())
        );

        // Formato para los kilómetros
        colKm.setCellValueFactory(new PropertyValueFactory<>("kilometraje"));
        colKm.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double km, boolean empty) {
                super.updateItem(km, empty);
                if (empty || km == null) setText(null);
                else setText(String.format("%,.0f km", km).replace(",", "."));
            }
        });

        // Nombre de la estación
        colEstacion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstacion().getNombreEstacion())
        );

        // Colores para el estado
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
                    if (estado == EstadoVehiculo.DISPONIBLE) badge.getStyleClass().add("status-disponible");
                    else if (estado == EstadoVehiculo.EN_USO) badge.getStyleClass().add("status-en-uso");
                    else if (estado == EstadoVehiculo.MANTENIMIENTO) badge.getStyleClass().add("status-mantenimiento");
                    else badge.getStyleClass().add("status-inactivo");

                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Barra de Batería visual
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

                    // Se establece un color verde estándar siempre
                    pb.setStyle("-fx-accent: #10B981;");

                    Label lbl = new Label(bateria.intValue() + "%");
                    lbl.setStyle("-fx-font-size: 11px;");

                    HBox box = new HBox(8, pb, lbl);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        // Botones de Editar y Borrar
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");

            {
                btnEdit.getStyleClass().addAll("action-btn", "btn-edit");
                btnDelete.getStyleClass().addAll("action-btn", "btn-delete");

                btnEdit.setOnAction(e -> editarVehiculo(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> borrarVehiculo(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox box = new HBox(5, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    /**
     * Carga la lista de vehículos en la tabla.
     */
    private void cargarTablaVehiculos() {
        tablaVehiculos.setItems(FXCollections.observableArrayList(vehiculoDAO.getAll()));
    }

    /**
     * Botón para añadir un vehículo nuevo.
     */
    @FXML
    private void onAddVehiculo() {
        mostrarFormularioVehiculo(null); // null significa que es nuevo
    }

    /**
     * Botón para editar un vehículo existente.
     */
    private void editarVehiculo(Vehiculo v) {
        mostrarFormularioVehiculo(v); // Pasamos el vehículo a editar
    }

    /**
     * Abre la ventana para rellenar los datos del vehículo.
     */
    private void mostrarFormularioVehiculo(Vehiculo vEditar) {
        boolean esEdicion = (vEditar != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Vehículo" : "Añadir Vehículo");
        dialog.setHeaderText(null);

        // Cargar estilos
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");
        content.setPrefWidth(400);

        Label titulo = new Label(esEdicion ? "Editar Vehículo" : "Nuevo Vehículo");
        titulo.getStyleClass().add("dialog-heading");

        // Crear los campos
        ComboBox<TipoVehiculo> comboTipo = new ComboBox<>(FXCollections.observableArrayList(TipoVehiculo.values()));
        comboTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtMarca = new TextField(); txtMarca.setPromptText("Marca");
        TextField txtModelo = new TextField(); txtModelo.setPromptText("Modelo");

        ComboBox<Estacion> comboEstacion = new ComboBox<>(FXCollections.observableArrayList(estacionDAO.getAll()));
        comboEstacion.setMaxWidth(Double.MAX_VALUE);

        // Que se vea el nombre de la estación en el desplegable
        comboEstacion.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Estacion e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty ? "" : e.getNombreEstacion());
            }
        });
        comboEstacion.setButtonCell(comboEstacion.getCellFactory().call(null));

        // Campos extra (solo visibles al editar)
        ComboBox<EstadoVehiculo> comboEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoVehiculo.values()));
        TextField txtBateria = new TextField();
        TextField txtKm = new TextField();

        // Rellenar datos si estamos editando
        if (esEdicion) {
            comboTipo.setValue(vEditar.getTipo());
            txtMarca.setText(vEditar.getMarca());
            txtModelo.setText(vEditar.getModelo());

            // Seleccionar la estación correcta
            for(Estacion e : comboEstacion.getItems()) {
                if(e.getIdEstacion() == vEditar.getEstacion().getIdEstacion()) {
                    comboEstacion.setValue(e);
                    break;
                }
            }
            comboEstado.setValue(vEditar.getEstadoVehiculo());
            txtBateria.setText(String.valueOf(vEditar.getNivelBateria()));
            txtKm.setText(String.valueOf(vEditar.getKilometraje()));
        } else {
            // Valores por defecto
            comboTipo.getSelectionModel().selectFirst();
            if(!comboEstacion.getItems().isEmpty()) comboEstacion.getSelectionModel().selectFirst();
        }

        // Añadir los campos a la ventana
        content.getChildren().addAll(
                titulo,
                new Label("Tipo:"), comboTipo,
                new Label("Marca:"), txtMarca,
                new Label("Modelo:"), txtModelo,
                new Label("Estación:"), comboEstacion
        );

        if (esEdicion) {
            content.getChildren().addAll(
                    new Label("Estado:"), comboEstado,
                    new Label("Batería (%):"), txtBateria,
                    new Label("Kilometraje:"), txtKm
            );
        }

        dialog.getDialogPane().setContent(content);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardar);

        // Guardar los datos
        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
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

                    // Actualizar tablas
                    cargarTablaVehiculos();
                    cargarDatosResumen();
                    mostrarMensaje("Guardado correctamente");

                } catch (Exception e) {
                    mostrarError("Error al guardar: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Elimina un vehículo preguntando antes.
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
                mostrarMensaje("Vehículo eliminado");
            } else {
                mostrarError("No se pudo borrar (puede tener reservas)");
            }
        }
    }

    // ================================================================
    //  PESTAÑA 3: ESTACIONES
    // ================================================================

    /**
     * Carga las tarjetas de las estaciones.
     */
    private void cargarEstaciones() {
        contenedorEstaciones.getChildren().clear();
        List<Estacion> lista = estacionDAO.getAll();
        List<Vehiculo> vehiculos = vehiculoDAO.getAll();

        for (Estacion e : lista) {
            // Contar coches en esta estación
            long numVehiculos = vehiculos.stream()
                    .filter(v -> v.getEstacion().getIdEstacion() == e.getIdEstacion())
                    .count();

            contenedorEstaciones.getChildren().add(crearTarjetaEstacion(e, numVehiculos));
        }
    }

    /**
     * Dibuja la tarjeta de una estación.
     */
    private VBox crearTarjetaEstacion(Estacion e, long numVehiculos) {
        VBox card = new VBox(10);
        card.getStyleClass().add("station-card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📍");
        icon.setStyle("-fx-font-size: 20px;");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botones de editar y borrar
        Button btnEdit = new Button("✎");
        Button btnDel = new Button("🗑");
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

    @FXML private void onAddEstacion() { mostrarFormularioEstacion(null); }

    /**
     * Ventana para crear o editar estación.
     */
    private void mostrarFormularioEstacion(Estacion estEditar) {
        boolean esEdicion = (estEditar != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Datos Estación");
        dialog.setHeaderText(null);

        VBox content = new VBox(10);
        TextField txtNombre = new TextField(esEdicion ? estEditar.getNombreEstacion() : "");
        TextField txtCiudad = new TextField(esEdicion ? estEditar.getCiudad() : "");
        TextField txtDir = new TextField(esEdicion ? estEditar.getDireccion() : "");
        Spinner<Integer> spinCap = new Spinner<>(1, 100, esEdicion ? estEditar.getCapacidad() : 10);

        content.getChildren().addAll(
                new Label("Nombre:"), txtNombre,
                new Label("Ciudad:"), txtCiudad,
                new Label("Dirección:"), txtDir,
                new Label("Capacidad:"), spinCap
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                Estacion e = esEdicion ? estEditar : new Estacion();
                e.setNombreEstacion(txtNombre.getText());
                e.setCiudad(txtCiudad.getText());
                e.setDireccion(txtDir.getText());
                e.setCapacidad(spinCap.getValue());

                if (esEdicion) estacionDAO.update(e);
                else estacionDAO.insert(e);

                cargarEstaciones();
                cargarDatosResumen();
            }
        });
    }

    private void borrarEstacion(Estacion e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Estación");
        alert.setContentText("¿Eliminar " + e.getNombreEstacion() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (estacionDAO.delete(e.getIdEstacion())) {
                cargarEstaciones();
                cargarDatosResumen();
            } else {
                mostrarError("No se puede borrar si tiene vehículos asignados.");
            }
        }
    }

    // ================================================================
    //  PESTAÑA 4: MANTENIMIENTO
    // ================================================================

    /**
     * Carga el historial de mantenimiento.
     */
    private void cargarHistorialMantenimiento() {
        if(contenedorMantenimiento == null) return;
        contenedorMantenimiento.getChildren().clear();
        List<com.uber.model.Tiene> historial = tieneDAO.getAll();

        if (historial.isEmpty()) {
            contenedorMantenimiento.getChildren().add(new Label("No hay registros."));
            return;
        }

        // Ordenar por fecha (más reciente primero)
        historial.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));

        for (com.uber.model.Tiene t : historial) {
            contenedorMantenimiento.getChildren().add(crearTarjetaMantenimiento(t));
        }
    }

    /**
     * Dibuja la tarjeta de un mantenimiento.
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

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        String tipoMant = "MANTENIMIENTO";
        if (t.getMantenimiento().getTipo() != null) {
            tipoMant = t.getMantenimiento().getTipo().toString();
        }
        Label badge = new Label(tipoMant);
        badge.setStyle("-fx-background-color: #FFC107; -fx-padding: 5 10; -fx-background-radius: 15;");

        header.getChildren().addAll(lblCoche, spacer, badge);

        Label lblDetalles = new Label("Fecha: " + t.getFechaHora().toLocalDate() + " | Coste: " + t.getCoste() + "€");
        Label lblNotas = new Label("Notas: " + (t.getNotas() != null ? t.getNotas() : "-"));

        card.getChildren().addAll(header, lblDetalles, lblNotas);
        return card;
    }

    @FXML
    private void onAddMantenimiento() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Mantenimiento");
        dialog.setHeaderText(null);

        VBox content = new VBox(10);

        ComboBox<Vehiculo> comboVehiculo = new ComboBox<>(FXCollections.observableArrayList(vehiculoDAO.getAll()));
        ComboBox<com.uber.model.Mantenimiento> comboTipo = new ComboBox<>(FXCollections.observableArrayList(mantenimientoDAO.getAll()));
        TextField txtCoste = new TextField(); txtCoste.setPromptText("Coste (€)");
        TextArea txtNotas = new TextArea(); txtNotas.setPromptText("Notas...");

        content.getChildren().addAll(new Label("Vehículo:"), comboVehiculo, new Label("Tipo:"), comboTipo, new Label("Coste:"), txtCoste, new Label("Notas:"), txtNotas);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    com.uber.model.Tiene registro = new com.uber.model.Tiene();
                    registro.setVehiculo(comboVehiculo.getValue());
                    registro.setMantenimiento(comboTipo.getValue());
                    registro.setFechaHora(java.time.LocalDateTime.now());
                    registro.setCoste(Double.parseDouble(txtCoste.getText()));
                    registro.setNotas(txtNotas.getText());

                    if (tieneDAO.insert(registro)) {
                        // Poner vehículo en MANTENIMIENTO
                        Vehiculo v = comboVehiculo.getValue();
                        v.setEstadoVehiculo(EstadoVehiculo.MANTENIMIENTO);
                        vehiculoDAO.update(v);

                        mostrarMensaje("Mantenimiento registrado");
                        cargarHistorialMantenimiento();
                        cargarDatosResumen();
                    }
                } catch (Exception e) {
                    mostrarError("Datos incorrectos");
                }
            }
        });
    }

    // ================================================================
    //  PESTAÑA 5: USUARIOS
    // ================================================================

    /**
     * Configura la tabla de usuarios.
     */
    private void configurarTablaUsuarios() {
        if (tablaUsuarios == null) return;

        colUserId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

        colUserNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre() + " " + cellData.getValue().getApellidos())
        );

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

                btnEdit.setOnAction(e -> onEditarUsuario(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> onBorrarUsuario(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
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

    /**
     * Carga los usuarios (sin mostrar administradores).
     */
    private void cargarTablaUsuarios() {
        if (tablaUsuarios != null) {
            List<Usuario> todos = usuarioDAO.getAll();

            // Filtrar para no mostrar admins
            List<Usuario> soloClientes = todos.stream()
                    .filter(u -> u.getRol() != Rol.ADMIN)
                    .collect(Collectors.toList());

            tablaUsuarios.setItems(FXCollections.observableArrayList(soloClientes));
        }
    }

    /**
     * Edita el estado de un usuario.
     */
    private void onEditarUsuario(Usuario u) {
        List<EstadoCuenta> estados = List.of(EstadoCuenta.values());
        ChoiceDialog<EstadoCuenta> dialog = new ChoiceDialog<>(u.getEstadoCuenta(), estados);
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Cambiar estado de " + u.getNombre());
        dialog.setContentText("Nuevo estado:");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());

        dialog.showAndWait().ifPresent(nuevoEstado -> {
            u.setEstadoCuenta(nuevoEstado);
            if (usuarioDAO.update(u)) {
                mostrarExito("Estado actualizado.");
                cargarTablaUsuarios();
            } else {
                mostrarError("Error al actualizar.");
            }
        });
    }

    /**
     * Borra un usuario.
     */
    private void onBorrarUsuario(Usuario u) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Usuario");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Vas a eliminar a: " + u.getNombre() + "\nEsta acción es irreversible.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

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

    // ================================================================
    //  UTILIDADES
    // ================================================================

    /**
     * Cierra la sesión y vuelve al login.
     */
    @FXML
    private void onCerrarSesion() {
        Sesion.getInstancia().logOut();
        try {
            Stage stage = (Stage) lblTotalVehiculos.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje informativo simple.
     */
    private void mostrarMensaje(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }

    /**
     * Muestra un mensaje de éxito con título.
     */
    private void mostrarExito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    /**
     * Muestra un mensaje de error.
     */
    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}