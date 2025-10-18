package dev.cj.weather_dashboard.client;

import dev.cj.weather_dashboard.config.WeatherProperties;
import dev.cj.weather_dashboard.exception.CityNotFoundException;
import dev.cj.weather_dashboard.exception.WeatherApiException;
import dev.cj.weather_dashboard.model.dto.ForecastResponse;
import dev.cj.weather_dashboard.model.dto.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client for OpenWeatherMap API integration.
 * Implements Adapter pattern to convert external API to internal domain.
 *
 * Features:
 * - Circuit Breaker pattern for fault tolerance
 * - Retry mechanism with exponential backoff
 * - Comprehensive error handling
 * - Reactive programming with WebFlux
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherMapClient {

    private final WebClient weatherWebClient;
    private final WeatherProperties weatherProperties;

    private static final String CURRENT_WEATHER_ENDPOINT = "/weather";
    private static final String FORECAST_ENDPOINT = "/forecast";

    /**
     * Fetches current weather data for a city.
     * Uses Circuit Breaker and Retry patterns for resilience.
     *
     * @param city city name (e.g., "London" or "London,GB")
     * @return weather response from API
     * @throws CityNotFoundException if city not found
     * @throws WeatherApiException if API communication fails
     */
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getCurrentWeatherFallback")
    @Retry(name = "weatherApi")
    public WeatherResponse getCurrentWeather(String city) {
        log.debug("Fetching current weather for city: {}", city);

        try {
            WeatherResponse response = weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(CURRENT_WEATHER_ENDPOINT)
                            .queryParam("q", city)
                            .queryParam("appid", weatherProperties.getKey())
                            .queryParam("units", "standard")  // Always fetch in Kelvin
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return Mono.error(new CityNotFoundException(city));
                        }
                        return Mono.error(new WeatherApiException(
                                "API client error: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            Mono.error(new WeatherApiException(
                                    "API server error: " + clientResponse.statusCode())))
                    .bodyToMono(WeatherResponse.class)
                    .block();

            log.info("Successfully fetched weather for city: {}", city);
            return response;

        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching weather for city: {}", city, e);
            throw new WeatherApiException("Failed to fetch weather data for " + city, e);
        }
    }

    /**
     * Fetches 5-day forecast data for a city.
     * Returns forecasts in 3-hour intervals.
     *
     * @param city city name
     * @return forecast response from API
     * @throws CityNotFoundException if city not found
     * @throws WeatherApiException if API communication fails
     */
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getForecastFallback")
    @Retry(name = "weatherApi")
    public ForecastResponse getForecast(String city) {
        log.debug("Fetching forecast for city: {}", city);

        try {
            ForecastResponse response = weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(FORECAST_ENDPOINT)
                            .queryParam("q", city)
                            .queryParam("appid", weatherProperties.getKey())
                            .queryParam("units", "standard")
                            .queryParam("cnt", "40")  // 5 days * 8 (3-hour intervals)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return Mono.error(new CityNotFoundException(city));
                        }
                        return Mono.error(new WeatherApiException(
                                "API client error: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            Mono.error(new WeatherApiException(
                                    "API server error: " + clientResponse.statusCode())))
                    .bodyToMono(ForecastResponse.class)
                    .block();

            log.info("Successfully fetched forecast for city: {}", city);
            return response;

        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching forecast for city: {}", city, e);
            throw new WeatherApiException("Failed to fetch forecast data for " + city, e);
        }
    }

    /**
     * Fallback method for getCurrentWeather when circuit is open.
     * Provides graceful degradation.
     *
     * @param city city name
     * @param throwable the exception that triggered the fallback
     * @return null (service layer will handle gracefully)
     */
    @SuppressWarnings("unused")
    private WeatherResponse getCurrentWeatherFallback(String city, Throwable throwable) {
        log.warn("Circuit breaker activated for getCurrentWeather: {}", city);
        log.warn("Fallback reason: {}", throwable.getMessage());
        throw new WeatherApiException(
                "Weather service is temporarily unavailable. Please try again later.", throwable);
    }

    /**
     * Fallback method for getForecast when circuit is open.
     *
     * @param city city name
     * @param throwable the exception that triggered the fallback
     * @return null
     */
    @SuppressWarnings("unused")
    private ForecastResponse getForecastFallback(String city, Throwable throwable) {
        log.warn("Circuit breaker activated for getForecast: {}", city);
        log.warn("Fallback reason: {}", throwable.getMessage());
        throw new WeatherApiException(
                "Forecast service is temporarily unavailable. Please try again later.", throwable);
    }
}
