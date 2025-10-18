package dev.cj.weather_dashboard.service;

import dev.cj.weather_dashboard.client.OpenWeatherMapClient;
import dev.cj.weather_dashboard.model.domain.Forecast;
import dev.cj.weather_dashboard.model.domain.Weather;
import dev.cj.weather_dashboard.model.dto.ForecastResponse;
import dev.cj.weather_dashboard.model.dto.WeatherRequest;
import dev.cj.weather_dashboard.model.dto.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WeatherService.
 * Uses Mockito to mock external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Weather Service Tests")
class WeatherServiceTest {

    @Mock
    private OpenWeatherMapClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    private WeatherResponse mockWeatherResponse;
    private ForecastResponse mockForecastResponse;

    @BeforeEach
    void setUp() {
        // Setup mock weather response
        mockWeatherResponse = createMockWeatherResponse();
        mockForecastResponse = createMockForecastResponse();
    }

    @Test
    @DisplayName("Should fetch current weather successfully")
    void testGetCurrentWeather() {
        // Given
        when(weatherClient.getCurrentWeather(anyString())).thenReturn(mockWeatherResponse);

        WeatherRequest request = WeatherRequest.builder()
                .city("London")
                .build();

        // When
        Weather weather = weatherService.getCurrentWeather(request);

        // Then
        assertNotNull(weather);
        assertEquals("London", weather.getCityName());
        assertEquals("GB", weather.getCountry());
        assertEquals(293.15, weather.getTemperature(), 0.01);
        assertEquals(291.15, weather.getFeelsLike(), 0.01);
        assertEquals(80, weather.getHumidity());
        assertEquals(1013, weather.getPressure());
        assertEquals("Clear", weather.getCondition());

        verify(weatherClient, times(1)).getCurrentWeather("London");
    }

    @Test
    @DisplayName("Should fetch current weather by city name")
    void testGetCurrentWeatherByCity() {
        // Given
        when(weatherClient.getCurrentWeather("Paris")).thenReturn(mockWeatherResponse);

        // When
        Weather weather = weatherService.getCurrentWeather("Paris");

        // Then
        assertNotNull(weather);
        assertEquals("London", weather.getCityName()); // Mock returns London
        verify(weatherClient, times(1)).getCurrentWeather("Paris");
    }

    @Test
    @DisplayName("Should fetch forecast successfully")
    void testGetForecast() {
        // Given
        when(weatherClient.getForecast("Tokyo")).thenReturn(mockForecastResponse);

        // When
        List<Forecast> forecasts = weatherService.getForecast("Tokyo");

        // Then
        assertNotNull(forecasts);
        assertFalse(forecasts.isEmpty());
        assertEquals(2, forecasts.size());

        Forecast first = forecasts.get(0);
        assertEquals(295.15, first.getTemperature(), 0.01);
        assertEquals("Clouds", first.getCondition());

        verify(weatherClient, times(1)).getForecast("Tokyo");
    }

    @Test
    @DisplayName("Should fetch daily forecast successfully")
    void testGetDailyForecast() {
        // Given
        when(weatherClient.getForecast("Berlin")).thenReturn(mockForecastResponse);

        // When
        List<Forecast> dailyForecasts = weatherService.getDailyForecast("Berlin");

        // Then
        assertNotNull(dailyForecasts);
        // Daily forecast filters to only noon forecasts
        verify(weatherClient, times(1)).getForecast("Berlin");
    }

    @Test
    @DisplayName("Should map weather response to domain correctly")
    void testWeatherMapping() {
        // Given
        when(weatherClient.getCurrentWeather("Madrid")).thenReturn(mockWeatherResponse);

        WeatherRequest request = WeatherRequest.builder()
                .city("Madrid")
                .build();

        // When
        Weather weather = weatherService.getCurrentWeather(request);

        // Then
        assertNotNull(weather.getTimestamp());
        assertNotNull(weather.getSunrise());
        assertNotNull(weather.getSunset());
        assertEquals(51.51, weather.getLatitude(), 0.01);
        assertEquals(-0.13, weather.getLongitude(), 0.01);
    }

    // Helper methods to create mock responses

