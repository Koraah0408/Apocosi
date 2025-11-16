package cafeteria.cafeteria;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class ConfirmarPedidoController {

    @FXML private Label infoPedidoLabel;
    @FXML private TextArea direccionTextArea;

    private List<PedidoItem> itemsDelPedido;
    private double total;
    private String nombreUsuario;

    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
    }

    public void setDatosPedido(double total, List<PedidoItem> items) {
        this.itemsDelPedido = items;
        this.total = total;
        actualizarVista();
    }

    private void actualizarVista() {
        if (infoPedidoLabel != null) {
            int numItems = (itemsDelPedido != null) ? itemsDelPedido.size() : 0;
            infoPedidoLabel.setText(String.format("Estás por ordenar %d productos por un total de MXN %.2f.", numItems, total));
        }
    }

    @FXML
    private void confirmarPedidoFinal(ActionEvent event) {
        String direccion = direccionTextArea.getText().trim();

        if (direccion.isEmpty() || direccion.length() < 10) {
            mostrarAlerta("Error", "Por favor, ingresa una dirección detallada (mínimo 10 caracteres).");
            return;
        }

        // CREAR PEDIDO COMPLETO
        PedidoCompleto pedidoCompletado = new PedidoCompleto(itemsDelPedido, total, direccion);

        // GUARDAR EN BASE DE DATOS
        int usuarioId = DatabaseManager.obtenerUsuarioId(nombreUsuario);
        if (usuarioId != -1) {
            int ventaId = DatabaseManager.guardarVenta(pedidoCompletado, usuarioId);

            if (ventaId != -1) {
                // GUARDAR ITEMS EN BD
                for (PedidoItem item : itemsDelPedido) {
                    DatabaseManager.guardarDetalleVenta(ventaId, item);
                }

                // GUARDAR EN HISTORIAL EN MEMORIA
                HistorialPedidos.agregarPedido(pedidoCompletado);

                // LIMPIAR CARRITO
                Carrito.limpiarCarrito();

                // MOSTRAR CONFIRMACIÓN
                StringBuilder resumen = new StringBuilder();
                for (PedidoItem item : itemsDelPedido) {
                    resumen.append(item.getResumen()).append("\n");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("¡Pedido Confirmado!");
                alert.setHeaderText("✅ Tu pedido ha sido confirmado exitosamente.");
                alert.setContentText("Resumen:\n" + resumen.toString() +
                        "\nTotal: MXN " + String.format("%.2f", total) +
                        "\n\nTu pedido está 'En preparación'.");
                alert.showAndWait();

                regresarAlMenuPrincipal(event);
            } else {
                mostrarError("Error al guardar el pedido en la base de datos.");
            }
        } else {
            mostrarError("Error: No se pudo identificar al usuario.");
        }
    }

    @FXML
    private void volverAPersonalizar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent carritoView = loader.load();
            CarritoController controller = loader.getController();
            controller.setNombreUsuario(nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(carritoView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al regresar al carrito");
        }
    }

    private void regresarAlMenuPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al regresar al menú");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // --- MÉTODOS DE NAVEGACIÓN GLOBAL ---
    @FXML
    private void handleIrACarrito(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent view = loader.load();
            CarritoController controller = loader.getController();
            controller.setNombreUsuario(this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }
    }
}