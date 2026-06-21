package com.berbin.weatherapp.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Central utility for resolving UI icons used in the application.
 *
 * Responsibilities:
 * - Load and cache country flags (PNG files)
 * - Extract weather icons from a spritesheet
 *
 * This class does NOT manage UI state or business logic.
 * It only resolves visual assets.
 */
public class IconResolverUtil {

    private IconResolverUtil() {}

    /**
     * Cache for loaded country flag images.
     * Prevents reloading the same image multiple times.
     */
    private static final Map<String, Image> FLAG_CACHE = new HashMap<>();

    /**
     * Preloaded weather spritesheet image.
     * This contains all weather icons in a grid format.
     */
    private static final Image WEATHER_SPRITESHEET =
        new Image(IconResolverUtil.class.getResourceAsStream(
            "/com/berbin/weatherapp/images/icons/weather-condition.png"
        ));

    // -----------------------------
    // FLAGS
    // -----------------------------

    /**
     * Loads a country flag by ISO2 code.
     * Uses caching to avoid repeated disk access.
     *
     * @param iso2 country code (e.g. "CO", "US")
     * @return Image of the flag, or null if not found
     */
    public static Image getFlag(String iso2) {

        String key = iso2.toLowerCase();

        return FLAG_CACHE.computeIfAbsent(key, k -> {

            String path = "/com/berbin/weatherapp/images/icons/flags/" + k.toUpperCase() + ".png";
            var stream = IconResolverUtil.class.getResourceAsStream(path);

            // Defensive check: avoid NullPointerException
            if (stream == null) {
                System.err.println("Flag not found: " + path);
                return null;
            }
            return new Image(stream);
        });
    }

    // -----------------------------
    // WEATHER ICONS (SPRITESHEET)
    // -----------------------------

    /**
     * Extracts a weather icon from the spritesheet using grid coordinates.
     *
     * DESIGN:
     * - Returns Image (NOT ImageView)
     * - UI layer is responsible for rendering
     *
     * @param col column index in spritesheet
     * @param row row index in spritesheet
     * @param size size of each icon (width/height in pixels)
     * @return Image extracted from spritesheet
     */
    public static Image getWeatherIcon(int col, int row, int width, int height) {

        PixelReader reader = WEATHER_SPRITESHEET.getPixelReader();

        return new WritableImage(
            reader,
            col * width,
            row * height,
            width,
            height
        );
    }

    // -----------------------------
    // WEATHER ICONS (SPRITESHEET MAPPING)
    // -----------------------------

    /**
     * Maps OpenWeather icon codes to positions in the spritesheet.
     *
     * Each weather condition has a day/night variant:
     * - "d" = day
     * - "n" = night
     *
     * Example:
     * 01d = clear sky day
     * 01n = clear sky night
     *
     * @param code OpenWeather icon code (e.g. "01d", "10n")
     * @return Image extracted from spritesheet
     */
    public static Image getWeatherIcon(String code) {

        // Dimensions of each sprite inside the sheet
        int width = 234;
        int height = 256;

        return switch (code) {
            case "01d" -> getWeatherIcon(0, 0, width, height);
            case "01n" -> getWeatherIcon(0, 1, width, height);
            case "02d" -> getWeatherIcon(1, 0, width, height);
            case "02n" -> getWeatherIcon(1, 1, width, height);
            case "03d" -> getWeatherIcon(2, 0, width, height);
            case "03n" -> getWeatherIcon(2, 1, width, height);
            case "04d" -> getWeatherIcon(3, 0, width, height);
            case "04n" -> getWeatherIcon(3, 1, width, height);
            case "09d" -> getWeatherIcon(4, 0, width, height);
            case "09n" -> getWeatherIcon(4, 1, width, height);
            case "10d" -> getWeatherIcon(5, 0, width, height);
            case "10n" -> getWeatherIcon(5, 1, width, height);
            case "11d" -> getWeatherIcon(0, 2, width, height);
            case "11n" -> getWeatherIcon(1, 2, width, height);
            case "13d" -> getWeatherIcon(2, 2, width, height);
            case "13n" -> getWeatherIcon(3, 2, width, height);
            case "50d" -> getWeatherIcon(4, 2, width, height);
            case "50n" -> getWeatherIcon(5, 2, width, height);
            default    -> getWeatherIcon(0, 0, width, height);
        };
    }
}