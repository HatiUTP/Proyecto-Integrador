package com.example.viacostafx.Controller;
import com.example.viacostafx.Modelo.AsientoModel;
import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.dao.AsientoDao;
import com.example.viacostafx.dao.BusDao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

public class BusPiso1Controller implements Initializable {
    @FXML
    private GridPane gridAsientos;

    @FXML
    private TextArea txtServicios;

    private Map<Integer, Button> botonesAsientos;
    private AsientoDao asientoDAO;
    private BusDao busDAO;
    private int busId;

    /**
     * Inicializa el controlador y agrega una hoja de estilo a la escena cuando se carga.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setBusId(1);
        gridAsientos.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        });
    }


    public BusPiso1Controller() {
        asientoDAO = new AsientoDao();
        busDAO = new BusDao();
        botonesAsientos = new HashMap<>();
    }

    /**
     * Muestra la descripción de los servicios del bus en el área de texto.
     */
    public void mostrarDescripcionServicios(String descripcionServicios) {
        txtServicios.setText(descripcionServicios);
    }

    /**
     * Establece el ID del bus actual y carga los asientos.
     */
    public void setBusId(int busId) {
        this.busId = busId;
        inicializarAsientos();
    }

    /**
     * Inicializa la visualización de los asientos en el grid, agregando los botones correspondientes
     * según el estado de los asientos y el bus actual.
     */
    private void inicializarAsientos() {
        gridAsientos.getChildren().clear();

        Label espacioVacio = new Label("-");
        gridAsientos.add(espacioVacio, 0, 0);

        // Obtener los asientos del bus desde la base de datos
        List<AsientoModel> asientos = asientoDAO.obtenerAsientosPorBus(busId);

        // Colocar los botones de los asientos
        int columna = 1;
        int fila = 3;
        for (AsientoModel asiento : asientos) {
            Button btn = new Button(asiento.getNumero());
            btn.setMinSize(50, 50);
            btn.setMaxSize(50, 50);
            botonesAsientos.put(asiento.getId(), btn); // Guardar el botón asociado al ID del asiento

            // Asignar clase de estilo según el estado inicial
            if (asiento.getEstado().equals("DESOCUPADO")) {
                btn.getStyleClass().add("asiento-disponible");
            } else {
                btn.getStyleClass().add("asiento-ocupado");
            }

            // Agregar evento de clic
            btn.setOnAction(e -> handleAsientoClick(asiento));

            gridAsientos.add(btn, columna, fila);

            fila--;
            if (fila < 0) {
                fila = 3;
                columna++;
            }
        }

        // Agregar el asiento del conductor
        Button btnConductor = new Button("Conductor");
        btnConductor.setMinSize(50, 50);
        btnConductor.setMaxSize(50, 50);
        btnConductor.getStyleClass().add("asiento-conductor");
        gridAsientos.add(btnConductor, 0, 3);

        // Cargar el bus y verificar si se carga correctamente
        BusModel bus = busDAO.obtenerBusPorId(busId);
        if (bus != null) {
            System.out.println("Bus cargado: " + bus.getId());
        } else {
            System.out.println("No se encontró el bus con ID: " + busId);
        }
    }

    /**
     * Maneja el clic en un asiento, cambiando su estado entre "OCUPADO" y "DESOCUPADO".
     */
    private void handleAsientoClick(AsientoModel asiento) {
        boolean nuevoEstado = !asiento.getEstado().equals("OCUPADO");
        asiento.setEstado(nuevoEstado ? "OCUPADO" : "DESOCUPADO");

        if (asientoDAO.actualizarAsiento(asiento)) {
            actualizarEstiloBoton(asiento);
        }
    }

    /**
     * Actualiza el estilo del botón de un asiento después de cambiar su estado.
     */
    private void actualizarEstiloBoton(AsientoModel asiento) {
        Button btn = botonesAsientos.get(asiento.getId());
        btn.getStyleClass().removeAll("asiento-ocupado", "asiento-disponible");
        if (asiento.getEstado().equals("DESOCUPADO")) {
            btn.getStyleClass().add("asiento-disponible");
        } else {
            btn.getStyleClass().add("asiento-ocupado");
        }
    }
}