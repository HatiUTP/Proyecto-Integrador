package com.example.viacostafx.Controller;

import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ChoferModel;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.ChoferDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;

public class ChoferController {

    private ChoferDao choferDao;
    private ObservableList<ChoferModel> listaChoferes;
    private BusDao busDao;

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtLicencia;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<ChoferModel> tablaChoferes;

    @FXML
    private TableColumn<ChoferModel, Integer> IdColumn;

    @FXML
    private TableColumn<ChoferModel, String> dniColumn;

    @FXML
    private TableColumn<ChoferModel, String> emailColumn;

    @FXML
    private TableColumn<ChoferModel, Integer> idBusColumn;

    @FXML
    private TableColumn<ChoferModel, String> licenciaColumn;

    @FXML
    private TableColumn<ChoferModel, String> nombreColumn;

    @FXML
    private TableColumn<ChoferModel, String> telefonoColumn;

    @FXML
    private ComboBox<BusModel> cmbBus;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnLimpiar;
    @FXML
    private GridPane Tabla1;

    public ChoferController() {
        this.choferDao = new ChoferDao();
        this.busDao = new BusDao();
    }

    @FXML
    public void initialize() {
        // Inicializar la lista observable
        listaChoferes = FXCollections.observableArrayList();

        // Configurar columnas
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id")); // Asegúrate de que el nombre del campo sea correcto
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        licenciaColumn.setCellValueFactory(new PropertyValueFactory<>("numeroLicencia"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        idBusColumn.setCellValueFactory(new PropertyValueFactory<>("bus")); // Cambia si necesitas obtener una propiedad específica


        btnAgregar.setOnAction(event -> agregarChofer());
        btnEditar.setOnAction(event -> editarChofer());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnEliminar.setOnAction(event -> desactivarChoferSeleccionado());
        // Cargar datos de choferes
        cargarChoferes();
        List<BusModel> buses = busDao.obtenerTodosLosBuses();
        cmbBus.setItems(FXCollections.observableArrayList(buses));
        tablaChoferes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarDatosChoferEnCampos(newValue);
            }
        });
    }

    private void limpiarCampos(){
        txtDni.setText("");
        txtEmail.setText("");
        txtLicencia.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        cmbBus.getSelectionModel().clearSelection();
        tablaChoferes.getSelectionModel().clearSelection();
    }

    private void cargarDatosChoferEnCampos(ChoferModel chofer) {
        txtDni.setText(chofer.getDni());
        txtEmail.setText(chofer.getEmail());
        txtLicencia.setText(chofer.getNumeroLicencia());
        txtNombre.setText(chofer.getNombre());
        txtTelefono.setText(chofer.getTelefono());

        // Seleccionar el bus correspondiente en el ComboBox, si está presente
        if (chofer.getBus() != null) {
            cmbBus.getSelectionModel().select(chofer.getBus());
        } else {
            cmbBus.getSelectionModel().clearSelection();
        }
    }

    // Método para cargar la lista de choferes
    private void cargarChoferes() {
        List<ChoferModel> choferes = choferDao.obtenerTodosLosChoferes();
        listaChoferes.clear(); // Limpia la lista antes de agregar nuevos elementos
        listaChoferes.addAll(choferes); // Agrega los choferes a la lista observable
        tablaChoferes.setItems(listaChoferes); // Establece la lista en la tabla
    }


    @FXML
    private void agregarChofer() {
        ChoferModel nuevoChofer = new ChoferModel();
        nuevoChofer.setDni(txtDni.getText());
        nuevoChofer.setEmail(txtEmail.getText());
        nuevoChofer.setNumeroLicencia(txtLicencia.getText());
        nuevoChofer.setNombre(txtNombre.getText());
        nuevoChofer.setTelefono(txtTelefono.getText());
        nuevoChofer.setIsActive(true);

        // Obtener el bus seleccionado en el ComboBox
        BusModel busSeleccionado = cmbBus.getSelectionModel().getSelectedItem();
        if (busSeleccionado != null) {
            nuevoChofer.setBus(busSeleccionado);
        } else {
            System.out.println("Seleccione un bus antes de agregar el chofer.");
        }

        choferDao.agregarChofer(nuevoChofer);
        cargarChoferes(); // Recargar la lista de choferes
        limpiarCampos();
    }

    private void editarChofer() {
        // Obtén el chofer seleccionado en la tabla
        ChoferModel choferSeleccionado = tablaChoferes.getSelectionModel().getSelectedItem();

        if (choferSeleccionado != null) {
            // Actualiza los datos del chofer seleccionado
            choferSeleccionado.setDni(txtDni.getText());
            choferSeleccionado.setEmail(txtEmail.getText());
            choferSeleccionado.setNumeroLicencia(txtLicencia.getText());
            choferSeleccionado.setNombre(txtNombre.getText());
            choferSeleccionado.setTelefono(txtTelefono.getText());
            choferSeleccionado.setIsActive(true);

            // Asigna el bus seleccionado
            BusModel busSeleccionado = cmbBus.getSelectionModel().getSelectedItem();
            if (busSeleccionado != null) {
                choferSeleccionado.setBus(busSeleccionado);
            } else {
                System.out.println("Seleccione un bus antes de agregar el chofer.");
            }

            // Llama al método del DAO para actualizar el chofer
            choferDao.editarChofer(choferSeleccionado);

            // Recarga la tabla para mostrar los cambios
            cargarChoferes();
        } else {
            System.out.println("Seleccione un chofer en la tabla para editar.");
        }
    }

    public void desactivarChoferSeleccionado() {
        ChoferModel choferSeleccionado = tablaChoferes.getSelectionModel().getSelectedItem();

        if (choferSeleccionado != null) {
            choferDao.desactivarChofer(choferSeleccionado.getId());  // Llama al método en el DAO
            cargarChoferes();  // Actualiza la tabla para reflejar el cambio
        } else {
            System.out.println("Seleccione un chofer en la tabla para desactivar.");
        }
    }

    private void eliminarChofer(){
        ChoferModel choferSeleccionado = tablaChoferes.getSelectionModel().getSelectedItem();
        choferDao.eliminarChofer(choferSeleccionado.getId());
        cargarChoferes();
        limpiarCampos();
    }

    @FXML
    public void buscarChoferes(Event event) {
        String nombreBuscado = txtBuscar.getText();
        List<ChoferModel> choferesFiltrados = choferDao.buscarChoferPorNombre(nombreBuscado);

        listaChoferes.clear();
        listaChoferes.addAll(choferesFiltrados);
        tablaChoferes.setItems(listaChoferes);
    }
}
