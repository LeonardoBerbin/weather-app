package com.berbin.weatherapp.state;

/** 
 * Represents the supported unit systems for weather data in the application.
 *  
 * It is used by converters and formatters to ensure consistent
 * representation of weather data across the application.
 */
public enum UnitSystem {
    METRIC,     // Metric system (Celsius, m/s)
    IMPERIAL    // Imperial system (Fahrenheit, mph)
}