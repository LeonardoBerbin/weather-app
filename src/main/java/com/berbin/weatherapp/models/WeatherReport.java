package com.berbin.weatherapp.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import com.berbin.weatherapp.utils.ValidateUtils;

/**
 * Immutable record representing a complete weather query.
 *
 * Contains location information, retrieval timestamp,
 * current weather conditions and forecast data.
 */
public record WeatherReport(

    String city,                            // City name
    String countryCode,                     // ISO country code (2 letters)
    int timezoneOffset,                     // UTC offset in seconds
    WeatherData currentWeather,             // Current weather conditions
    List<ForecastEntry> forecastEntries,    // Forecast entries
    LocalDateTime retrievedAt               // Query timestamp

) {

    /**
     * Convenience constructor.
     *
     * Automatically assigns the current date and time to
     * the retrievedAt field.
     */
    public WeatherReport(
        String city,
        String countryCode,
        int timezoneOffSet,
        WeatherData currentWeather,
        List<ForecastEntry> forecastEntries
    ) {
        this(
            city,
            countryCode,
            timezoneOffSet,
            currentWeather,
            forecastEntries,
            LocalDateTime.now()
        );
    }

    /**
     * Compact constructor with validation logic.
     *
     * Ensures that all report fields contain valid values
     * before the record instance is created.
     */
    public WeatherReport {

        ValidateUtils.requireText("Report city", city);
        ValidateUtils.requireText("Report country code", countryCode);

        countryCode = countryCode.trim().toUpperCase();

        if (!countryCode.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Report country code must contain exactly two letters.");
        }

        currentWeather  = Objects.requireNonNull(currentWeather, "Report current weather cannot be null.");
        forecastEntries = Objects.requireNonNull(forecastEntries, "Report forecast entries cannot be null.");

        if (forecastEntries.isEmpty()) {
            throw new IllegalArgumentException("Report forecast entries cannot be empty.");
        }
    }

    /**
     * Calculates the current local date and time
     * for the queried city using the timezone offset
     * returned by OpenWeatherMap.
     *
     * @return Current local date and time in the city.
     */
    public LocalDateTime getLocalTime() {
        return LocalDateTime.ofInstant(
            Instant.now(),
            ZoneOffset.ofTotalSeconds(timezoneOffset)
        );
    }
}