package dev.cj.weather_dashboard.client;

import dev.cj.weather_dashboard.config.WeatherProperties;
import dev.cj.weather_dashboard.exception.CityNotFoundException;
import dev.cj.weather_dashboard.exception.WeatherApiException;
import dev.cj.weather_dashboard.model.dto.ForecastResponse;
import dev.cj.weather_dashboard.model.dto.WeatherResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenWeatherMapClient using MockWebServer.
 * Tests API integration with mocked HTTP responses.
 */
@DisplayName("OpenWeatherMap Client Tests")
class OpenWeatherMapClientTest {

    private MockWebServer mockWebServer;
    private OpenWeatherMapClient weatherClient;
    private WeatherProperties weatherProperties;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        weatherProperties = new WeatherProperties();
        weatherProperties.setKey("test-api-key");
        weatherProperties.setBaseUrl(mockWebServer.url("/").toString());
        weatherProperties.setTimeout(5000);

        WebClient webClient = WebClient.builder()
                .baseUrl(weatherProperties.getBaseUrl())
                .build();

        weatherClient = new OpenWeatherMapClient(webClient, weatherProperties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should fetch current weather successfully")
    void testGetCurrentWeatherSuccess() throws InterruptedException {
        // Given
        String mockResponse = """
                {
                    "coord": {"lon": -0.13, "lat": 51.51},
                    "weather": [{"id": 800, "main": "Clear", "description": "clear sky", "icon": "01d"}],
                    "main": {"temp": 293.15, "feels_like": 291.15, "temp_min": 290.15, "temp_max": 295.15, "pressure": 1013, "humidity": 80},
                    "wind": {"speed": 5.5, "deg": 180},
                    "clouds": {"all": 20},
                    "visibility": 10000,
                    "dt": 1609459200,
                    "sys": {"country": "GB", "sunrise": 1609459200, "sunset": 1609502400},
                    "name": "London",
                    "cod": 200
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        // When
        WeatherResponse response = weatherClient.getCurrentWeather("London");

        // Then
        assertNotNull(response);
        assertEquals("London", response.getCityName());
        assertEquals(200, response.getCode());
        assertEquals("GB", response.getSys().getCountry());
        assertEquals(293.15, response.getMain().getTemperature());

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request);
        assertEquals("GET", request.getMethod());
        assertNotNull(request.getPath());
        assertTrue(request.getPath().contains("q=London"));
        assertTrue(request.getPath().contains("appid=test-api-key"));
    }

    @Test
    @DisplayName("Should throw CityNotFoundException for 404 response")
    void testGetCurrentWeatherCityNotFound() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"cod\":\"404\",\"message\":\"city not found\"}"));

        // When & Then
        CityNotFoundException exception = assertThrows(CityNotFoundException.class, () ->
                weatherClient.getCurrentWeather("InvalidCity"));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw WeatherApiException for 500 response")
    void testGetCurrentWeatherServerError() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        // When & Then
        WeatherApiException exception = assertThrows(WeatherApiException.class, () ->
                weatherClient.getCurrentWeather("London"));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should fetch forecast successfully")
    void testGetForecastSuccess() throws InterruptedException {
        // Given
        String mockResponse = """
                {
                    "cod": "200",
                    "list": [
                        {
                            "dt": 1609459200,
                            "main": {"temp": 295.15, "temp_min": 293.15, "temp_max": 297.15, "humidity": 75},
                            "weather": [{"main": "Clouds", "description": "scattered clouds", "icon": "03d"}],
                            "clouds": {"all": 40},
                            "wind": {"speed": 4.0, "deg": 90},
                            "pop": 0.2,
                            "dt_txt": "2021-01-01 00:00:00"
                        }
                    ],
                    "city": {"name": "London", "country": "GB"}
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        // When
        ForecastResponse response = weatherClient.getForecast("London");

        // Then
        assertNotNull(response);
        assertEquals("200", response.getCode());
        assertNotNull(response.getList());
        assertFalse(response.getList().isEmpty());

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertTrue(request.getPath().contains("/forecast"));
        assertTrue(request.getPath().contains("q=London"));
    }

    @Test
    @DisplayName("Should throw CityNotFoundException for forecast 404")
    void testGetForecastCityNotFound() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"cod\":\"404\",\"message\":\"city not found\"}"));

        // When & Then
        assertThrows(CityNotFoundException.class, () ->
                weatherClient.getForecast("InvalidCity"));
    }

    @Test
    @DisplayName("Should include correct query parameters")
    void testQueryParameters() throws InterruptedException {
        // Given
        String mockResponse = """
                {
                    "coord": {"lon": -0.13, "lat": 51.51},
                    "weather": [{"id": 800, "main": "Clear", "description": "clear sky", "icon": "01d"}],
                    "main": {"temp": 293.15, "feels_like": 291.15, "temp_min": 290.15, "temp_max": 295.15, "pressure": 1013, "humidity": 80},
                    "wind": {"speed": 5.5, "deg": 180},
                    "clouds": {"all": 20},
                    "visibility": 10000,
                    "dt": 1609459200,
                    "sys": {"country": "GB", "sunrise": 1609459200, "sunset": 1609502400},
                    "name": "London",
                    "cod": 200
                }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        // When
        weatherClient.getCurrentWeather("London,GB");

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        String path = request.getPath();
        assertTrue(path.contains("q=London,GB"));
        assertTrue(path.contains("appid=test-api-key"));
        assertTrue(path.contains("units=standard"));
    }
}
