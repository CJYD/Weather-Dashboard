package dev.cj.weather_dashboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OpenWeatherMap API integration.
 * Binds properties from application.yml with prefix "openweathermap.api"
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "openweathermap.api")
public class WeatherProperties {

    /**
     * OpenWeatherMap API key (required for authentication)
     * Set via environment variable OPENWEATHER_API_KEY or in application.yml
     */
    private String key;

    /**
     * Base URL for OpenWeatherMap API endpoints
     * Default: https://api.openweathermap.org/data/2.5
     */
    private String baseUrl;

    /**
     * HTTP request timeout in milliseconds
     * Default: 5000ms (5 seconds)
     */
    private int timeout = 5000;

    /**
     * Maximum number of retry attempts for failed requests
     * Default: 3
     */
    private int maxRetries = 3;
}
