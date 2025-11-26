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

public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;
    @FXML private Button btnRegistrar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        try {
            imgLogo.setImage(new Image(getClass().getResourceAsStream("/com/uber/img/logouber.jpg")));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo.");
        }
    }

    @FXML
    private void onRegistrar() {
        // 1. Limpiar errores previos
        lblError.setText("");

        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // 2. Validar campos vacíos
        if (nombre.isBlank() || apellidos.isBlank() || email.isBlank() || password.isBlank()) {
            lblError.setText("⚠️ Rellena todos los campos");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 3. Comprobar si el email ya existe
        if (usuarioDAO.emailExiste(email)) {
            lblError.setText("❌ El email ya está registrado");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        // 4. Crear objeto Usuario
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setEmail(email);
        u.setContrasena(password);
        u.setMetodoPago("SIN DEFINIR"); // Valor por defecto
        u.setSaldo(0.0);
        u.setRol(Rol.CLIENTE);          // Rol por defecto
        u.setEstadoCuenta(EstadoCuenta.ACTIVO);

        // 5. Guardar en Base de Datos
        if (usuarioDAO.insert(u)) {
            mostrarAlertaExito();
            irALogin();
        } else {
            lblError.setText("❌ Error de conexión al guardar");
        }
    }

    @FXML
    private void onVolver() {
        irALogin();
    }

    private void irALogin() {
        try {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Completado");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
        alert.showAndWait();
    }
}

