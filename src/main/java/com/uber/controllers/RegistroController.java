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

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;
    @FXML private Button btnRegistrar;

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
        lblError.setText("");

        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();


        // 1. Campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblError.setText("⚠️ Rellena todos los campos");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 2. Validación de Email
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            lblError.setText("❌ El email no es válido");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 3. Validación de Contraseña (Mínimo 6 caracteres, letras y números)
        if (!password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Contraseña Insegura");
            alert.setHeaderText(null);
            alert.setContentText("La contraseña debe tener al menos 6 caracteres, incluyendo letras y números.");
            alert.showAndWait();
            return;
        }

        // 4. Comprobar si el email ya existe en la BD
        if (usuarioDAO.emailExiste(email)) {
            lblError.setText("❌ Ese email ya está registrado");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellidos(apellidos);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(password);
        nuevoUsuario.setMetodoPago("SIN DEFINIR");
        nuevoUsuario.setSaldo(0.0);
        nuevoUsuario.setRol(Rol.CLIENTE);
        nuevoUsuario.setEstadoCuenta(EstadoCuenta.ACTIVO);

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
