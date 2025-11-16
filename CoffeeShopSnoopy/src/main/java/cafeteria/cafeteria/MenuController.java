package cafeteria.cafeteria;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException; // Añadido
import java.util.HashMap;
import java.util.Map;

public class MenuController {

    @FXML private Label saludoLabel;
    @FXML private Button carritoButton;

    // Renombrado para consistencia
    private String nombreUsuario;
    private Map<String, Double> precios = new HashMap<>();

    @FXML
    public void initialize() {
        precios.put("Chocolate caliente", 40.0); precios.put("Capuccino", 50.0);
        precios.put("Latte", 50.0); precios.put("Macchiato", 35.0);
        precios.put("Mocha", 50.0); precios.put("Americano", 40.0);
        precios.put("Flat White", 55.0);
        actualizarBotonCarrito();
    }

    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
        if (saludoLabel != null) saludoLabel.setText("Hola, " + nombre + "!");
        actualizarBotonCarrito();
    }

    private void actualizarBotonCarrito() {
        if (carritoButton != null) {
            int cantidad = Carrito.getCantidadItems();
            carritoButton.setText(String.format("Ver Carrito (%d)", cantidad));
        }
    }

    /**
     * Modificado para usar el nuevo método de navegación
     */
    @FXML
    private void handleVerCarrito(ActionEvent event) {
        handleIrACarrito(event);
    }

    private void seleccionarBebida(ActionEvent event, String nombreBebida) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalizacion-view.fxml"));
            Parent personalizarView = loader.load();
            PersonalizarController controller = loader.getController();
            controller.setDatosBebida(nombreBebida, precios.get(nombreBebida));
            controller.setNombreUsuario(nombreUsuario); // Pasamos el nombre
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(personalizarView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error cargando personalización");
        }
    }

    @FXML private void seleccionarChocolateCaliente(ActionEvent event) { seleccionarBebida(event, "Chocolate caliente"); }
    @FXML private void seleccionarCapuccino(ActionEvent event) { seleccionarBebida(event, "Capuccino"); }
    @FXML private void seleccionarLatte(ActionEvent event) { seleccionarBebida(event, "Latte"); }
    @FXML private void seleccionarMacchiato(ActionEvent event) { seleccionarBebida(event, "Macchiato"); }
    @FXML private void seleccionarMocha(ActionEvent event) { seleccionarBebida(event, "Mocha"); }
    @FXML private void seleccionarAmericano(ActionEvent event) { seleccionarBebida(event, "Americano"); }
    @FXML private void seleccionarFlatWhite(ActionEvent event) { seleccionarBebida(event, "Flat White"); }

    @FXML
    private void handleRegresarAction(ActionEvent event) {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginView, 400, 600));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje); alert.showAndWait();
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