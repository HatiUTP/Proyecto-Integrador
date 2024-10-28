package com.example.viacostafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class BusPiso1y2Controller implements Initializable {
    @FXML
    private TextArea txtServicios;

    private int busId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void mostrarDescripcionServicios(String descripcionServicios) {
        txtServicios.setText(descripcionServicios);
    }

    public void setBusId(int busId) {
        this.busId = busId;

    }
}
