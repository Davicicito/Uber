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
        imgLogo.setImage(new Image("/com/uber/img/logouber.jpg"));
    }

    @FXML
    private void onRegistrar() {

        if (txtNombre.getText().isBlank() ||
                txtApellidos.getText().isBlank() ||
                txtEmail.getText().isBlank() ||
                txtPassword.getText().isBlank()) {
            lblError.setText(" Rellena todos los campos");
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(txtNombre.getText());
        u.setApellidos(txtApellidos.getText());
        u.setEmail(txtEmail.getText());
        u.setContrasena(txtPassword.getText());
        u.setMetodoPago("SIN DEFINIR");
        u.setSaldo(0.0);
        u.setEstadoCuenta(EstadoCuenta.ACTIVO);
        u.setRol(Rol.CLIENTE); // Registro SIEMPRE crea cliente

        if (usuarioDAO.insert(u)) {
            abrirVista("/com/uber/fxml/Login.fxml");
        } else {
            lblError.setText(" Error al registrar usuario");
        }
    }

    @FXML
    private void onVolver() {
        abrirVista("/com/uber/fxml/Login.fxml");
    }

    private void abrirVista(String ruta) {
        try {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(ruta)));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

