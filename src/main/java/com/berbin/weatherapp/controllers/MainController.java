package com.berbin.weatherapp.controllers;

import com.berbin.weatherapp.models.WeatherReport;
import com.berbin.weatherapp.services.WeatherService;
import com.berbin.weatherapp.state.AppState;
import com.berbin.weatherapp.state.UnitSystem;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * MainController is the central controller for the WeatherApp UI.
 * It manages application state, handles search queries, updates
 * weather information, and controls background images and overlays.
 */
public class MainController {

    /** Global reactive state (unit system). */
    private AppState appState;

    /** Weather service providing API access and caching. */
    private WeatherService service;

    /** Last fetched weather report. */
    private WeatherReport report;

    @FXML private StackPane rootPane;
    @FXML private VBox content;

    /** Blocking overlay shown during async fetch operations. */
    @FXML private StackPane loadingOverlay;

    /** ImageView used to display loading animation (GIF). */
    @FXML private ImageView loadingImage;

    /** Label displaying the current unit system. */
    @FXML private Label unitSystemLabel;

    /** Button to toggle between metric and imperial units. */
    @FXML private Button unitToggleButton;

    @FXML private SearchBarController searchBarController;
    @FXML private CurrentWeatherController currentWeatherController;
    @FXML private ForecastController forecastController;
    @FXML private HistoryBarController historyBarController;

    /**
     * Initializes UI components and sets up the loading animation.
     */
    @FXML
    private void initialize() {
        Image gif = new Image(
            getClass()
                .getResource("/com/berbin/weatherapp/images/loading.gif")
                .toExternalForm()
        );
        loadingImage.setImage(gif);

        unitToggleButton.setOnAction(e -> appState.toggleUnit());
    }

    /**
     * Injects the WeatherService dependency and registers search callbacks.
     * Also triggers an initial search for "Bogota".
     *
     * @param service WeatherService instance
     */
    public void setService(WeatherService service) {
        this.service = service;
        searchBarController.setOnSearch(this::handleSearch);
        historyBarController.setOnSearch(this::handleSearch);
        handleSearch("Bogota");
    }

    /**
     * Injects the AppState dependency and binds UI updates to unit system changes.
     *
     * @param appState application state instance
     */
    public void setAppState(AppState appState) {
        this.appState = appState;
        appState.unitProperty().addListener((obs, oldV, newV) -> renderUI());
        updateUnitLabel();
    }

    /**
     * Handles a search query asynchronously.
     * Shows loading overlay, fetches weather report in background,
     * updates UI on success, and shows error dialog on failure.
     *
     * @param query city name or location string
     */
    private void handleSearch(String query) {
        showLoading(true);

        Task<WeatherReport> task = new Task<>() {
            @Override
            protected WeatherReport call() throws Exception {
                return service.getWeatherReport(query);
            }
        };

        task.setOnSucceeded(e -> {
            report = task.getValue();
            renderUI();
            showLoading(false);
        });

        task.setOnFailed(e -> {
            showLoading(false);
            showError();
        });

        new Thread(task).start();
    }

    /**
     * Renders the UI components with the latest weather report and unit system.
     */
    private void renderUI() {
        if (report == null) return;

        UnitSystem unit = appState.getUnit();
        currentWeatherController.render(report, unit);
        forecastController.render(report.forecastEntries(), unit);
        historyBarController.render(service.getCachedHistory());

        updateUnitLabel();
        applyBackground(report);
        rootPane.requestFocus();
    }

    /**
     * Applies a background image to the root pane based on the report's local time.
     *
     * @param report WeatherReport containing local time
     */
    private void applyBackground(WeatherReport report) {
        String path = resolveBackground(report);

        Image image = new Image(
            getClass().getResource(path).toExternalForm()
        );

        BackgroundImage bgImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, false, true)
        );

        rootPane.setBackground(new Background(bgImage));
    }

    /**
     * Resolves which background image to use based on the hour of the day.
     *
     * @param report WeatherReport containing local time
     * @return path to background image resource
     */
    private String resolveBackground(WeatherReport report) {
        int hour = report.getLocalTime().getHour();
        String base = "/com/berbin/weatherapp/images/backgrounds/";

        if (hour >= 6 && hour < 12)  return base + "morning.png";
        if (hour >= 12 && hour < 17) return base + "day.png";
        if (hour >= 17 && hour < 19) return base + "sunset.png";

        return base + "night.png";
    }

    /**
     * Shows or hides the loading overlay.
     *
     * @param show true to show overlay, false to hide
     */
    private void showLoading(boolean show) {
        loadingOverlay.setVisible(show);
        loadingOverlay.setManaged(show);
    }

    /**
     * Updates the unit system label text.
     */
    private void updateUnitLabel() {
        unitSystemLabel.setText(appState.getUnit().toString());
    }

    /**
     * Displays an error dialog when the weather service fails.
     */
    private void showError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Weather Service Error");
            alert.setHeaderText("Unable to fetch weather report");
            alert.setContentText("The request did not return any results.");
            alert.showAndWait();
        });
    }
}
