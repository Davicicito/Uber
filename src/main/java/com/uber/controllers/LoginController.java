package com.uber.controllers;

import com.uber.dao.UsuarioDAO;
import com.uber.model.Usuario;
import com.uber.enums.Rol;
import com.uber.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador para gestionar el inicio de sesión de la aplicación.
 * Se encarga de validar las credenciales del usuario y redirigirlo a su vista correspondiente.
 */
public class LoginController {

    // Elementos de la interfaz gráfica
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private ImageView imgLogo;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegistro;

    // Objeto para acceder a la base de datos de usuarios
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que se ejecuta al cargar la ventana.
     * Inicializa la imagen del logo de la aplicación.
     */
    @FXML
    public void initialize() {
        try {
            // Intentamos cargar la imagen del logo
            imgLogo.setImage(new Image(getClass().getResourceAsStream("/com/uber/img/logouber.jpg")));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del logo: " + e.getMessage());
        }
    }

    /**
     * Método que se ejecuta al pulsar el botón de "Iniciar Sesión".
     * Comprueba el usuario y la contraseña contra la base de datos.
     * @param event El evento del botón.
     */
    @FXML
    void onLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        // Validamos que los campos no estén vacíos
        if (email.isEmpty() || pass.isEmpty()) {
            lblError.setText("Introduce email y contraseña");
            return;
        }

        // Consultamos a la base de datos
        Usuario u = usuarioDAO.login(email, pass);

        // Si el usuario es null, es que no existe o la contraseña está mal
        if (u == null) {
            lblError.setText("❌ Credenciales incorrectas");
            return;
        }

        // Si llegamos aquí, el login es correcto. Guardamos la sesión.
        Sesion.getInstancia().logIn(u);

        // Redirigimos a la pantalla correspondiente según si es Admin o Cliente
        switch (u.getRol()) {
            case ADMIN:
                abrirVista("/com/uber/fxml/AdminView.fxml");
                break;
            case CLIENTE:
                abrirVista("/com/uber/fxml/ClienteView.fxml");
                break;
            default:
                lblError.setText("Rol de usuario desconocido");
        }
    }

    /**
     * Método que se ejecuta al pulsar el enlace de "Regístrate".
     * Cambia la pantalla a la vista de registro.
     * @param event El evento del enlace.
     */
    @FXML
    void onRegistro(ActionEvent event) {
        abrirVista("/com/uber/fxml/Registro.fxml");
    }

    /**
     * Método auxiliar para cargar y cambiar de escena (ventana).
     * @param ruta La ruta del archivo FXML que queremos cargar.
     */
    private void abrirVista(String ruta) {
        try {
            // Obtenemos la ventana (Stage) actual a través de uno de los componentes
            Stage stage = (Stage) txtEmail.getScene().getWindow();

            // Cargamos la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());

            // Cambiamos la escena y la mostramos
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error al cargar la vista: " + ruta);
        }
    }
}



