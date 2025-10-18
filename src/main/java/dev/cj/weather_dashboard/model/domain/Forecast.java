package dev.cj.weather_dashboard.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing weather forecast data for a specific time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    /**
     * Forecast timestamp
     */
    private LocalDateTime dateTime;

    /**
     * Temperature in Kelvin
     */
    private double temperature;

    /**
     * Minimum temperature in Kelvin
     */
    private double tempMin;

    /**
     * Maximum temperature in Kelvin
     */
    private double tempMax;

    /**
     * Weather condition
     */
    private String condition;

    /**
     * Weather description
     */
    private String description;

    /**
     * Weather icon code
     */
    private String icon;

    /**
     * Humidity percentage
     */
    private int humidity;

    /**
     * Probability of precipitation (0-1)
     */
    private double pop;

    /**
     * Wind speed in m/s
     */
    private double windSpeed;

    /**
     * Cloudiness percentage
     */
    private int cloudiness;

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
     * Converts min temperature to specified unit.
     *
     * @param unit target temperature unit
     * @return min temperature in the specified unit
     */
    public double getTempMinInUnit(TemperatureUnit unit) {
        return unit.fromKelvin(tempMin);
    }

    /**
     * Converts max temperature to specified unit.
     *
     * @param unit target temperature unit
     * @return max temperature in the specified unit
     */
    public double getTempMaxInUnit(TemperatureUnit unit) {
        return unit.fromKelvin(tempMax);
    }
}
