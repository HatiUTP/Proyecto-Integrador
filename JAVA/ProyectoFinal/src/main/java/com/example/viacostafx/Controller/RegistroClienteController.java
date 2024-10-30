package com.example.viacostafx.Controller;

import com.example.viacostafx.Modelo.PasajeroModel;
import com.example.viacostafx.dao.PasajeroDao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroClienteController implements Initializable {
    @FXML
    private TextField txtDNI;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtOrigen;
    @FXML
    private TextField txtDestino;
    @FXML
    private TextField txtFecha;
    @FXML
    private TextField txtAsiento;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextArea txtServicio;
    @FXML
    private TextField txtCategoria;


    private PasajeroDao pasajeroDao;
    private int viajeId;
    private int asientoId;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pasajeroDao = new PasajeroDao();

        btnBuscar.setOnAction(event -> buscarPasajeroPorDni());
    }

    private void buscarPasajeroPorDni() {
        String dni = txtDNI.getText().trim();

        if (dni.isEmpty()) {
            mostrarAlerta("Por favor, ingrese un DNI para buscar.");
            return;
        }

        List<PasajeroModel> pasajeros = pasajeroDao.buscarPasajerosPorDni(dni);

        if (pasajeros != null && !pasajeros.isEmpty()) {
            PasajeroModel pasajero = pasajeros.get(0);

            String nombreCompleto = pasajero.getNombre();
            String nombre = "";
            String apellido = "";

            if (nombreCompleto != null && !nombreCompleto.isEmpty()) {
                String[] partes = nombreCompleto.split(" ", 2); // Dividir en dos partes en el primer espacio
                nombre = partes[0];
                if (partes.length > 1) {
                    apellido = partes[1];
                }
            }

            txtNombre.setText(nombre);
            txtApellido.setText(apellido);
            txtCorreo.setText(pasajero.getEmail());
            txtTelefono.setText(pasajero.getTelefono());

            mostrarAlerta("Pasajero encontrado y datos autocompletados.");
        } else {
            mostrarAlerta("El cliente aún no se ha registrado en la base de datos.");
        }
    }


    public void setDatosCliente(String origen, String destino, String fechaConHora, String asiento, double precio, String servicio, String categoria, int viajeId, int asientoId) {
        txtOrigen.setText(origen);
        txtDestino.setText(destino);
        txtFecha.setText(fechaConHora);
        txtAsiento.setText(asiento);
        txtPrecio.setText(String.format("S/ %.2f", precio));
        txtServicio.setText(servicio);
        txtCategoria.setText(categoria);
        this.viajeId = viajeId;
        this.asientoId = asientoId;
    }

    public int getViajeId() {
        return viajeId;
    }

    public int getAsientoId() {
        return asientoId;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
