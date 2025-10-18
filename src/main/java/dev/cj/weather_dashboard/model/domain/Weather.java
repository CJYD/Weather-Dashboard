package dev.cj.weather_dashboard.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing current weather data for a location.
 * This is the internal representation used by the service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {

    /**
     * City name
     */
    private String cityName;

    /**
     * Country code (ISO 3166)
     */
    private String country;

    /**
     * Geographic coordinates
     */
    private double latitude;
    private double longitude;

    /**
     * Current temperature in Kelvin
     */
    private double temperature;

    /**
     * Feels-like temperature in Kelvin
     */
    private double feelsLike;

    /**
     * Minimum temperature in Kelvin
     */
    private double tempMin;

    /**
     * Maximum temperature in Kelvin
     */
    private double tempMax;

    /**
     * Atmospheric pressure in hPa
     */
    private int pressure;

    /**
     * Humidity percentage
     */
    private int humidity;

    /**
     * Weather condition (e.g., "Clear", "Rain", "Clouds")
     */
    private String condition;

    /**
     * Detailed weather description
     */
    private String description;

    /**
     * Weather icon code (e.g., "01d", "10n")
     */
    private String icon;

    /**
     * Wind speed in m/s
     */
    private double windSpeed;

    /**
     * Wind direction in degrees
     */
    private int windDegrees;

    /**
     * Cloudiness percentage
     */
    private int cloudiness;

    /**
     * Visibility in meters
     */
    private int visibility;

    /**
     * Data calculation timestamp
     */
    private LocalDateTime timestamp;

    /**
     * Sunrise time
     */
    private LocalDateTime sunrise;

    /**
     * Sunset time
     */
    private LocalDateTime sunset;

    /**
     * Converts temperature to specified unit.
     *
     * @param unit target temperature unit
     * @return temperature in the specified unit
     */
    public double getTemperatureInUnit(TemperatureUnit unit) {
        return unit.fromKelvin(temperature);
    }

    /**
     * Converts feels-like temperature to specified unit.
     *
     * @param unit target temperature unit
     * @return feels-like temperature in the specified unit
     */
    public double getFeelsLikeInUnit(TemperatureUnit unit) {
        return unit.fromKelvin(feelsLike);
    }

    /**
     * Gets wind direction as cardinal direction (N, NE, E, etc.)
     *
     * @return cardinal direction string
     */
    public String getWindDirectionCardinal() {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(((windDegrees % 360) / 45.0)) % 8;
        return directions[index];
    }
}
