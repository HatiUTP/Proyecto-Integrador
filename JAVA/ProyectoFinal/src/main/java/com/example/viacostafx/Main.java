package com.example.viacostafx;

import com.example.viacostafx.Modelo.JPAUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import javafx.stage.Screen;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            JPAUtils.getEntityManagerFactory();

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/GUI/Login.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 1024, 720);

            stage.setTitle("Via Costa - Inicio de Sesión");
            stage.setScene(scene);
            stage.show();
            // Obtiene las dimensiones de la pantalla principal después de mostrar la ventana
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();
            System.out.println(screenWidth);
            System.out.println(screenHeight);

            stage.setX((screenWidth - stage.getWidth()) / 2);
            stage.setY((screenHeight - stage.getHeight()) / 2);

            // Permite que la ventana se redimensione dinámicamente
            stage.setMaximized(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        JPAUtils.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}