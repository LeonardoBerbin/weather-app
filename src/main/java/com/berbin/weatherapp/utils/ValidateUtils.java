package com.berbin.weatherapp.utils;

/**
 * Utility class for validating input values.
 * All methods throw IllegalArgumentException when validation fails.
 * 
 * This class is final and has a private constructor to prevent instantiation.
 */
public final class ValidateUtils {

    // Private constructor to prevent instantiation
    private ValidateUtils() {}

    /**
     * Ensures that a text value is not null or blank.
     *
     * @param label descriptive name of the field
     * @param value the string to validate
     * @throws IllegalArgumentException if the value is null or blank
     */
    public static void requireText(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be null or blank.");
        }
    }

    /**
     * Ensures that a numeric value is greater than or equal to a minimum.
     *
     * @param label descriptive name of the field
     * @param value the number to validate
     * @param min   the minimum allowed value
     * @throws IllegalArgumentException if the value is less than min
     */
    public static void requireMin(String label, double value, double min) {
        if (value < min) {
            throw new IllegalArgumentException(
                String.format("%s must be greater than or equal to %.2f.", label, min)
            );
        }
    }

    /**
     * Ensures that a numeric value is less than or equal to a maximum.
     *
     * @param label descriptive name of the field
     * @param value the number to validate
     * @param max   the maximum allowed value
     * @throws IllegalArgumentException if the value is greater than max
     */
    public static void requireMax(String label, double value, double max) {
        if (value > max) {
            throw new IllegalArgumentException(
                String.format("%s must be less than or equal to %.2f.", label, max)
            );
        }
    }

    /**
     * Ensures that a numeric value falls within a given range (inclusive).
     *
     * @param label descriptive name of the field
     * @param value the number to validate
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @throws IllegalArgumentException if the value is outside the range
     */
    public static void requireRange(String label, double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("%s must be between %.2f and %.2f.", label, min, max)
            );
        }
    }
}