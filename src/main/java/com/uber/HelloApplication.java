package com.uber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/uber/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Uber - Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR cargando Login.fxml");
        }
    }


    public static void main(String[] args) {
        launch();
    }

}
