package com.berbin.weatherapp.services.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DTO for deserializing forecast data
 * from the OpenWeatherMap API.
 */
public record ForecastDTO(
    List<ForecastItem> list,
    City city
) {

    /** DTO containing forecast information for a single time slot. */
    public record ForecastItem(
        Main main,
        List<WeatherInfo> weather,

        @SerializedName("dt_txt")
        String dateTimeText // Renamed

    ) {}

    /** DTO containing forecast temperature data. */
    public record Main(
        double temp
    ) {}

    /** DTO containing weather condition details. */
    public record WeatherInfo(
        String description,
        String icon
    ) {}

    /** DTO containing city information. */
    public record City(
        String name,
        String country
    ) {}
}