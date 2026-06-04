package com.berbin.weatherapp;

import com.berbin.weatherapp.controllers.MainController;
import com.berbin.weatherapp.services.WeatherService;
import com.berbin.weatherapp.state.AppState;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        WeatherService service = new WeatherService("YOUR_KEY");
        AppState appState = new AppState();

        // Inject dependencies into controller
        controller.setService(service);
        controller.setAppState(appState);

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
}
