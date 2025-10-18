package dev.cj.weather_dashboard.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO for OpenWeatherMap 5-day forecast API response.
 * Maps JSON response to Java objects.
 *
 * API endpoint: https://api.openweathermap.org/data/2.5/forecast
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {

    @JsonProperty("cod")
    private String code;

    @JsonProperty("list")
    private List<ForecastItem> list;

    @JsonProperty("city")
    private City city;

    /**
     * Individual forecast item (3-hour interval)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        @JsonProperty("dt")
        private Long timestamp;

        @JsonProperty("main")
        private MainData main;

        @JsonProperty("weather")
        private List<WeatherResponse.WeatherCondition> weather;

        @JsonProperty("clouds")
        private WeatherResponse.Clouds clouds;

        @JsonProperty("wind")
        private WeatherResponse.Wind wind;

        @JsonProperty("pop")
        private Double pop;  // Probability of precipitation

        @JsonProperty("dt_txt")
        private String dateTimeText;
    }

    /**
     * Main forecast data
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        @JsonProperty("temp")
        private Double temperature;

        @JsonProperty("temp_min")
        private Double tempMin;

        @JsonProperty("temp_max")
        private Double tempMax;

        @JsonProperty("humidity")
        private Integer humidity;
    }

    /**
     * City information
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        @JsonProperty("name")
        private String name;

        @JsonProperty("country")
        private String country;

        @JsonProperty("coord")
        private WeatherResponse.Coordinates coordinates;
    }
}
