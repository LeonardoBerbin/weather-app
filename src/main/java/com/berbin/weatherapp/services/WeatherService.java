package com.berbin.weatherapp.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.berbin.weatherapp.models.ForecastEntry;
import com.berbin.weatherapp.models.WeatherData;
import com.berbin.weatherapp.models.WeatherReport;
import com.berbin.weatherapp.services.dto.CurrentWeatherDTO;
import com.berbin.weatherapp.services.dto.ForecastDTO;
import com.berbin.weatherapp.utils.ValidateUtils;
import com.google.gson.Gson;

/**
 * WeatherService is responsible for fetching current weather and forecast data
 * from the OpenWeatherMap API, mapping it into domain models, and caching results
 * to reduce redundant API calls.
 */
public class WeatherService {

    /** Base URL for OpenWeatherMap API endpoints */
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    /** Duration for which cached weather reports remain valid */
    private static final Duration CACHE_DURATION = Duration.ofMinutes(10);

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    /** Cache storing up to 5 recent WeatherReport entries */
    private final Map<String, WeatherReport> cache;

    /**
     * Constructs a WeatherService with the provided API key.
     * Validates the key with OpenWeatherMap API before completing initialization.
     * 
     * @param apiKey OpenWeatherMap API key (required, non-null, non-blank)
     * @throws RuntimeException If the API key is invalid or unauthorized.
     */
    public WeatherService(String apiKey) {
        
        ValidateUtils.requireText("Weather service API key", apiKey);

        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();

        // LinkedHashMap with access-order to evict oldest entry when size > 5
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, WeatherReport> eldest) {
                return size() > 5;
            }
        };

        // Validate the key against the remote server immediately
        validateApiKey();
    }

    /**
     * Performs a lightweight test query to validate the API key status.
     * 
     * @throws RuntimeException If the server responds with 401 Unauthorized or other errors.
     */
    private void validateApiKey() {
        // Lightweight request to London just to verify the credentials
        String testUrl = BASE_URL + "weather?q=London&appid=" + this.apiKey;
        
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(testUrl))
                .GET()
                .build();
                
        try {
            java.net.http.HttpResponse<String> response = this.httpClient.send(
                    request, 
                    java.net.http.HttpResponse.BodyHandlers.ofString()
            );
            
            // 401 means Unauthorized (Invalid API Key)
            if (response.statusCode() == 401) {
                throw new IllegalArgumentException("The provided API key is unauthorized or invalid.");
            } else if (response.statusCode() != 200) {
                throw new IOException("Server returned an unexpected HTTP status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            // Restore interrupted status for safety
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to validate API credentials via network.", e);
        }
    }

    /**
     * Returns a snapshot of the cached weather reports.
     *
     * @return A copy of the cache.
     */
    public Map<String, WeatherReport> getCachedHistory() {
        return new LinkedHashMap<>(cache);
    }

    /**
     * Retrieves a WeatherReport for the given query.
     * Uses cache if a valid entry exists for the same normalized query.
     *
     * Query normalization prevents duplicate cache entries caused by
     * differences in capitalization or leading/trailing spaces. It does
     * not prevent duplicates when different queries refer to the same
     * location (e.g. "Sevilla" and "Seville").
     *
     * To keep the search history consistent, any existing cache entry
     * representing the same city and country returned by the API is
     * removed before storing the new report. This ensures that a location
     * appears only once in the cache, regardless of the query used.
     *
     * @param query Location query (non-null, non-blank).
     * @return WeatherReport containing current weather and forecast information.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    public WeatherReport getWeatherReport(String query)
        throws IOException, InterruptedException {

        ValidateUtils.requireText("Query", query);
        String key = query.trim().toLowerCase();

        // Reuse cached data when the same query was recently executed.
        WeatherReport cached = cache.get(key);
        if (cached != null && isCacheValid(cached)) {
            return cached;
        }

        // Retrieve fresh data from the API.
        WeatherReport fresh = buildReport(
            fetchCurrentWeather(query),
            fetchForecast(query)
        );

        // Remove any existing entry that represents the same location,
        // even if it was originally searched using a different query.
        String duplicateKey = null;

        for (Map.Entry<String, WeatherReport> entry : cache.entrySet()) {

            WeatherReport report = entry.getValue();
            if (
                fresh.city().equals(report.city()) && 
                fresh.countryCode().equals(report.countryCode())
            ) {
                duplicateKey = entry.getKey();
                break;
            }
        }

        if (duplicateKey != null) {
            cache.remove(duplicateKey);
        }

        // Store the report using the normalized query as the cache key.
        cache.put(key, fresh);
        return fresh;
    }

    /**
     * Builds a WeatherReport object from DTOs.
     *
     * @param currentDTO CurrentWeatherDTO containing current weather data.
     * @param forecastDTO ForecastDTO containing forecast data.
     * @return WeatherReport combining current and forecast information.
     */
    private WeatherReport buildReport(
        CurrentWeatherDTO currentDTO,
        ForecastDTO forecastDTO
    ) {
        return new WeatherReport(
            currentDTO.name(), 
            currentDTO.sys().country(),
            currentDTO.timezone(),
            mapCurrentWeather(currentDTO),
            mapForecast(forecastDTO)
        );
    }

    /**
     * Maps CurrentWeatherDTO into WeatherData domain model.
     *
     * @param dto CurrentWeatherDTO object with raw API response data.
     * @return WeatherData mapped domain model with temperature, humidity, etc.
     */
    private WeatherData mapCurrentWeather(CurrentWeatherDTO dto) {

        CurrentWeatherDTO.WeatherInfo weather = dto.weather().getFirst();
        return new WeatherData(
            dto.main().temp(), 
            dto.main().feelsLike(), 
            dto.main().humidity(), 
            dto.main().pressure(), 
            dto.wind().speed(),
            weather.description(), 
            weather.icon()
        );
    }

    /**
     * Maps ForecastDTO into a list of ForecastEntry domain models.
     *
     * @param dto ForecastDTO object containing forecast data from API.
     * @return List of ForecastEntry objects representing forecast timeline.
     */
    private List<ForecastEntry> mapForecast(ForecastDTO dto) {
        return dto.list()
            .stream()
            .map(this::mapForecastEntry)
            .toList();
    }

    /**
     * Maps a single ForecastItem into ForecastEntry.
     *
     * @param item ForecastItem containing raw forecast data.
     * @return ForecastEntry mapped domain model with date, temperature, and weather info.
     */
    private ForecastEntry mapForecastEntry(ForecastDTO.ForecastItem item) {

        ForecastDTO.WeatherInfo weather = item.weather().getFirst();
        return new ForecastEntry(
            LocalDateTime.parse(
                item.dateTimeText(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ),
            item.main().temp(), 
            weather.description(),
            weather.icon()
        );
    }

    /**
     * Fetches forecast data from OpenWeatherMap API.
     *
     * @param query Location query accepted by OpenWeatherMap
     *              (e.g. "Bogota", "Valencia,VE", "London,GB").
     * @return ForecastDTO containing forecast data.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    private ForecastDTO fetchForecast(String query)
        throws IOException, InterruptedException {

        String url = String.format(
            "%sforecast?q=%s&appid=%s&units=metric",
            BASE_URL,
            URLEncoder.encode(query, StandardCharsets.UTF_8),
            apiKey
        );

        String json = fetch(url);
        return gson.fromJson(json, ForecastDTO.class);
    }

    /**
     * Fetches current weather data from OpenWeatherMap API.
     *
     * @param query Location query accepted by OpenWeatherMap
     *              (e.g. "Bogota", "Valencia,VE", "London,GB").
     * @return CurrentWeatherDTO containing current weather data.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    private CurrentWeatherDTO fetchCurrentWeather(String query)
        throws IOException, InterruptedException {

        String url = String.format(
            "%sweather?q=%s&appid=%s&units=metric", 
            BASE_URL,
            URLEncoder.encode(query, StandardCharsets.UTF_8),
            apiKey
        ); 

        String json = fetch(url);
        return gson.fromJson(json, CurrentWeatherDTO.class);
    }

    /**
     * Executes HTTP GET request and returns response body as String.
     *
     * @param url Fully constructed API request URL.
     * @return Response body as JSON string.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    private String fetch(String url) 
        throws IOException, InterruptedException {
            
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response =
            httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );

        return response.body();
    }

    /**
     * Checks if cached WeatherReport is still valid based on CACHE_DURATION.
     *
     * @param report WeatherReport to validate.
     * @return true if cache entry is still valid, false otherwise.
     */
    private boolean isCacheValid(WeatherReport report) {
        return Duration
            .between(
                report.retrievedAt(), 
                LocalDateTime.now()
            )
            .compareTo(CACHE_DURATION) < 0;
    }
}