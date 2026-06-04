package com.berbin.weatherapp.utils;

import com.berbin.weatherapp.state.UnitSystem;

/**
 * Utility class responsible for converting weather-related numeric values
 * between Metric and Imperial unit systems.
 *
 * This class ONLY handles mathematical conversions.
 * It does NOT format strings or interact with the UI.
 */
public class UnitConverterUtil {

    /** Private constructor to prevent instantiation. */
    private UnitConverterUtil() {}

    /**
     * Converts temperature from Celsius (default API unit)
     * to the selected unit system.
     *
     * @param celsius temperature value in Celsius
     * @param unit target unit system (METRIC or IMPERIAL)
     * @return converted temperature as a numeric value
     */
    public static double toTemperature(double celsius, UnitSystem unit) {
        return switch (unit) {
            case METRIC -> celsius;
            case IMPERIAL -> celsius * 9 / 5 + 32;
        };
    }

    /**
     * Converts wind speed from meters per second (m/s)
     * to the selected unit system.
     *
     * @param ms wind speed in meters per second
     * @param unit target unit system (METRIC or IMPERIAL)
     * @return converted wind speed as a numeric value
     */
    public static double toWindSpeed(double ms, UnitSystem unit) {
        return switch (unit) {
            case METRIC -> ms;
            case IMPERIAL -> ms * 2.237;
        };
    }
}