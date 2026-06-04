package com.berbin.weatherapp.controllers;

import java.time.format.DateTimeFormatter;

import com.berbin.weatherapp.models.WeatherData;
import com.berbin.weatherapp.models.WeatherReport;
import com.berbin.weatherapp.state.UnitSystem;
import com.berbin.weatherapp.utils.IconResolverUtil;
import com.berbin.weatherapp.utils.WeatherFormatterUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Controller responsible for rendering the current weather view.
 *
 * This class connects the WeatherReport domain model with the JavaFX UI.
 * It is responsible for displaying weather data, managing empty states,
 * and formatting domain information into UI-friendly values.
 *
 * The controller does not fetch data. It only renders data provided
 * by the service layer.
 */
public class CurrentWeatherController {

    /** Label displayed when no weather data is available */
    @FXML private Label noDataLabel;

    /** Root container of the weather card */
    @FXML private HBox cardPane;
    
    /** Data fields */
    @FXML private Label cityLabel;
    @FXML private Label localTimeLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label detailsLabel;
    @FXML private Label countryCodeLabel;
    @FXML private ImageView countryFlagView;
    @FXML private ImageView weatherIconView;

    /** Formatter used to display local time in HH:mm format */
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Initializes the controller after FXML loading.
     *
     * Sets the initial UI state by rendering an empty view.
     * Ensures the interface starts in a consistent state
     * before any weather data is provided.
     */
    @FXML
    private void initialize() {
        render(null, null);
    }

    /**
     * Renders weather information into the UI.
     *
     * This is the main entry point for updating the view.
     * It maps WeatherReport data into JavaFX components and
     * handles both valid and empty states.
     *
     * When the report is null, the empty state is displayed
     * and the weather card is hidden.
     *
     * @param report weather data model containing current conditions
     * @param unit temperature unit system (Celsius, Fahrenheit, etc.)
     */
    public void render(WeatherReport report, UnitSystem unit) {

        // Handle empty state
        if (report == null) {
            noDataLabel.setVisible(true);
            cardPane.setVisible(false);
            return;
        }

        noDataLabel.setVisible(false);
        cardPane.setVisible(true);

        // Header
        setCountryFlag(report.countryCode());
        cityLabel.setText(report.city());
        localTimeLabel.setText(report.getLocalTime().format(TIME_FORMAT));


        WeatherData data = report.currentWeather();

        // Main data
        weatherIconView.setImage(IconResolverUtil.getWeatherIcon(data.weatherIconCode()));
        temperatureLabel.setText(WeatherFormatterUtil.temperature(data.temperature(), unit));
        descriptionLabel.setText(data.description());

        // Detailed metrics block
        detailsLabel.setText(buildDetails(data, unit));
    }

    /**
     * Builds formatted weather details string.
     *
     * Combines humidity, pressure, wind speed, and perceived temperature
     * into a readable multi-line format for display.
     *
     * @param data current weather data
     * @param unit temperature unit system
     * @return formatted string with weather details
     */
    private String buildDetails(WeatherData data, UnitSystem unit) {
        return String.format("""
            Feels like: %s
            Humidity: %s%%
            Pressure: %s hPa
            Wind Speed: %s
            """,
            WeatherFormatterUtil.temperature(data.feelsLike(), unit),
            data.humidity(),
            data.pressure(),
            WeatherFormatterUtil.windSpeed(data.windSpeed(), unit)
        );
    }

    /**
     * Sets country flag image or shows ISO fallback text.
     *
     * If a flag image exists for the given country code, it is displayed.
     * Otherwise, the ISO2 code is shown as text fallback.
     *
     * @param iso2 two-letter ISO country code
     */
    private void setCountryFlag(String iso2) {

        Image flag = IconResolverUtil.getFlag(iso2);

        if (flag != null) {
            countryFlagView.setImage(flag);
            countryFlagView.setVisible(true);
            countryCodeLabel.setVisible(false);
        } else {
            countryFlagView.setImage(null);
            countryFlagView.setVisible(false);
            countryCodeLabel.setText(iso2.toUpperCase());
            countryCodeLabel.setVisible(true);
        }
    }
}