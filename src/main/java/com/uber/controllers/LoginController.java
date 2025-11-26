package com.uber.controllers;

import com.uber.dao.UsuarioDAO;
import com.uber.model.Usuario;
import com.uber.enums.Rol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegistro;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        imgLogo.setImage(new Image("/com/uber/img/logouber.jpg"));
    }


    @FXML
    void onLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        Usuario u = usuarioDAO.login(email, pass);

        if (u == null) {
            lblError.setText("❌ Credenciales incorrectas");
            return;
        }

        switch (u.getRol()) {
            case ADMIN -> abrirVista("/com/uber/fxml/AdminView.fxml");
            case CLIENTE -> abrirVista("/com/uber/fxml/ClienteView.fxml");
        }
    }

    @FXML
    void onRegistro(ActionEvent event) {
        abrirVista("/com/uber/fxml/Registro.fxml");
    }

    private void abrirVista(String ruta) {
        try {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(ruta)));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}



