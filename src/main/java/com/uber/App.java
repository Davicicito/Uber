package com.uber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación UberCar.
 * Se encarga de iniciar la interfaz gráfica JavaFX y cargar la ventana de Login.
 */
public class App extends Application {

    /**
     * Método de inicio de la aplicación JavaFX.
     * Carga el FXML del login y muestra la ventana principal.
     * @param stage La ventana principal de la aplicación.
     */
    @Override
    public void start(Stage stage) {
        try {
            // Cargar la vista inicial (Login)
            Parent root = FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml"));
            Scene scene = new Scene(root);

            // Configurar y mostrar la ventana
            stage.setTitle("UberCar - Gestión de Flota");
            stage.setScene(scene);
            stage.setMaximized(true);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error crítico: No se pudo cargar la pantalla de inicio.");
        }
    }

    /**
     * Método main, punto de entrada de la aplicación Java.
     * Lanza la aplicación JavaFX.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }
}