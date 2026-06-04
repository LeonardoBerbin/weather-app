package com.berbin.weatherapp.controllers;

import java.time.format.DateTimeFormatter;

import com.berbin.weatherapp.models.ForecastEntry;
import com.berbin.weatherapp.state.UnitSystem;
import com.berbin.weatherapp.utils.IconResolverUtil;
import com.berbin.weatherapp.utils.WeatherFormatterUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * Controller responsible for rendering a single forecast entry.
 *
 * This controller maps forecast data into a reusable forecast card
 * component. Each instance represents one forecast item displayed
 * within the forecast list.
 */
public class ForecastCardController {

    @FXML private ImageView iconView;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label temperatureLabel;

    /** Formatter used for forecast date and time display */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd HH:mm");

    /**
     * Populates the forecast card with forecast data.
     *
     * The provided forecast entry is formatted and mapped into
     * the card UI components, including date, description,
     * temperature, and weather icon.
     *
     * @param item forecast entry to display
     * @param unit preferred unit system
     */
    public void setData(ForecastEntry item, UnitSystem unit) {

        dateLabel.setText(item.dateTime().format(DATE_FORMAT));
        descriptionLabel.setText(item.description());

        temperatureLabel.setText(
            WeatherFormatterUtil.temperature(
                item.temperature(),
                unit
            )
        );

        iconView.setImage(
            IconResolverUtil.getWeatherIcon(item.weatherIconCode())
        );
    }
}