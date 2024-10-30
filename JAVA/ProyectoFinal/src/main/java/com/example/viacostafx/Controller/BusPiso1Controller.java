package com.example.viacostafx.Controller;
import com.example.viacostafx.Modelo.AsientoModel;
import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ViajeBusModel;
import com.example.viacostafx.Modelo.ViajeModel;
import com.example.viacostafx.dao.AsientoDao;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.ViajeBusDao;
import com.example.viacostafx.dao.ViajeDao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

public class BusPiso1Controller implements Initializable {
    @FXML
    private GridPane gridAsientos;
    @FXML
    private TextArea txtServicios;
    @FXML
    private TextField txtAsiento;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Button btnSiguiente;


    private double precio; // Variable para almacenar el precio
    private Map<Integer, Button> botonesAsientos;
    private AsientoDao asientoDAO;
    private BusDao busDAO;
    private int busId;
    private ViajeBusModel viajeBusDAO;
    private int viajeId;
    private String origen;
    private String destino;
    private String fecha;
    private InterfazPrincipalController mainController;
    private int asientoId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gridAsientos.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        });

        btnSiguiente.setOnAction(event -> abrirRegistroCliente());
    }

    public BusPiso1Controller() {
        asientoDAO = new AsientoDao();
        busDAO = new BusDao();
        botonesAsientos = new HashMap<>();
    }

    private void abrirRegistroCliente() {
        if (txtAsiento.getText().isEmpty()) {
            mostrarAlerta("Debe seleccionar un asiento antes de continuar.");
            return;
        }

        String asiento = txtAsiento.getText();
        String precioStr = txtPrecio.getText();
        double precio = 0.0;
        try {
            precio = Double.parseDouble(precioStr.replace("S/ ", ""));
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio inválido.");
            return;
        }

        String servicio = txtServicios.getText();
        String categoria = obtenerCategoriaDelBus();
        String fechaConHora = obtenerFechaConHora();

        // Obtener asientoId y viajeId
        int asientoIdLocal = asientoId;
        int viajeIdLocal = viajeId;

        System.out.println("Viaje ID: " + viajeIdLocal);
        System.out.println("Asiento ID: " + asientoIdLocal);

        if (mainController != null) {
            mainController.cargarRegistroCliente(origen, destino, fechaConHora, asiento, precio, servicio, categoria, viajeIdLocal, asientoIdLocal);
        } else {
            mostrarAlerta("Error: controlador principal no está definido.");
            return;
        }

        Stage stage = (Stage) btnSiguiente.getScene().getWindow();
        stage.close();
    }




    public void mostrarDescripcionServicios(String descripcionServicios) {
        txtServicios.setText(descripcionServicios);
    }

    public void setBusId(int busId) {
        this.busId = busId;
        inicializarAsientos();
    }

    private void inicializarAsientos() {

            gridAsientos.getChildren().clear();

            List<AsientoModel> asientos = asientoDAO.obtenerAsientosPorBus(busId);
            System.out.println("Cantidad de asientos: " + asientos.size());

            int columna = 1;
            int fila = 3;
            for (AsientoModel asiento : asientos) {
                Button btn = new Button(asiento.getNumero());
                btn.setMinSize(50, 50);
                btn.setMaxSize(50, 50);
                botonesAsientos.put(asiento.getId(), btn);

                if (asiento.getEstado().equals("DESOCUPADO")) {
                    btn.getStyleClass().add("asiento-disponible");
                } else {
                    btn.getStyleClass().add("asiento-ocupado");
                }

                btn.setOnAction(e -> handleAsientoClick(asiento));
                gridAsientos.add(btn, columna, fila);

                fila--;
                if (fila < 0) {
                    fila = 3;
                    columna++;
                }
            }

            Button btnConductor = new Button("Conductor");
            btnConductor.setMinSize(50, 50);
            btnConductor.getStyleClass().add("asiento-conductor");
            gridAsientos.add(btnConductor, 0, 3);
        }

    private void handleAsientoClick(AsientoModel asiento) {
        boolean nuevoEstado = !asiento.getEstado().equals("OCUPADO");
        asiento.setEstado(nuevoEstado ? "OCUPADO" : "DESOCUPADO");

        if (asientoDAO.actualizarAsiento(asiento)) {
            actualizarEstiloBoton(asiento);

            if (nuevoEstado) {
                txtAsiento.setText(asiento.getNumero());
                txtPrecio.setText(String.format("S/ %.2f", precio));
                this.asientoId = asiento.getId();
            } else {
                if (txtAsiento.getText().equals(asiento.getNumero())) {
                    txtAsiento.clear();
                    txtPrecio.clear();
                    this.asientoId = 0;
                }
            }
        } else {
            mostrarAlerta("No se pudo actualizar el estado del asiento.");
        }
    }



    private void actualizarEstiloBoton(AsientoModel asiento) {
        Button btn = botonesAsientos.get(asiento.getId());
        btn.getStyleClass().removeAll("asiento-ocupado", "asiento-disponible");
        if (asiento.getEstado().equals("DESOCUPADO")) {
            btn.getStyleClass().add("asiento-disponible");
        } else {
            btn.getStyleClass().add("asiento-ocupado");
        }
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setDatosViaje(int viajeId, String origen, String destino, String fecha) {
        this.viajeId = viajeId;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
    }

    public void setMainController(InterfazPrincipalController mainController) {
        this.mainController = mainController;
    }


    private String obtenerCategoriaDelBus() {
        ViajeBusDao viajeBusDao = new ViajeBusDao();
        ViajeBusModel viajeBus = viajeBusDao.obtenerPorViajeId(viajeId).stream().findFirst().orElse(null);
        if (viajeBus != null) {
            BusDao busDao = new BusDao();
            BusModel bus = busDao.obtenerBusPorId(viajeBus.getBus().getId());
            if (bus != null && bus.getCategoria() != null) {
                return bus.getCategoria().getNombre();
            }
        }
        return "Desconocida";
    }

    private String obtenerFechaConHora() {
        ViajeDao viajeDao = new ViajeDao();
        ViajeModel viaje = viajeDao.obtenerViajePorId(viajeId);
        if (viaje != null && viaje.getFechaHoraSalida() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return viaje.getFechaHoraSalida().format(formatter);
        }
        return "Fecha y Hora Desconocidas";
    }

    public void setViajeId(int viajeId) {
        this.viajeId = viajeId;
    }

}