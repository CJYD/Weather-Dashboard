package dev.cj.weather_dashboard.exception;

/**
 * Custom exception for weather API related errors.
 * Wraps underlying API communication failures, timeout errors, and data parsing issues.
 */
public class WeatherApiException extends RuntimeException {

    /**
     * Constructs a new WeatherApiException with the specified detail message.
     *
     * @param message the detail message
     */
    public WeatherApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new WeatherApiException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
