package com.berbin.weatherapp;

import com.berbin.weatherapp.controllers.MainController;
import com.berbin.weatherapp.services.WeatherService;
import com.berbin.weatherapp.state.AppState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.Objects;
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * WeatherApp is the entry point of the application.
 * It initializes the JavaFX stage, loads the main FXML layout,
 * injects dependencies into the MainController, and displays the UI.
 */
public class WeatherApp extends Application {

    /**
     * Starts the JavaFX application.
     *
     * @param stage primary stage provided by the JavaFX runtime
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Load main FXML layout
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/berbin/weatherapp/fxml/main.fxml")
        );
        Parent root = loader.load();

        // Retrieve controller instance
        MainController controller = loader.getController();

        // Initialize application services and state
        WeatherService service = initializeWeatherService();
        AppState appState = new AppState();

        // Inject dependencies into controller
        controller.setService(service);
        controller.setAppState(appState);

        // Load the application icon from the resources folder safely
        try {
            Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/com/berbin/weatherapp/images/icons/app-icon.png")
            ));
            stage.getIcons().add(icon);
        } catch (NullPointerException e) {
            // Log a warning if the icon file is missing in resources
            System.err.println("Deployment Warning: Application icon 'app-icon.png' not found in resources.");
        }

        // Configure and show scene
        Scene scene = new Scene(root, 740, 580);
        stage.setTitle("Weather Information App");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main entry point. Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Orchestrates the application startup credentials flow.
     * Resolves the API key location, attempts to instantiate the service domain,
     * and intercepts initialization failures to prevent saving invalid tokens to disk.
     *
     * @return A validated WeatherService instance ready for use.
     */
    private static WeatherService initializeWeatherService() {
        File configFile = new File("config.properties");
        Properties prop = new Properties();
        String keyToValidate = null;
        boolean fileAlreadyExists = false;

        // 1. Attempt to read from an existing configuration file
        if (configFile.exists()) {
            try (FileInputStream input = new FileInputStream(configFile)) {
                prop.load(input);
                String existingKey = prop.getProperty("api.key");
                if (existingKey != null && !existingKey.trim().isEmpty() && !existingKey.equals("YOUR_API_KEY_HERE")) {
                    keyToValidate = existingKey.trim();
                    fileAlreadyExists = true;
                }
            } catch (IOException ex) {
                System.err.println("Portfolio System Error: Failed to open the configuration file: " + ex.getMessage());
            }
        }

        // 2. If no key was found in the file, prompt the user visually
        if (keyToValidate == null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("API Configuration Required");
            dialog.setHeaderText("OpenWeatherMap Credentials Missing");
            dialog.setContentText("Please input a valid OpenWeatherMap API Key to continue:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty() || result.get().trim().isEmpty()) {
                System.out.println("Portfolio System: Configuration canceled. Exiting runtime environment.");
                Platform.exit();
                System.exit(0);
            }
            keyToValidate = result.get().trim();
        }

        // 3. Attempt instantiation. WeatherService constructor handles remote verification.
        try {
            System.out.println("Portfolio System: Attempting service domain instantiation...");
            WeatherService validatedService = new WeatherService(keyToValidate);

            // 4. Verification successful: If it was a new inputted key, persist the file safely
            if (!fileAlreadyExists) {
                prop.setProperty("api.key", keyToValidate);
                try (FileOutputStream output = new FileOutputStream(configFile)) {
                    prop.store(output, "Automated Configuration File - Verified by WeatherService");
                    System.out.println("Portfolio System: Validation successful. API configuration written to disk.");
                } catch (IOException ex) {
                    System.err.println("Critical IO Error: Failed to write configuration file to disk: " + ex.getMessage());
                }
            }

            return validatedService;

        } catch (IllegalArgumentException ex) {
            // Intercept invalid 401 token authentication exceptions thrown by WeatherService
            showVisualError("Authentication Failure", ex.getMessage() + "\nExecution terminated.");
            Platform.exit();
            System.exit(0);
        } catch (RuntimeException ex) {
            // Intercept connection losses, timeouts or server anomalies
            showVisualError("Initialization Failure", ex.getMessage() + "\nExecution terminated.");
            Platform.exit();
            System.exit(0);
        }

        return null;
    }

    /**
     * Utility helper to display modal error warnings uniformly during the initialization stage.
     */
    private static void showVisualError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Critical Initialization Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
