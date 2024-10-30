package com.example.viacostafx.Controller;

import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.dao.EmpleadosDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private EmpleadosDao empleadosDao;
    public LoginController() {
        this.empleadosDao = new EmpleadosDao();
    }

    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnLogin;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label txtLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction(event -> iniciarSesion(txtUsername.getText(), txtPassword.getText()));
    }


    private void iniciarSesion(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            mostrarAlerta("Por favor, completa ambos campos de usuario y contraseña.");
            return;
        }

        EmpleadosModel empleado = empleadosDao.obtenerEmpleadoPorUsername(username);
        if (empleado != null) {
            if (empleado.getContrasenia() != null && empleado.getContrasenia().equals(password)) {
                cargarInterfazPrincipal();
            } else {
                System.out.println("Contraseña incorrecta");
                mostrarAlerta("Usuario o contraseña incorrecta");
            }
        } else {
            System.out.println("Usuario no encontrado");
            mostrarAlerta("Usuario no encontrado. Por favor, verifica tus credenciales.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error al comprobar credenciales");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Centrar la alerta en la ventana principal
        alert.initOwner((Stage) txtLabel.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);

        alert.setOnShown(event -> {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            Stage ownerStage = (Stage) alert.getOwner();
            if (ownerStage != null) {
                stage.setX(ownerStage.getX() + (ownerStage.getWidth() - stage.getWidth()) / 2);
                stage.setY(ownerStage.getY() + (ownerStage.getHeight() - stage.getHeight()) / 2);
            }
        });

        alert.showAndWait();
    }

    private void cargarInterfazPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazPrincipal.fxml"));
            Parent mainInterfaceRoot = loader.load();

            Stage stage = (Stage) txtLabel.getScene().getWindow();
            Scene scene = new Scene(mainInterfaceRoot);

            stage.setScene(scene);
            stage.setMaximized(true);

            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            txtLabel.setText("Error al cargar la interfaz principal");
        }
    }
}