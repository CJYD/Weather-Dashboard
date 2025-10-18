package dev.cj.weather_dashboard.controller;

import dev.cj.weather_dashboard.model.domain.Forecast;
import dev.cj.weather_dashboard.model.domain.TemperatureUnit;
import dev.cj.weather_dashboard.model.domain.Weather;
import dev.cj.weather_dashboard.service.UnitConversionService;
import dev.cj.weather_dashboard.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Web controller for rendering dashboard views.
 * Serves Thymeleaf templates with weather data.
 *
 * Endpoints:
 * - GET / - Main dashboard page
 * - GET /weather - Weather search page
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final WeatherService weatherService;
    private final UnitConversionService conversionService;

    /**
     * Renders the main dashboard page.
     *
     * @param model Spring MVC model
     * @return template name
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        log.info("Rendering dashboard home page");

        // Default cities for quick access
        List<String> defaultCities = List.of("London", "New York", "Tokyo", "Paris", "Sydney");
        model.addAttribute("defaultCities", defaultCities);
        model.addAttribute("temperatureUnits", TemperatureUnit.values());

        return "dashboard";
    }

    /**
     * Searches and displays weather for a specific city.
     *
     * @param city city name (required)
     * @param unit temperature unit (optional, defaults to CELSIUS)
     * @param model Spring MVC model
     * @return template name
     */
    @GetMapping("/weather")
    public String getWeather(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "CELSIUS") TemperatureUnit unit,
            Model model) {

        if (city == null || city.isBlank()) {
            log.info("No city provided, redirecting to dashboard");
            return "redirect:/";
        }

        log.info("Web: Getting weather for city: {} in {}", city, unit);

        try {
            // Fetch current weather
            Weather weather = weatherService.getCurrentWeather(city);

            // Fetch daily forecast
            List<Forecast> forecasts = weatherService.getDailyForecast(city);

            // Add data to model
            model.addAttribute("weather", weather);
            model.addAttribute("forecasts", forecasts);
            model.addAttribute("selectedUnit", unit);
            model.addAttribute("searchedCity", city);
            model.addAttribute("temperatureUnits", TemperatureUnit.values());
            model.addAttribute("conversionService", conversionService);

            // Add default cities for quick navigation
            List<String> defaultCities = List.of("London", "New York", "Tokyo", "Paris", "Sydney");
            model.addAttribute("defaultCities", defaultCities);

            return "dashboard";

        } catch (Exception e) {
            log.error("Error fetching weather for city: {}", city, e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("searchedCity", city);
            return "error";
        }
    }
}