    private WeatherResponse createMockWeatherResponse() {
        WeatherResponse response = new WeatherResponse();
        response.setCityName("London");
        response.setCode(200);
        response.setTimestamp(1609459200L); // 2021-01-01 00:00:00 UTC
        response.setVisibility(10000);

        // Coordinates
        WeatherResponse.Coordinates coord = new WeatherResponse.Coordinates();
        coord.setLatitude(51.51);
        coord.setLongitude(-0.13);
        response.setCoordinates(coord);

        // Weather condition
        WeatherResponse.WeatherCondition condition = new WeatherResponse.WeatherCondition();
        condition.setId(800);
        condition.setMain("Clear");
        condition.setDescription("clear sky");
        condition.setIcon("01d");
        response.setWeather(List.of(condition));

        // Main data
        WeatherResponse.MainData main = new WeatherResponse.MainData();
        main.setTemperature(293.15); // 20°C in Kelvin
        main.setFeelsLike(291.15); // 18°C in Kelvin
        main.setTempMin(290.15);
        main.setTempMax(295.15);
        main.setPressure(1013);
        main.setHumidity(80);
        response.setMain(main);

        // Wind
        WeatherResponse.Wind wind = new WeatherResponse.Wind();
        wind.setSpeed(5.5);
        wind.setDegrees(180);
        response.setWind(wind);

        // Clouds
        WeatherResponse.Clouds clouds = new WeatherResponse.Clouds();
        clouds.setCloudiness(20);
        response.setClouds(clouds);

        // System data
        WeatherResponse.SystemData sys = new WeatherResponse.SystemData();
        sys.setCountry("GB");
        sys.setSunrise(1609459200L);
        sys.setSunset(1609502400L);
        response.setSys(sys);

        return response;
    }

    private ForecastResponse createMockForecastResponse() {
        ForecastResponse response = new ForecastResponse();
        response.setCode("200");

        List<ForecastResponse.ForecastItem> items = new ArrayList<>();

        // First forecast item
        ForecastResponse.ForecastItem item1 = new ForecastResponse.ForecastItem();
        item1.setTimestamp(1609459200L);
        item1.setPop(0.2);
        item1.setDateTimeText("2021-01-01 00:00:00");

        ForecastResponse.MainData main1 = new ForecastResponse.MainData();
        main1.setTemperature(295.15);
        main1.setTempMin(293.15);
        main1.setTempMax(297.15);
        main1.setHumidity(75);
        item1.setMain(main1);

        WeatherResponse.WeatherCondition cond1 = new WeatherResponse.WeatherCondition();
        cond1.setMain("Clouds");
        cond1.setDescription("scattered clouds");
        cond1.setIcon("03d");
        item1.setWeather(List.of(cond1));

        WeatherResponse.Wind wind1 = new WeatherResponse.Wind();
        wind1.setSpeed(4.0);
        wind1.setDegrees(90);
        item1.setWind(wind1);

        WeatherResponse.Clouds clouds1 = new WeatherResponse.Clouds();
        clouds1.setCloudiness(40);
        item1.setClouds(clouds1);

        items.add(item1);

        // Second forecast item
        ForecastResponse.ForecastItem item2 = new ForecastResponse.ForecastItem();
        item2.setTimestamp(1609470000L);
        item2.setPop(0.0);
        item2.setDateTimeText("2021-01-01 03:00:00");

        ForecastResponse.MainData main2 = new ForecastResponse.MainData();
        main2.setTemperature(293.15);
        main2.setTempMin(291.15);
        main2.setTempMax(295.15);
        main2.setHumidity(80);
        item2.setMain(main2);

        WeatherResponse.WeatherCondition cond2 = new WeatherResponse.WeatherCondition();
        cond2.setMain("Clear");
        cond2.setDescription("clear sky");
        cond2.setIcon("01n");
        item2.setWeather(List.of(cond2));

        WeatherResponse.Wind wind2 = new WeatherResponse.Wind();
        wind2.setSpeed(3.5);
        wind2.setDegrees(120);
        item2.setWind(wind2);

        WeatherResponse.Clouds clouds2 = new WeatherResponse.Clouds();
        clouds2.setCloudiness(10);
        item2.setClouds(clouds2);

        items.add(item2);

        response.setList(items);

        // City info
        ForecastResponse.City city = new ForecastResponse.City();
        city.setName("London");
        city.setCountry("GB");
        response.setCity(city);

        return response;
    }
}
