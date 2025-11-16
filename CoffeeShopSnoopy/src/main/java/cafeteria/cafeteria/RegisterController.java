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

public class RegisterController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField usernameField; // AÑADIDO: Campo para username

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String nombre = nombreField.getText().trim();
        String username = usernameField.getText().trim(); // AÑADIDO
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validación
        if (nombre.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta("Error de Validación", "Por favor, completa todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error de Validación", "Las contraseñas no coinciden.");
            return;
        }

        // REGISTRO EN BASE DE DATOS
        if (DatabaseManager.registrarUsuario(nombre, username, password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("¡Cuenta creada exitosamente!\nAhora puedes iniciar sesión.");
            alert.showAndWait();

            handleVolverLogin(event);
        } else {
            mostrarAlerta("Error", "No se pudo registrar el usuario.\nEl username ya existe.");
        }
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al inicio de sesión.");
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