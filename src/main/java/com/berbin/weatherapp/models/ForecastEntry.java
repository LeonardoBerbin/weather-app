package com.berbin.weatherapp.models;

import java.time.LocalDateTime;
import java.util.Objects;

import com.berbin.weatherapp.utils.ValidateUtils;

/**
 * Immutable record representing a single forecast prediction.
 *
 * Each entry corresponds to a forecasted weather condition
 * for a specific date and time.
 *
 * Units:
 * - Temperature: Celsius (°C)
 */
public record ForecastEntry(

    LocalDateTime dateTime, // Forecast date and time
    double temperature,     // Forecast temperature in Celsius
    String description,     // Weather condition description
    String weatherIconCode  // Icon code from API

) {

    /**
     * Compact constructor with validation logic.
     *
     * Ensures that all forecast fields contain valid values
     * before the record instance is created.
     */
    public ForecastEntry {

        dateTime = Objects.requireNonNull(dateTime, "Forecast date and time cannot be null.");

        ValidateUtils.requireText("Forecast description", description);
        ValidateUtils.requireText("Forecast weather icon code", weatherIconCode);
    }
}