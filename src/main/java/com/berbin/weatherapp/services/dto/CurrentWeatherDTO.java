package com.berbin.weatherapp.services.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DTO for deserializing current weather data
 * from the OpenWeatherMap API.
 */
public record CurrentWeatherDTO(
    String name,
    int timezone,
    Sys sys,
    Main main,
    Wind wind,
    List<WeatherInfo> weather
) {

    /** DTO containing country information. */
    public record Sys(
        String country
    ) {}

    /** DTO containing the main weather measurements. */
    public record Main(
        double temp,
        int humidity,
        int pressure,

        @SerializedName("feels_like")
        double feelsLike   // Renamed
    ) {}

    /** DTO containing wind information. */
    public record Wind(
        double speed
    ) {}

    /** DTO containing weather condition details. */
    public record WeatherInfo(
        String description,
        String icon
    ) {}
}