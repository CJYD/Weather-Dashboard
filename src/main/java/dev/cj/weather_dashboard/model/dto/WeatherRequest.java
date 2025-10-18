package dev.cj.weather_dashboard.model.dto;

import dev.cj.weather_dashboard.model.domain.TemperatureUnit;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for weather queries.
 * Uses Builder pattern for flexible request construction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {

    /**
     * City name (required)
     * Example: "London", "New York", "Tokyo"
     */
    @NotBlank(message = "City name is required")
    private String city;

    /**
     * Country code (optional, improves accuracy)
     * Example: "GB", "US", "JP"
     * Format: ISO 3166 country code
     */
    private String countryCode;

    /**
     * Preferred temperature unit (optional, defaults to Celsius)
     */
    @Builder.Default
    private TemperatureUnit unit = TemperatureUnit.CELSIUS;

    /**
     * Whether to include forecast data (optional)
     */
    @Builder.Default
    private boolean includeForecast = false;

    /**
     * Constructs the query parameter for city search.
     * Format: "city,countryCode" or just "city"
     *
     * @return formatted city query string
     */
    public String getCityQuery() {
        if (countryCode != null && !countryCode.isBlank()) {
            return city + "," + countryCode;
        }
        return city;
    }
}
