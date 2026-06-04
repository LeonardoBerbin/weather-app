package com.berbin.weatherapp.utils;

import com.berbin.weatherapp.state.UnitSystem;

/**
 * Utility class responsible for formatting weather values
 * into user-friendly strings for UI display.
 *
 * This class:
 * - Uses UnitConverterUtil for numeric conversion
 * - Applies rounding and formatting rules
 * - Adds unit symbols (°C, °F, m/s, mph)
 *
 * It does NOT store state or perform business logic.
 */
public class WeatherFormatterUtil {

    /** Private constructor to prevent instantiation. */
    private WeatherFormatterUtil() {}

    /**
     * Converts a temperature value and formats it for display.
     *
     * Process:
     * 1. Converts Celsius to selected unit system
     * 2. Rounds the value
     * 3. Appends unit symbol (°C or °F)
     *
     * Example:
     * - 25°C → "25°C"
     * - 25°C → "77°F"
     *
     * @param value temperature in Celsius
     * @param unit target unit system
     * @return formatted temperature string
     */
    public static String temperature(double value, UnitSystem unit) {
        value = UnitConverterUtil.toTemperature(value, unit);

        return Math.round(value) +
               (unit == UnitSystem.METRIC ? "°C" : "°F");
    }

    /**
     * Converts and formats wind speed for display.
     *
     * Process:
     * 1. Converts m/s to selected unit system
     * 2. Formats to 1 decimal place
     * 3. Appends unit label (m/s or mph)
     *
     * Example:
     * - 5.2 m/s → "5.2 m/s"
     * - 5.2 m/s → "11.6 mph"
     *
     * @param value wind speed in m/s
     * @param unit target unit system
     * @return formatted wind speed string
     */
    public static String windSpeed(double value, UnitSystem unit) {
        value = UnitConverterUtil.toWindSpeed(value, unit);

        return String.format("%.1f %s",
            value,
            unit == UnitSystem.METRIC ? "m/s" : "mph"
        );
    }
}