package cafeteria.cafeteria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField nombreField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleIngresarAction() {
        String username = nombreField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.");
            return;
        }

        // VERIFICAR EN BASE DE DATOS
        if (DatabaseManager.verificarCredenciales(username, password)) {
            // Login exitoso
            irAMenu(username);
        } else {
            mostrarAlerta("Error", "Credenciales incorrectas.");
        }
    }

    @FXML
    private void handleRegistroLink(ActionEvent event) {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("register-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de registro.");
        }
    }

    private void irAMenu(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();

            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(username);

            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el menú.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}