package com.example.viacostafx.Controller;

import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ViajeBusModel;
import com.example.viacostafx.Modelo.ViajeModel;
import com.example.viacostafx.dao.AgenciaDao;
import com.example.viacostafx.dao.ViajeDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;


public class InterfazPrincipalController implements Initializable {
    @FXML
    private ComboBox<String> destinoCombo;
    @FXML
    private DatePicker viajeDate;
    @FXML
    private ComboBox<String> origenCombo;
    @FXML
    private DatePicker retornoDate;
    @FXML
    private Button btnBuscar;
    @FXML
    private GridPane Tabla1;

    @FXML
    private TableView<ViajeModel> tablaViajes;

    @FXML
    private TableColumn<ViajeModel, String> origenColumn;
    @FXML
    private TableColumn<ViajeModel, String> destinoColumn;
    @FXML
    private TableColumn<ViajeModel, String> horaSalidaColumn;
    @FXML
    private TableColumn<ViajeModel, String> tipoBusColumn;
    @FXML
    private TableColumn<ViajeModel, String> disponibilidadColumn;
    @FXML
    private TableColumn<ViajeModel, String> precioColumn;

    private Pane panelSecundario;
    private List<Node> elementosOriginalesTabla1;

    /**
     * Inicializa el controlador, configurando los componentes de la interfaz y asignando eventos.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        elementosOriginalesTabla1 = new ArrayList<>(Tabla1.getChildren());
        btnBuscar.setOnAction(event -> buscarYCargarPanelSecundario());

        // Limitar la selección de fechas en viajeDate y retornoDate
        limitarFechaSeleccionable(viajeDate);
        limitarFechaSeleccionable(retornoDate);

        cargarDistritosEnComboBox();

        origenColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAgenciaOrigen().getUbigeo().getDistrito()));
        destinoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAgenciaDestino().getUbigeo().getDistrito()));
        horaSalidaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaHoraSalida().toLocalTime().toString()));
        tipoBusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerTipoBus(cellData.getValue())));
        disponibilidadColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerDisponibilidad(cellData.getValue())));
        precioColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("S/ %.2f", calcularPrecio(cellData.getValue()))));
    }

    /**
     * Limita la selección de fechas en el DatePicker para que solo se puedan elegir fechas actuales o futuras.
     */
    private void limitarFechaSeleccionable(DatePicker datePicker) {
        datePicker.setDayCellFactory((Callback<DatePicker, DateCell>) param -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Deshabilitar fechas anteriores a la actual
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Color de fondo para indicar deshabilitación
                }
            }
        });
        
    }

    /**
     * Carga los distritos disponibles en los ComboBox de origen y destino.
     */
    private void cargarDistritosEnComboBox() {
        List<String> distritos = AgenciaDao.obtenerDistritosConAgencias();

        ObservableList<String> distritosList = FXCollections.observableArrayList(distritos);

        origenCombo.setItems(distritosList);
        destinoCombo.setItems(distritosList);

        origenCombo.setPromptText("Seleccione origen");
        destinoCombo.setPromptText("Seleccione destino");

        tablaViajes.setRowFactory(tv -> {
            TableRow<ViajeModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ViajeModel viajeSeleccionado = row.getItem();
                    manejarDobleClicEnViaje(viajeSeleccionado);
                }
            });
            return row;
        });
    }

    /**
     * Maneja el evento de doble clic en un viaje para mostrar la interfaz correspondiente.
     */
    private void manejarDobleClicEnViaje(ViajeModel viaje) {
        BusModel bus = obtenerBusDelViaje(viaje); // Obtener el bus del viaje
        if (bus != null) {
            int capacidad = bus.getCapacidad();
            String descripcionServicios = bus.getCategoria().getDescripcion();

            if (capacidad == 36) {
                abrirInterfaz("/GUI/BusPiso1.fxml", descripcionServicios, bus);
            } else if (capacidad == 51) {
                abrirInterfaz("/GUI/BusPiso1y2.fxml", descripcionServicios, bus);
            } else {
                mostrarAlerta("No se encontró una interfaz para la capacidad de " + capacidad + " asientos.");
            }
        } else {
            mostrarAlerta("No se pudo obtener la información del bus para el viaje seleccionado.");
        }
    }

    /**
     * Abre una interfaz de selección de asientos para el bus seleccionado.
     */
    private void abrirInterfaz(String fxmlPath, String descripcionServicios, BusModel bus) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane root = loader.load();

            // Obtener el controlador de la nueva interfaz
            Object controller = loader.getController();

            if (controller instanceof BusPiso1Controller) {
                BusPiso1Controller busController = (BusPiso1Controller) controller;
                busController.mostrarDescripcionServicios(descripcionServicios);
                busController.setBusId(bus.getId());
            } else if (controller instanceof BusPiso1y2Controller) {
                BusPiso1y2Controller busController = (BusPiso1y2Controller) controller;
                busController.mostrarDescripcionServicios(descripcionServicios);
                busController.setBusId(bus.getId());
            } else {
                mostrarAlerta("Controlador desconocido.");
                return;
            }

            // Crear una nueva escena y mostrarla en una nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Selección de Asiento");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la interfaz.");
        }
    }

    /**
     * Busca viajes y carga el panel secundario si es necesario.
     */
    @FXML
    private void buscarYCargarPanelSecundario() {
        if (retornoDate.getValue() != null){
            mostrarPanelSecundario();
        } else if (retornoDate.getValue() == null){
            Tabla1.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == 0);
            cargarTablaPrincipall();

            buscarYCargarViajesEnTabla();
        }
    }

    /**
     * Muestra el panel secundario en la interfaz.
     */
    private void mostrarPanelSecundario() {
        if (panelSecundario == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazSecundaria.fxml"));
                panelSecundario = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("No se pudo cargar el Panel Secundario");
                return;
            }
        }

        // Añadimos el panel secundario al GridPane principal
        if (Tabla1 != null) {
            // Eliminamos cualquier elemento previo en la posición deseada
            Tabla1.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == 1);

            // Añadimos el panel secundario en la posición deseada
            Tabla1.add(panelSecundario, 0, 1);

        } else {
            System.out.println("Error: gridPane no está inicializado.");
        }
    }

    /**
     * Restaura la tabla principal a su estado original.
     */
    private void cargarTablaPrincipall() {
        Tabla1.getChildren().clear();
        Tabla1.getChildren().addAll(elementosOriginalesTabla1);
    }

    /**
     * Busca viajes disponibles según los criterios seleccionados y los carga en la tabla.
     */
    private void buscarYCargarViajesEnTabla() {
        String origen = origenCombo.getSelectionModel().getSelectedItem();
        String destino = destinoCombo.getSelectionModel().getSelectedItem();
        LocalDate fechaSeleccionada = viajeDate.getValue();

        if (origen == null || destino == null || fechaSeleccionada == null) {
            mostrarAlerta("Debe seleccionar origen, destino y fecha de viaje.");
            return;
        }

        // Obtener los viajes disponibles
        List<ViajeModel> viajesDisponibles = ViajeDao.obtenerViajesDisponibles(origen, destino, fechaSeleccionada);

        if (viajesDisponibles == null || viajesDisponibles.isEmpty()) {
            // Mostrar mensaje indicando que no hay viajes disponibles
            mostrarAlerta("No hay viajes disponibles para los criterios seleccionados.");
            return;
        }

        // Cargar los viajes en la tabla
        cargarViajesEnTabla(viajesDisponibles);
    }

    /**
     * Carga los viajes disponibles en la tabla de viajes.
     */
    private void cargarViajesEnTabla(List<ViajeModel> viajesDisponibles) {
        tablaViajes.setItems(FXCollections.observableArrayList(viajesDisponibles));
    }

    /**
     * Obtiene el tipo de bus asociado al viaje.
     */
    private String obtenerTipoBus(ViajeModel viaje) {
        BusModel bus = obtenerBusDelViaje(viaje);

        if (bus != null) {
            System.out.println("Bus ID: " + bus.getId());
            if (bus.getCategoria() != null) {
                System.out.println("Categoría: " + bus.getCategoria().getNombre());
                return bus.getCategoria().getNombre();
            } else {
                System.out.println("bus.getCategoria() es null");
            }
        } else {
            System.out.println("Bus es null");
        }
        return "Desconocido";
    }

    /**
     * Obtiene el bus asociado al viaje seleccionado.
     */
    private BusModel obtenerBusDelViaje(ViajeModel viaje) {
        if (viaje.getViajeBuses() != null && !viaje.getViajeBuses().isEmpty()) {
            ViajeBusModel viajeBus = viaje.getViajeBuses().iterator().next();
            if (viajeBus != null) {
                BusModel bus = viajeBus.getBus();
                if (bus != null) {
                    return bus;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene la disponibilidad de asientos para el viaje.
     */
    private String obtenerDisponibilidad(ViajeModel viaje) {
        BusModel bus = obtenerBusDelViaje(viaje);
        if (bus != null) {
            if (bus.getAsientos() != null) {
                long asientosDisponibles = bus.getAsientos().stream()
                        .filter(asiento -> "DESOCUPADO".equalsIgnoreCase(asiento.getEstado()))
                        .count();
                System.out.println("Asientos disponibles: " + asientosDisponibles);
                return asientosDisponibles > 0 ? "Disponible (" + asientosDisponibles + " asientos)" : "Bus Lleno";
            } else {
                System.out.println("Asientos es null");
            }
        } else {
            System.out.println("Bus es null");
        }
        return "Desconocido";
    }

    /**
     * Calcula el precio del viaje, considerando el costo extra del bus.
     */
    private double calcularPrecio(ViajeModel viaje) {
        double precioBase = 35.0;
        BusModel bus = obtenerBusDelViaje(viaje);

        if (bus != null && bus.getCategoria() != null && bus.getCategoria().getCostoExtra() != null) {
            precioBase += bus.getCategoria().getCostoExtra().doubleValue();
        }

        return precioBase;
    }


    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}