package dev.cj.weather_dashboard.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO for OpenWeatherMap current weather API response.
 * Maps JSON response to Java objects using Jackson annotations.
 *
 * Example API response: https://api.openweathermap.org/data/2.5/weather?q=London
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    @JsonProperty("coord")
    private Coordinates coordinates;

    @JsonProperty("weather")
    private List<WeatherCondition> weather;

    @JsonProperty("main")
    private MainData main;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("clouds")
    private Clouds clouds;

    @JsonProperty("visibility")
    private Integer visibility;

    @JsonProperty("dt")
    private Long timestamp;

    @JsonProperty("sys")
    private SystemData sys;

    @JsonProperty("name")
    private String cityName;

    @JsonProperty("cod")
    private Integer code;

    /**
     * Geographic coordinates
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinates {
        @JsonProperty("lon")
        private Double longitude;

        @JsonProperty("lat")
        private Double latitude;
    }

    /**
     * Weather condition information
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherCondition {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;

        @JsonProperty("icon")
        private String icon;
    }

    /**
     * Main weather data (temperature, pressure, humidity)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        @JsonProperty("temp")
        private Double temperature;

        @JsonProperty("feels_like")
        private Double feelsLike;

        @JsonProperty("temp_min")
        private Double tempMin;

        @JsonProperty("temp_max")
        private Double tempMax;

        @JsonProperty("pressure")
        private Integer pressure;

        @JsonProperty("humidity")
        private Integer humidity;
    }

    /**
     * Wind data
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;

        @JsonProperty("deg")
        private Integer degrees;
    }

    /**
     * Cloud coverage
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        @JsonProperty("all")
        private Integer cloudiness;
    }

    /**
     * System data (country, sunrise, sunset)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SystemData {
        @JsonProperty("country")
        private String country;

        @JsonProperty("sunrise")
        private Long sunrise;

        @JsonProperty("sunset")
        private Long sunset;
    }
}
