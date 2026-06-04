package com.berbin.weatherapp.models;

import com.berbin.weatherapp.utils.ValidateUtils;

/**
 * Immutable record representing current weather conditions.
 *
 * Units:
 * - Temperature: Celsius (°C)
 * - Wind speed: meters per second (m/s)
 */
public record WeatherData(

    double temperature,     // Current temperature in Celsius
    double feelsLike,       // "Feels like" temperature in Celsius
    int humidity,           // Humidity percentage (0-100)
    int pressure,           // Atmospheric pressure in hPa
    double windSpeed,       // Wind speed in meters per second
    String description,     // Weather condition description
    String weatherIconCode  // Icon code from API

) {

    /**
     * Compact constructor with validation logic.
     *
     * Ensures that all weather condition fields contain valid values
     * before the record instance is created.
     */
    public WeatherData {
        
        ValidateUtils.requireText("Weather description", description);
        ValidateUtils.requireText("Weather icon code", weatherIconCode);
        ValidateUtils.requireRange("Weather humidity", humidity, 0, 100);
        ValidateUtils.requireMin("Weather pressure", pressure, 0);
        ValidateUtils.requireMin("Weather wind speed", windSpeed, 0);
    }
}