package com.example.viacostafx.Controller;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.dao.EmpleadosDao;
import jakarta.persistence.PersistenceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.hibernate.exception.ConstraintViolationException;

import java.security.SecureRandom;
import java.util.List;

public class RegistroEmpleadoController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDNI;
    @FXML private TextField txtTelefono;
    @FXML private TableView<EmpleadosModel> tablaEmpleados;
    @FXML private TableColumn<EmpleadosModel, String> colNombre;
    @FXML private TableColumn<EmpleadosModel, String> colApellido;
    @FXML private TableColumn<EmpleadosModel, Integer> colDNI;
    @FXML private TableColumn<EmpleadosModel, Integer> colTelefono;
    @FXML private TableColumn<EmpleadosModel, String> colUsuario;
    @FXML private TableColumn<EmpleadosModel, String> colContrasenia;
    @FXML private TableColumn<EmpleadosModel, Void> colAcciones;
    @FXML
    private Button btnAgregar;

    private boolean modoEdicion = false;
    private EmpleadosModel empleadoEnEdicion = null;


    private EmpleadosDao empleadosDao;
    private ObservableList<EmpleadosModel> listaEmpleados;

    @FXML
    public void initialize() {
        empleadosDao = new EmpleadosDao();
        listaEmpleados = FXCollections.observableArrayList();

        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colContrasenia.setCellValueFactory(new PropertyValueFactory<>("contrasenia"));
        btnAgregar.setOnAction(e -> {
            if (modoEdicion) {
                guardarEdicion();
            } else {
                handleAgregar();
            }
        });

        // Agregar columna de acciones
        agregarBotonesAccion();

        // Cargar datos
        cargarEmpleados();
    }

    private void agregarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox botones = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    EmpleadosModel empleado = getTableView().getItems().get(getIndex());
                    editarEmpleado(empleado);
                });

                btnEliminar.setOnAction(event -> {
                    EmpleadosModel empleado = getTableView().getItems().get(getIndex());
                    eliminarEmpleado(empleado);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });
    }

    @FXML
    private void handleAgregar() {
        try {
            // Validar campos vacíos
            if (txtNombre.getText().trim().isEmpty() ||
                    txtApellido.getText().trim().isEmpty() ||
                    txtDNI.getText().trim().isEmpty() ||
                    txtTelefono.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                return;
            }

            // Validar DNI y teléfono antes de la conversión
            String dniText = txtDNI.getText().trim();
            String telefonoText = txtTelefono.getText().trim();

            if (dniText.length() != 8) {
                mostrarAlerta("Error", "El DNI debe tener 8 dígitos", Alert.AlertType.ERROR);
                return;
            }

            if (telefonoText.length() != 9) {
                mostrarAlerta("Error", "El teléfono debe tener 9 dígitos", Alert.AlertType.ERROR);
                return;
            }

            // Convertir a números
            int dni = Integer.parseInt(dniText);
            int telefono = Integer.parseInt(telefonoText);

            // Verificar si el DNI ya existe
            if (empleadosDao.existeDNI(dni)) {
                mostrarAlerta("Error", "El DNI ya está registrado en el sistema", Alert.AlertType.ERROR);
                return;
            }

            EmpleadosModel empleado = new EmpleadosModel();
            empleado.setNombre(txtNombre.getText().trim());
            empleado.setApellido(txtApellido.getText().trim());
            empleado.setDNI(dni);
            empleado.setTelefono(telefono);

            // Generar usuario y contraseña
            String usuario = generarUsuario(empleado.getNombre(), empleado.getApellido());
            String contrasenia = generarContrasenia();

            empleado.setUsuario(usuario);
            empleado.setContrasenia(contrasenia);

            empleadosDao.crearEmpleado(empleado);
            mostrarAlerta("Éxito",
                    "Empleado agregado correctamente\n" +
                            "Usuario: " + usuario + "\n" +
                            "Contraseña: " + contrasenia,
                    Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarEmpleados();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "DNI y teléfono deben ser números válidos", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar empleado: " + e.getMessage(), Alert.AlertType.ERROR);
        }



    }

    private String generarUsuario(String nombre, String apellido) {
        // Generar usuario con primera letra del nombre + apellido en minúsculas
        String usuario = (nombre.charAt(0) + apellido).toLowerCase()
                .replaceAll("[áéíóúñ]", "a")
                .replaceAll("\\s+", "");

        // Verificar si ya existe y agregar número si es necesario
        int contador = 1;
        String usuarioBase = usuario;
        while (empleadosDao.obtenerEmpleadoPorUsername(usuario) != null) {
            usuario = usuarioBase + contador++;
        }
        return usuario;
    }

    private String generarContrasenia() {
        // Generar contraseña segura de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder contrasenia = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            contrasenia.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return contrasenia.toString();
    }



    private void eliminarEmpleado(EmpleadosModel empleado) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este empleado?");
        alert.setContentText("Esta acción no se puede deshacer");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (empleadosDao.eliminarEmpleado(empleado.getId())) {
                    mostrarAlerta("Éxito", "Empleado eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarEmpleados();
                }
            }
        });
    }
    private void editarEmpleado(EmpleadosModel empleado) {
        // Cambiar a modo edición
        modoEdicion = true;
        empleadoEnEdicion = empleado;

        // Cargar datos en los campos
        txtNombre.setText(empleado.getNombre());
        txtApellido.setText(empleado.getApellido());
        txtDNI.setText(String.valueOf(empleado.getDNI()));
        txtTelefono.setText(String.valueOf(empleado.getTelefono()));

        // Cambiar el texto del botón
        btnAgregar.setText("Guardar Cambios");
    }

    private void guardarEdicion() {
        if (empleadoEnEdicion == null) return;

        boolean hayModificaciones = false;
        boolean todoCorrecto = true;
        StringBuilder mensaje = new StringBuilder("Se actualizaron los siguientes campos:\n");

        // Verificar y actualizar nombre si cambió
        String nuevoNombre = txtNombre.getText().trim();
        if (!nuevoNombre.equals(empleadoEnEdicion.getNombre())) {
            if (!nuevoNombre.isEmpty()) {
                empleadoEnEdicion.setNombre(nuevoNombre);
                mensaje.append("- Nombre\n");
                hayModificaciones = true;
            } else {
                mostrarAlerta("Error", "El nombre no puede estar vacío", Alert.AlertType.ERROR);
                todoCorrecto = false;
            }
        }

        // Verificar y actualizar apellido si cambió
        String nuevoApellido = txtApellido.getText().trim();
        if (!nuevoApellido.equals(empleadoEnEdicion.getApellido())) {
            if (!nuevoApellido.isEmpty()) {
                empleadoEnEdicion.setApellido(nuevoApellido);
                mensaje.append("- Apellido\n");
                hayModificaciones = true;
            } else {
                mostrarAlerta("Error", "El apellido no puede estar vacío", Alert.AlertType.ERROR);
                todoCorrecto = false;
            }
        }

        // Verificar y actualizar DNI si cambió
        try {
            int nuevoDNI = Integer.parseInt(txtDNI.getText().trim());
            if (nuevoDNI != empleadoEnEdicion.getDNI()) {
                empleadoEnEdicion.setDNI(nuevoDNI);
                mensaje.append("- DNI\n");
                hayModificaciones = true;
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El DNI debe ser un número válido", Alert.AlertType.ERROR);
            todoCorrecto = false;
        }

        // Verificar y actualizar teléfono si cambió
        try {
            int nuevoTelefono = Integer.parseInt(txtTelefono.getText().trim());
            if (nuevoTelefono != empleadoEnEdicion.getTelefono()) {
                empleadoEnEdicion.setTelefono(nuevoTelefono);
                mensaje.append("- Teléfono\n");
                hayModificaciones = true;
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El teléfono debe ser un número válido", Alert.AlertType.ERROR);
            todoCorrecto = false;
        }

        // Si hay modificaciones y todo es correcto, actualizar en la base de datos
        if (hayModificaciones && todoCorrecto) {
            if (empleadosDao.actualizarEmpleado(empleadoEnEdicion)) {
                mostrarAlerta("Éxito", mensaje.toString(), Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEmpleados();
                volverAModoAgregar();
            } else {
                mostrarAlerta("Error", "No se pudieron guardar los cambios", Alert.AlertType.ERROR);
            }
        } else if (!hayModificaciones && todoCorrecto) {
            mostrarAlerta("Información", "No se detectaron cambios", Alert.AlertType.INFORMATION);
        }
    }
    private void volverAModoAgregar() {
        modoEdicion = false;
        empleadoEnEdicion = null;
        btnAgregar.setText("Agregar Empleado");
        limpiarCampos();
    }



    private void cargarEmpleados() {
        List<EmpleadosModel> empleados = empleadosDao.obtenerTodosEmpleados();
        listaEmpleados.clear();
        listaEmpleados.addAll(empleados);
        tablaEmpleados.setItems(listaEmpleados);
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDNI.clear();
        txtTelefono.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
