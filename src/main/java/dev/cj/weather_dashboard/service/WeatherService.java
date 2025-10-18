package dev.cj.weather_dashboard.service;

import dev.cj.weather_dashboard.client.OpenWeatherMapClient;
import dev.cj.weather_dashboard.model.domain.Forecast;
import dev.cj.weather_dashboard.model.domain.Weather;
import dev.cj.weather_dashboard.model.dto.ForecastResponse;
import dev.cj.weather_dashboard.model.dto.WeatherRequest;
import dev.cj.weather_dashboard.model.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic service for weather operations.
 * Implements caching to minimize API calls and improve performance.
 *
 * Responsibilities:
 * - Coordinate API client calls
 * - Transform DTOs to domain objects
 * - Apply caching strategy
 * - Business logic validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final OpenWeatherMapClient weatherClient;

    /**
     * Gets current weather for a city.
     * Results are cached for 10 minutes to reduce API calls.
     *
     * @param request weather request with city and preferences
     * @return weather domain object
     */
    @Cacheable(value = "weatherCache", key = "#request.cityQuery")
    public Weather getCurrentWeather(WeatherRequest request) {
        log.info("Fetching current weather for: {}", request.getCityQuery());

        WeatherResponse response = weatherClient.getCurrentWeather(request.getCityQuery());

        return mapToWeatherDomain(response);
    }

    /**
     * Gets current weather by city name (simple method).
     *
     * @param city city name
     * @return weather domain object
     */
    public Weather getCurrentWeather(String city) {
        WeatherRequest request = WeatherRequest.builder()
                .city(city)
                .build();
        return getCurrentWeather(request);
    }

    /**
     * Gets 5-day weather forecast for a city.
     * Results are cached for 30 minutes.
     *
     * @param city city name
     * @return list of forecast objects
     */
    @Cacheable(value = "forecastCache", key = "#city")
    public List<Forecast> getForecast(String city) {
        log.info("Fetching forecast for: {}", city);

        ForecastResponse response = weatherClient.getForecast(city);

        return mapToForecastDomain(response);
    }

    /**
     * Gets daily forecast summary (one forecast per day at noon).
     *
     * @param city city name
     * @return list of daily forecasts (max 5 days)
     */
    public List<Forecast> getDailyForecast(String city) {
        List<Forecast> allForecasts = getForecast(city);

        // Group by date and take the forecast closest to noon (12:00) for each day
        return allForecasts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        f -> f.getDateTime().toLocalDate()
                ))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .limit(5)
                .map(entry -> {
                    // Find forecast closest to noon for this day
                    return entry.getValue().stream()
                            .min(java.util.Comparator.comparingInt(
                                    f -> Math.abs(f.getDateTime().getHour() - 12)
                            ))
                            .orElse(entry.getValue().get(0));
                })
                .toList();
    }

    /**
     * Maps WeatherResponse DTO to Weather domain object.
     *
     * @param response API response
     * @return weather domain object
     */
    private Weather mapToWeatherDomain(WeatherResponse response) {
        WeatherResponse.WeatherCondition condition = response.getWeather().get(0);

        return Weather.builder()
                .cityName(response.getCityName())
                .country(response.getSys().getCountry())
                .latitude(response.getCoordinates().getLatitude())
                .longitude(response.getCoordinates().getLongitude())
                .temperature(response.getMain().getTemperature())
                .feelsLike(response.getMain().getFeelsLike())
                .tempMin(response.getMain().getTempMin())
                .tempMax(response.getMain().getTempMax())
                .pressure(response.getMain().getPressure())
                .humidity(response.getMain().getHumidity())
                .condition(condition.getMain())
                .description(condition.getDescription())
                .icon(condition.getIcon())
                .windSpeed(response.getWind().getSpeed())
                .windDegrees(response.getWind().getDegrees())
                .cloudiness(response.getClouds().getCloudiness())
                .visibility(response.getVisibility())
                .timestamp(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(response.getTimestamp()),
                        ZoneId.systemDefault()))
                .sunrise(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(response.getSys().getSunrise()),
                        ZoneId.systemDefault()))
                .sunset(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(response.getSys().getSunset()),
                        ZoneId.systemDefault()))
                .build();
    }

    /**
     * Maps ForecastResponse DTO to list of Forecast domain objects.
     *
     * @param response API response
     * @return list of forecast domain objects
     */
    private List<Forecast> mapToForecastDomain(ForecastResponse response) {
        List<Forecast> forecasts = new ArrayList<>();

        for (ForecastResponse.ForecastItem item : response.getList()) {
            if (item.getWeather() == null || item.getWeather().isEmpty()) {
                continue;
            }

            WeatherResponse.WeatherCondition condition = item.getWeather().get(0);

            Forecast forecast = Forecast.builder()
                    .dateTime(LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(item.getTimestamp()),
                            ZoneId.systemDefault()))
                    .temperature(item.getMain().getTemperature())
                    .tempMin(item.getMain().getTempMin())
                    .tempMax(item.getMain().getTempMax())
                    .condition(condition.getMain())
                    .description(condition.getDescription())
                    .icon(condition.getIcon())
                    .humidity(item.getMain().getHumidity())
                    .pop(item.getPop() != null ? item.getPop() : 0.0)
                    .windSpeed(item.getWind().getSpeed())
                    .cloudiness(item.getClouds().getCloudiness())
                    .build();

            forecasts.add(forecast);
        }

        return forecasts;
    }
}
