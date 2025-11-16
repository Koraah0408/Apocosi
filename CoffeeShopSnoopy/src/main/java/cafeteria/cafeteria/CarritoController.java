package cafeteria.cafeteria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarritoController {

    @FXML private ListView<PedidoItem> listaCarrito;
    @FXML private Label subtotalLabel;
    @FXML private TextField direccionField; // Campo de dirección añadido

    // --- NUEVOS BOTONES DE NAVEGACIÓN ---
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;

    private String nombreUsuario;

    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
    }

    @FXML
    public void initialize() {
        // Cargar los items del carrito estático
        cargarItemsCarrito();

        // Deshabilitar el botón de carrito (ya estamos aquí)
        if (navCarritoButton != null) {
            navCarritoButton.setDisable(true);
        }
    }

    private void cargarItemsCarrito() {
        listaCarrito.getItems().setAll(Carrito.getItems());
        subtotalLabel.setText(String.format("MXN %.2f", Carrito.getSubtotal()));
    }

    @FXML
    private void handleRemoverItem(ActionEvent event) {
        PedidoItem selectedItem = listaCarrito.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            mostrarAlerta("Error", "No has seleccionado ningún item para remover.");
            return;
        }
        Carrito.removerItem(selectedItem);
        cargarItemsCarrito();
    }

    @FXML
    private void handleVolverMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al menú.");
        }
    }

    @FXML
    private void handleConfirmarPedido() {
        // Verificar que el carrito no esté vacío
        if (Carrito.getItems().isEmpty()) {
            mostrarError("Carrito vacío", "No hay items en el carrito para confirmar.");
            return;
        }

        if (direccionField.getText().isEmpty()) {
            mostrarError("Dirección requerida", "Por favor ingresa una dirección de entrega");
            return;
        }

        try {
            List<PedidoItem> items = Carrito.getItems();
            double total = Carrito.getSubtotal();
            String direccion = direccionField.getText();

            // Crear pedido completo
            PedidoCompleto pedido = new PedidoCompleto(new ArrayList<>(items), total, direccion);

            // Guardar en base de datos
            int usuarioId = DatabaseManager.obtenerUsuarioId(nombreUsuario);
            if (usuarioId != -1) {
                int ventaId = DatabaseManager.guardarVenta(pedido, usuarioId);

                if (ventaId != -1) {
                    // Guardar cada item del pedido
                    for (PedidoItem item : items) {
                        DatabaseManager.guardarDetalleVenta(ventaId, item);
                    }

                    // Agregar al historial en memoria
                    HistorialPedidos.agregarPedido(pedido);

                    // Limpiar carrito
                    Carrito.limpiarCarrito();

                    mostrarConfirmacion("Pedido confirmado", "Tu pedido ha sido registrado exitosamente");

                    // ✅ CORRECCIÓN: Usar el nuevo método sin parámetros
                    regresarAlMenuDespuesDeConfirmar();

                } else {
                    mostrarError("Error", "No se pudo guardar el pedido en la base de datos.");
                }
            } else {
                mostrarError("Error de usuario", "No se pudo identificar al usuario.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "Error al confirmar el pedido: " + e.getMessage());
        }
    }

    // ✅ NUEVO MÉTODO: Para regresar al menú sin necesidad de ActionEvent
    private void regresarAlMenuDespuesDeConfirmar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(nombreUsuario);

            // Usamos cualquier nodo de la escena actual para obtener el Stage
            Stage stage = (Stage) direccionField.getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo regresar al menú.");
        }
    }

    @FXML
    private void handleRegresarMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al menú.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // --- MÉTODOS DE NAVEGACIÓN GLOBAL ---

    @FXML
    private void handleIrACarrito(ActionEvent event) {
        // No hacer nada, ya estamos en el carrito
    }

    @FXML
    private void handleIrAHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historial-view.fxml"));
            Parent view = loader.load();
            HistorialController controller = loader.getController();
            controller.setNombreUsuario(this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiarCarrito(ActionEvent event) {
        if (!Carrito.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Limpiar carrito");
            alert.setHeaderText("¿Estás seguro de que quieres limpiar el carrito?");
            alert.setContentText("Esta acción no se puede deshacer.");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    Carrito.limpiarCarrito();
                    cargarItemsCarrito();
                    mostrarConfirmacion("Carrito limpiado", "Todos los items han sido removidos del carrito.");
                }
            });
        } else {
            mostrarAlerta("Carrito vacío", "El carrito ya está vacío.");
        }
    }
}