package dev.cj.weather_dashboard.service;

import dev.cj.weather_dashboard.model.domain.TemperatureUnit;
import org.springframework.stereotype.Service;

/**
 * Service for temperature unit conversions.
 * Implements Strategy pattern for flexible unit conversion.
 *
 * Supports:
 * - Celsius
 * - Fahrenheit
 * - Kelvin
 */
@Service
public class UnitConversionService {

    /**
     * Converts temperature from one unit to another.
     *
     * @param value temperature value
     * @param from source unit
     * @param to target unit
     * @return converted temperature value
     */
    public double convert(double value, TemperatureUnit from, TemperatureUnit to) {
        if (from == to) {
            return value;
        }

        // Convert to Kelvin first (common base)
        double kelvin = from.toKelvin(value);

        // Convert from Kelvin to target unit
        return to.fromKelvin(kelvin);
    }

    /**
     * Formats temperature with unit symbol.
     *
     * @param value temperature value
     * @param unit temperature unit
     * @param decimals number of decimal places
     * @return formatted temperature string (e.g., "20.5°C")
     */
    public String format(double value, TemperatureUnit unit, int decimals) {
        String format = "%." + decimals + "f%s";
        return String.format(format, value, unit.getSymbol());
    }

    /**
     * Formats temperature with unit symbol (1 decimal place).
     *
     * @param value temperature value
     * @param unit temperature unit
     * @return formatted temperature string
     */
    public String format(double value, TemperatureUnit unit) {
        return format(value, unit, 1);
    }

    /**
     * Converts wind speed from m/s to different units.
     *
     * @param metersPerSecond wind speed in m/s
     * @param unit target unit ("mph", "kmh", or "ms")
     * @return converted wind speed
     */
    public double convertWindSpeed(double metersPerSecond, String unit) {
        return switch (unit.toLowerCase()) {
            case "mph" -> metersPerSecond * 2.23694;  // Miles per hour
            case "kmh", "kph" -> metersPerSecond * 3.6;  // Kilometers per hour
            default -> metersPerSecond;  // Meters per second (default)
        };
    }

    /**
     * Formats wind speed with unit.
     *
     * @param metersPerSecond wind speed in m/s
     * @param unit target unit
     * @return formatted wind speed string
     */
    public String formatWindSpeed(double metersPerSecond, String unit) {
        double converted = convertWindSpeed(metersPerSecond, unit);
        String unitSymbol = switch (unit.toLowerCase()) {
            case "mph" -> " mph";
            case "kmh", "kph" -> " km/h";
            default -> " m/s";
        };
        return String.format("%.1f%s", converted, unitSymbol);
    }
}
