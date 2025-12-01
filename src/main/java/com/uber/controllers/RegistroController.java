package com.uber.controllers;

import com.uber.dao.UsuarioDAO;
import com.uber.enums.EstadoCuenta;
import com.uber.enums.Rol;
import com.uber.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controlador para la pantalla de registro de nuevos usuarios.
 * Gestiona la recogida de datos del formulario, validaciones y la inserción en la base de datos.
 */
public class RegistroController {

    // --- Elementos de la interfaz ---
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;
    @FXML private Button btnRegistrar;

    // --- Objeto de acceso a datos ---
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al cargar la ventana.
     * Inicializa la imagen del logotipo.
     */
    @FXML
    public void initialize() {
        try {
            imgLogo.setImage(new Image(getClass().getResourceAsStream("/com/uber/img/logouber.jpg")));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo.");
        }
    }

    /**
     * Gestiona el evento del botón "Registrarse".
     * Valida los datos introducidos y crea un nuevo usuario en el sistema.
     */
    @FXML
    private void onRegistrar() {
        // 1. Limpiar mensajes de error previos
        lblError.setText("");

        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // 2. Validar que no haya campos vacíos
        if (nombre.isBlank() || apellidos.isBlank() || email.isBlank() || password.isBlank()) {
            lblError.setText("⚠️ Rellena todos los campos");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 3. Comprobar si el email ya existe en la base de datos
        if (usuarioDAO.emailExiste(email)) {
            lblError.setText("❌ El email ya está registrado");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 4. Crear objeto Usuario con los datos del formulario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellidos(apellidos);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(password);
        nuevoUsuario.setMetodoPago("SIN DEFINIR"); // Valor por defecto
        nuevoUsuario.setSaldo(0.0);
        nuevoUsuario.setRol(Rol.CLIENTE);          // Rol por defecto para nuevos registros
        nuevoUsuario.setEstadoCuenta(EstadoCuenta.ACTIVO);

        // 5. Insertar en la Base de Datos
        if (usuarioDAO.insert(nuevoUsuario)) {
            mostrarAlertaExito();
            volverAlLogin();
        } else {
            lblError.setText("❌ Error de conexión al guardar");
        }
    }

    /**
     * Gestiona el evento del enlace para volver al login.
     */
    @FXML
    private void onVolver() {
        volverAlLogin();
    }

    /**
     * Método auxiliar para cargar la vista de inicio de sesión.
     */
    private void volverAlLogin() {
        try {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra una ventana emergente informativa indicando que el registro fue exitoso.
     */
    private void mostrarAlertaExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Completado");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
        alert.showAndWait();
    }
}
