package dev.cj.weather_dashboard.controller;

import dev.cj.weather_dashboard.model.domain.Forecast;
import dev.cj.weather_dashboard.model.domain.TemperatureUnit;
import dev.cj.weather_dashboard.model.domain.Weather;
import dev.cj.weather_dashboard.model.dto.WeatherRequest;
import dev.cj.weather_dashboard.service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for weather data.
 * Provides JSON endpoints for programmatic access.
 *
 * Endpoints:
 * - GET /api/weather/current?city={city} - Get current weather
 * - GET /api/weather/forecast?city={city} - Get 5-day forecast
 * - POST /api/weather/search - Search weather with detailed parameters
 */
@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Gets current weather for a city (simple GET endpoint).
     *
     * @param city city name (required)
     * @param unit temperature unit (optional, defaults to Celsius)
     * @return current weather data
     *
     * Example: GET /api/weather/current?city=London&unit=CELSIUS
     */
    @GetMapping("/current")
    public ResponseEntity<Weather> getCurrentWeather(
            @RequestParam String city,
            @RequestParam(defaultValue = "CELSIUS") TemperatureUnit unit) {

        log.info("REST API: Getting current weather for city: {} in {}", city, unit);

        WeatherRequest request = WeatherRequest.builder()
                .city(city)
                .unit(unit)
                .build();

        Weather weather = weatherService.getCurrentWeather(request);
        return ResponseEntity.ok(weather);
    }

    /**
     * Gets 5-day forecast for a city.
     *
     * @param city city name (required)
     * @return list of forecast data
     *
     * Example: GET /api/weather/forecast?city=London
     */
    @GetMapping("/forecast")
    public ResponseEntity<List<Forecast>> getForecast(@RequestParam String city) {
        log.info("REST API: Getting forecast for city: {}", city);

        List<Forecast> forecasts = weatherService.getForecast(city);
        return ResponseEntity.ok(forecasts);
    }

    /**
     * Gets daily forecast summary (one per day).
     *
     * @param city city name (required)
     * @return list of daily forecasts (max 5 days)
     *
     * Example: GET /api/weather/daily?city=London
     */
    @GetMapping("/daily")
    public ResponseEntity<List<Forecast>> getDailyForecast(@RequestParam String city) {
        log.info("REST API: Getting daily forecast for city: {}", city);

        List<Forecast> forecasts = weatherService.getDailyForecast(city);
        return ResponseEntity.ok(forecasts);
    }

    /**
     * Search weather with detailed parameters (POST endpoint).
     * Allows for more complex queries with validation.
     *
     * @param request weather request object
     * @return current weather data
     *
     * Example: POST /api/weather/search
     * Body: { "city": "London", "countryCode": "GB", "unit": "CELSIUS" }
     */
    @PostMapping("/search")
    public ResponseEntity<Weather> searchWeather(@Valid @RequestBody WeatherRequest request) {
        log.info("REST API: Searching weather for: {}", request.getCityQuery());

        Weather weather = weatherService.getCurrentWeather(request);
        return ResponseEntity.ok(weather);
    }
}
