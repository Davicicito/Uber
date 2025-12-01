package com.uber.controllers;

import com.uber.dao.EstacionDAO;
import com.uber.dao.UsuarioDAO;
import com.uber.dao.VehiculoDAO;
import com.uber.enums.EstadoVehiculo;
import com.uber.enums.TipoVehiculo;
import com.uber.model.Estacion;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class AdminController {

    // --- VISTAS (PESTAÑAS) ---
    @FXML private VBox vistaResumen;
    @FXML private VBox vistaVehiculos;
    @FXML private VBox vistaMantenimiento;

    // --- BOTONES NAVEGACIÓN ---
    @FXML private Button btnNavResumen;
    @FXML private Button btnNavVehiculos;
    @FXML private Button btnNavMantenimiento;

    // --- PESTAÑA RESUMEN (KPIs y Alertas) ---
    @FXML private Label lblTotalVehiculos, lblVehiculosDisponibles, lblTotalUsuarios, lblTotalEstaciones;
    @FXML private Label badgeDisponibles, badgeEnUso, badgeMantenimiento;
    @FXML private VBox contenedorAlertas;

    // --- PESTAÑA VEHÍCULOS (Tabla) ---
    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colId;
    @FXML private TableColumn<Vehiculo, String> colTipo;
    @FXML private TableColumn<Vehiculo, String> colMarcaModelo;
    @FXML private TableColumn<Vehiculo, EstadoVehiculo> colEstado;
    @FXML private TableColumn<Vehiculo, Double> colBateria;
    @FXML private TableColumn<Vehiculo, Double> colKm;
    @FXML private TableColumn<Vehiculo, String> colEstacion;
    @FXML private TableColumn<Vehiculo, Void> colAcciones;

    // --- DAOS ---
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstacionDAO estacionDAO = new EstacionDAO();

    // ================================================================
    // 1. INICIALIZACIÓN
    // ================================================================
    @FXML
    public void initialize() {
        configurarTabla(); // Prepara las columnas personalizadas
        mostrarResumen();  // Arranca en la pantalla principal
    }

    // ================================================================
    // 2. NAVEGACIÓN
    // ================================================================
    @FXML
    void mostrarResumen() {
        ocultarTodo();
        vistaResumen.setVisible(true);
        actualizarBotonNav(btnNavResumen);
        cargarDatosResumen(); // Refresca los contadores
    }

    @FXML
    void mostrarVehiculos() {
        ocultarTodo();
        vistaVehiculos.setVisible(true);
        actualizarBotonNav(btnNavVehiculos);
        cargarTablaVehiculos(); // Refresca la tabla
    }

    @FXML
    void mostrarMantenimiento() {
        ocultarTodo();
        vistaMantenimiento.setVisible(true);
        actualizarBotonNav(btnNavMantenimiento);
    }

    private void ocultarTodo() {
        vistaResumen.setVisible(false);
        vistaVehiculos.setVisible(false);
        vistaMantenimiento.setVisible(false);
    }

    private void actualizarBotonNav(Button activo) {
        btnNavResumen.getStyleClass().remove("admin-nav-selected");
        btnNavVehiculos.getStyleClass().remove("admin-nav-selected");
        btnNavMantenimiento.getStyleClass().remove("admin-nav-selected");
        activo.getStyleClass().add("admin-nav-selected");
    }

    // ================================================================
    // 3. LÓGICA RESUMEN (DASHBOARD)
    // ================================================================
    private void cargarDatosResumen() {
        List<Vehiculo> vehiculos = vehiculoDAO.getAll();

        long disp = vehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.DISPONIBLE).count();
        long uso = vehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.EN_USO).count();
        long mant = vehiculos.stream().filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.MANTENIMIENTO).count();

        // KPIs
        lblTotalVehiculos.setText(String.valueOf(vehiculos.size()));
        lblVehiculosDisponibles.setText(String.valueOf(disp));
        lblTotalUsuarios.setText(String.valueOf(usuarioDAO.getAll().size()));
        lblTotalEstaciones.setText(String.valueOf(estacionDAO.getAll().size()));

        // Badges
        badgeDisponibles.setText(String.valueOf(disp));
        badgeEnUso.setText(String.valueOf(uso));
        badgeMantenimiento.setText(String.valueOf(mant));

        // Alertas
        contenedorAlertas.getChildren().clear();
        boolean hayAlertas = false;

        for (Vehiculo v : vehiculos) {
            if (v.getNivelBateria() < 20) {
                crearAlerta("⚠️ Batería baja", v.getMarca() + " (" + v.getNivelBateria() + "%)", "alert-warning-box");
                hayAlertas = true;
            }
            if (v.getEstadoVehiculo() == EstadoVehiculo.INACTIVO) {
                crearAlerta("⛔ Vehículo inactivo", v.getMarca() + " requiere revisión.", "alert-danger-box");
                hayAlertas = true;
            }
        }

        if (!hayAlertas) {
            Label ok = new Label("✅ Todo en orden. No hay alertas.");
            ok.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-padding: 10;");
            contenedorAlertas.getChildren().add(ok);
        }
    }

    private void crearAlerta(String titulo, String desc, String estilo) {
        VBox card = new VBox(2);
        card.getStyleClass().addAll("alert-item", estilo);
        Label t = new Label(titulo); t.getStyleClass().add("alert-title");
        Label d = new Label(desc); d.getStyleClass().add("alert-desc");
        card.getChildren().addAll(t, d);
        contenedorAlertas.getChildren().add(card);
    }

    // ================================================================
    // 4. LÓGICA TABLA VEHÍCULOS
    // ================================================================
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVehiculo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colMarcaModelo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMarca() + " " + cellData.getValue().getModelo())
        );

        // Kilometraje bonito
        colKm.setCellValueFactory(new PropertyValueFactory<>("kilometraje"));
        colKm.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double km, boolean empty) {
                super.updateItem(km, empty);
                if (empty || km == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f km", km).replace(",", "."));
                }
            }
        });

        colEstacion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstacion().getNombreEstacion())
        );

        // Estado con colores
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

        // Barra batería
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
                    if (bateria < 20) pb.setStyle("-fx-accent: #EF4444;");
                    else pb.setStyle("-fx-accent: #10B981;");

                    Label lbl = new Label(bateria.intValue() + "%");
                    lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    HBox box = new HBox(8, pb, lbl);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        // Botones de Acción
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");

            {
                btnEdit.getStyleClass().addAll("action-btn", "btn-edit");
                btnDelete.getStyleClass().addAll("action-btn", "btn-delete");
                btnEdit.setTooltip(new Tooltip("Editar"));
                btnDelete.setTooltip(new Tooltip("Eliminar"));

                btnEdit.setOnAction(e -> onEditarVehiculo(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> onBorrarVehiculo(getTableView().getItems().get(getIndex())));
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

    private void cargarTablaVehiculos() {
        tablaVehiculos.setItems(FXCollections.observableArrayList(vehiculoDAO.getAll()));
    }

    // ================================================================
    // 5. ACCIONES CRUD (AÑADIR / EDITAR / BORRAR)
    // ================================================================

    @FXML
    private void onAddVehiculo() {
        abrirFormularioVehiculo(null); // null = Modo Añadir
    }

    private void onEditarVehiculo(Vehiculo v) {
        abrirFormularioVehiculo(v); // objeto = Modo Editar
    }

    // Método UNIFICADO para crear y editar con diseño bonito
    private void abrirFormularioVehiculo(Vehiculo vehiculoEditar) {
        boolean esEdicion = (vehiculoEditar != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Vehículo" : "Añadir Vehículo");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Layout del formulario
        VBox content = new VBox(15);
        content.getStyleClass().add("dialog-content-box");
        content.setPrefWidth(400);

        Label lblTitulo = new Label(esEdicion ? "Editar Vehículo #" + vehiculoEditar.getIdVehiculo() : "Añadir Nuevo Vehículo");
        lblTitulo.getStyleClass().add("dialog-heading");

        // CAMPOS
        ComboBox<TipoVehiculo> comboTipo = new ComboBox<>(FXCollections.observableArrayList(TipoVehiculo.values()));
        comboTipo.getStyleClass().add("combo-grey");
        comboTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtMarca = new TextField(); txtMarca.getStyleClass().add("input-grey"); txtMarca.setPromptText("Ej: Tesla");
        TextField txtModelo = new TextField(); txtModelo.getStyleClass().add("input-grey"); txtModelo.setPromptText("Ej: Model 3");

        // Combo Estaciones (con nombres bonitos)
        ComboBox<Estacion> comboEstacion = new ComboBox<>(FXCollections.observableArrayList(estacionDAO.getAll()));
        comboEstacion.getStyleClass().add("combo-grey");
        comboEstacion.setMaxWidth(Double.MAX_VALUE);
        Callback<ListView<Estacion>, ListCell<Estacion>> factory = lv -> new ListCell<>() {
            @Override protected void updateItem(Estacion e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty ? "" : e.getNombreEstacion());
            }
        };
        comboEstacion.setCellFactory(factory);
        comboEstacion.setButtonCell(factory.call(null));

        // Campos Extra para Edición (Estado, Batería, KM)
        ComboBox<EstadoVehiculo> comboEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoVehiculo.values()));
        comboEstado.getStyleClass().add("combo-grey");
        comboEstado.setMaxWidth(Double.MAX_VALUE);

        TextField txtBateria = new TextField(); txtBateria.getStyleClass().add("input-grey"); txtBateria.setPromptText("Batería %");
        TextField txtKm = new TextField(); txtKm.getStyleClass().add("input-grey"); txtKm.setPromptText("Kilometraje");

        // Rellenar datos si estamos editando
        if (esEdicion) {
            comboTipo.setValue(vehiculoEditar.getTipo());
            txtMarca.setText(vehiculoEditar.getMarca());
            txtModelo.setText(vehiculoEditar.getModelo());
            // Seleccionar estación actual
            for(Estacion e : comboEstacion.getItems()) {
                if(e.getIdEstacion() == vehiculoEditar.getEstacion().getIdEstacion()) {
                    comboEstacion.setValue(e);
                    break;
                }
            }
            comboEstado.setValue(vehiculoEditar.getEstadoVehiculo());
            txtBateria.setText(String.valueOf(vehiculoEditar.getNivelBateria()));
            txtKm.setText(String.valueOf(vehiculoEditar.getKilometraje()));
        } else {
            // Valores por defecto para nuevo
            comboTipo.getSelectionModel().selectFirst();
            if (!comboEstacion.getItems().isEmpty()) comboEstacion.getSelectionModel().selectFirst();
            comboEstado.setValue(EstadoVehiculo.DISPONIBLE);
        }

        // Añadir campos al layout (Estado/Bat/Km solo si editas, o puedes ponerlos siempre si quieres)
        content.getChildren().addAll(
                lblTitulo,
                new VBox(5, new Label("Tipo"), comboTipo),
                new VBox(5, new Label("Marca"), txtMarca),
                new VBox(5, new Label("Modelo"), txtModelo),
                new VBox(5, new Label("Estación"), comboEstacion)
        );

        if (esEdicion) {
            content.getChildren().addAll(
                    new VBox(5, new Label("Estado"), comboEstado),
                    new HBox(10,
                            new VBox(5, new Label("Batería (%)"), txtBateria),
                            new VBox(5, new Label("Kilometraje"), txtKm)
                    )
            );
        }

        dialog.getDialogPane().setContent(content);

        // Botones Personalizados
        ButtonType btnGuardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnGuardarType);

        Button btnGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardarType);
        btnGuardar.getStyleClass().add("btn-save");

        Button btnCancelar = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCancelar.getStyleClass().add("btn-cancel-solid");

        // LÓGICA GUARDAR
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnGuardarType) {
                try {
                    // Validaciones básicas
                    if (txtMarca.getText().isBlank() || txtModelo.getText().isBlank()) {
                        mostrarError("Marca y Modelo son obligatorios.");
                        return;
                    }

                    Vehiculo v = esEdicion ? vehiculoEditar : new Vehiculo();
                    v.setTipo(comboTipo.getValue());
                    v.setMarca(txtMarca.getText());
                    v.setModelo(txtModelo.getText());
                    v.setEstacion(comboEstacion.getValue());

                    if (esEdicion) {
                        v.setEstadoVehiculo(comboEstado.getValue());
                        v.setNivelBateria(Double.parseDouble(txtBateria.getText()));
                        v.setKilometraje(Double.parseDouble(txtKm.getText()));

                        if (vehiculoDAO.update(v)) {
                            mostrarExito("Vehículo actualizado correctamente.");
                        }
                    } else {
                        // Valores iniciales para nuevo
                        v.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
                        v.setNivelBateria(100.0);
                        v.setKilometraje(0.0);

                        if (vehiculoDAO.insert(v)) {
                            mostrarExito("Vehículo creado correctamente.");
                        }
                    }
                    // Refrescar todo
                    cargarTablaVehiculos();
                    cargarDatosResumen();

                } catch (NumberFormatException e) {
                    mostrarError("Formato de número incorrecto en batería o km.");
                } catch (Exception e) {
                    mostrarError("Error al guardar: " + e.getMessage());
                }
            }
        });
    }

    private void onBorrarVehiculo(Vehiculo v) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Vehículo");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Vas a eliminar: " + v.getMarca() + " " + v.getModelo());

        // Estilo alerta
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/uber/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (vehiculoDAO.delete(v.getIdVehiculo())) {
                mostrarExito("Vehículo eliminado.");
                cargarTablaVehiculos();
                cargarDatosResumen();
            } else {
                mostrarError("No se pudo eliminar. Puede tener reservas activas.");
            }
        }
    }

    private void mostrarExito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito"); a.setHeaderText(null); a.setContentText(msg); a.show();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg); a.show();
    }

    @FXML
    private void onCerrarSesion() {
        Sesion.getInstancia().logOut();
        try {
            Stage stage = (Stage) lblTotalVehiculos.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}