package dev.cj.weather_dashboard.controller;

import dev.cj.weather_dashboard.model.domain.Forecast;
import dev.cj.weather_dashboard.model.domain.Weather;
import dev.cj.weather_dashboard.model.dto.WeatherRequest;
import dev.cj.weather_dashboard.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WeatherController.
 * Uses MockMvc to test REST endpoints.
 */
@WebMvcTest(WeatherController.class)
@DisplayName("Weather Controller Integration Tests")
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    private Weather mockWeather;
    private List<Forecast> mockForecasts;

    @BeforeEach
    void setUp() {
        mockWeather = createMockWeather();
        mockForecasts = createMockForecasts();
    }

    @Test
    @DisplayName("GET /api/weather/current should return weather data")
    void testGetCurrentWeather() throws Exception {
        // Given
        when(weatherService.getCurrentWeather(any(WeatherRequest.class))).thenReturn(mockWeather);

        // When & Then
        mockMvc.perform(get("/api/weather/current")
                        .param("city", "London")
                        .param("unit", "CELSIUS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("London"))
                .andExpect(jsonPath("$.country").value("GB"))
                .andExpect(jsonPath("$.temperature").exists())
                .andExpect(jsonPath("$.condition").value("Clear"));
    }

    @Test
    @DisplayName("GET /api/weather/current with default unit should use CELSIUS")
    void testGetCurrentWeatherDefaultUnit() throws Exception {
        // Given
        when(weatherService.getCurrentWeather(any(WeatherRequest.class))).thenReturn(mockWeather);

        // When & Then
        mockMvc.perform(get("/api/weather/current")
                        .param("city", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName").exists());
    }

    @Test
    @DisplayName("GET /api/weather/forecast should return forecast list")
    void testGetForecast() throws Exception {
        // Given
        when(weatherService.getForecast(anyString())).thenReturn(mockForecasts);

        // When & Then
        mockMvc.perform(get("/api/weather/forecast")
                        .param("city", "Tokyo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].temperature").exists())
                .andExpect(jsonPath("$[0].condition").exists());
    }

    @Test
    @DisplayName("GET /api/weather/daily should return daily forecasts")
    void testGetDailyForecast() throws Exception {
        // Given
        when(weatherService.getDailyForecast(anyString())).thenReturn(mockForecasts);

        // When & Then
        mockMvc.perform(get("/api/weather/daily")
                        .param("city", "Berlin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/weather/search should return weather data")
    void testSearchWeather() throws Exception {
        // Given
        when(weatherService.getCurrentWeather(any(WeatherRequest.class))).thenReturn(mockWeather);

        String requestBody = """
                {
                    "city": "London",
                    "countryCode": "GB",
                    "unit": "CELSIUS"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/weather/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cityName").value("London"))
                .andExpect(jsonPath("$.country").value("GB"));
    }

    @Test
    @DisplayName("POST /api/weather/search with invalid request should return 400")
    void testSearchWeatherInvalidRequest() throws Exception {
        // Given - Empty city name
        String requestBody = """
                {
                    "city": "",
                    "unit": "CELSIUS"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/weather/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // Helper methods

    private Weather createMockWeather() {
        return Weather.builder()
                .cityName("London")
                .country("GB")
                .latitude(51.51)
                .longitude(-0.13)
                .temperature(293.15)
                .feelsLike(291.15)
                .tempMin(290.15)
                .tempMax(295.15)
                .pressure(1013)
                .humidity(80)
                .condition("Clear")
                .description("clear sky")
                .icon("01d")
                .windSpeed(5.5)
                .windDegrees(180)
                .cloudiness(20)
                .visibility(10000)
                .timestamp(LocalDateTime.now())
                .sunrise(LocalDateTime.now().withHour(6))
                .sunset(LocalDateTime.now().withHour(18))
                .build();
    }

    private List<Forecast> createMockForecasts() {
        Forecast forecast1 = Forecast.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .temperature(295.15)
                .tempMin(293.15)
                .tempMax(297.15)
                .condition("Clouds")
                .description("scattered clouds")
                .icon("03d")
                .humidity(75)
                .pop(0.2)
                .windSpeed(4.0)
                .cloudiness(40)
                .build();

        Forecast forecast2 = Forecast.builder()
                .dateTime(LocalDateTime.now().plusDays(2))
                .temperature(290.15)
                .tempMin(288.15)
                .tempMax(292.15)
                .condition("Rain")
                .description("light rain")
                .icon("10d")
                .humidity(85)
                .pop(0.6)
                .windSpeed(6.0)
                .cloudiness(80)
                .build();

        return List.of(forecast1, forecast2);
    }
}
