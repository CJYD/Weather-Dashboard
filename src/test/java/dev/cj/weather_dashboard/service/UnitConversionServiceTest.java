package dev.cj.weather_dashboard.service;

import dev.cj.weather_dashboard.model.domain.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UnitConversionService.
 * Tests temperature conversion logic and formatting.
 */
@DisplayName("Unit Conversion Service Tests")
class UnitConversionServiceTest {

    private UnitConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new UnitConversionService();
    }

    @Test
    @DisplayName("Should convert Celsius to Fahrenheit correctly")
    void testCelsiusToFahrenheit() {
        double celsius = 0.0;
        double fahrenheit = conversionService.convert(celsius, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT);
        assertEquals(32.0, fahrenheit, 0.01, "0°C should equal 32°F");

        celsius = 100.0;
        fahrenheit = conversionService.convert(celsius, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, fahrenheit, 0.01, "100°C should equal 212°F");

        celsius = -40.0;
        fahrenheit = conversionService.convert(celsius, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT);
        assertEquals(-40.0, fahrenheit, 0.01, "-40°C should equal -40°F");
    }

    @Test
    @DisplayName("Should convert Fahrenheit to Celsius correctly")
    void testFahrenheitToCelsius() {
        double fahrenheit = 32.0;
        double celsius = conversionService.convert(fahrenheit, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS);
        assertEquals(0.0, celsius, 0.01, "32°F should equal 0°C");

        fahrenheit = 212.0;
        celsius = conversionService.convert(fahrenheit, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS);
        assertEquals(100.0, celsius, 0.01, "212°F should equal 100°C");
    }

    @Test
    @DisplayName("Should convert Kelvin to Celsius correctly")
    void testKelvinToCelsius() {
        double kelvin = 273.15;
        double celsius = conversionService.convert(kelvin, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS);
        assertEquals(0.0, celsius, 0.01, "273.15K should equal 0°C");

        kelvin = 373.15;
        celsius = conversionService.convert(kelvin, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS);
        assertEquals(100.0, celsius, 0.01, "373.15K should equal 100°C");
    }

    @Test
    @DisplayName("Should return same value when converting to same unit")
    void testSameUnitConversion() {
        double temp = 25.0;
        assertEquals(temp, conversionService.convert(temp, TemperatureUnit.CELSIUS, TemperatureUnit.CELSIUS));
        assertEquals(temp, conversionService.convert(temp, TemperatureUnit.FAHRENHEIT, TemperatureUnit.FAHRENHEIT));
        assertEquals(temp, conversionService.convert(temp, TemperatureUnit.KELVIN, TemperatureUnit.KELVIN));
    }

    @Test
    @DisplayName("Should format temperature with correct decimals")
    void testTemperatureFormatting() {
        String formatted = conversionService.format(20.5, TemperatureUnit.CELSIUS, 1);
        assertEquals("20.5°C", formatted);

        formatted = conversionService.format(20.567, TemperatureUnit.CELSIUS, 2);
        assertEquals("20.57°C", formatted);

        formatted = conversionService.format(20.0, TemperatureUnit.FAHRENHEIT, 0);
        assertEquals("20°F", formatted);
    }

    @Test
    @DisplayName("Should format temperature with default 1 decimal")
    void testDefaultFormatting() {
        String formatted = conversionService.format(25.678, TemperatureUnit.CELSIUS);
        assertEquals("25.7°C", formatted);
    }

    @Test
    @DisplayName("Should convert wind speed from m/s to mph")
    void testWindSpeedToMph() {
        double metersPerSecond = 10.0;
        double mph = conversionService.convertWindSpeed(metersPerSecond, "mph");
        assertEquals(22.37, mph, 0.01, "10 m/s should equal ~22.37 mph");
    }

    @Test
    @DisplayName("Should convert wind speed from m/s to km/h")
    void testWindSpeedToKmh() {
        double metersPerSecond = 10.0;
        double kmh = conversionService.convertWindSpeed(metersPerSecond, "kmh");
        assertEquals(36.0, kmh, 0.01, "10 m/s should equal 36 km/h");
    }

    @Test
    @DisplayName("Should return same value for m/s wind speed")
    void testWindSpeedMs() {
        double metersPerSecond = 10.0;
        double result = conversionService.convertWindSpeed(metersPerSecond, "ms");
        assertEquals(metersPerSecond, result, "Should return same value for m/s");
    }

    @Test
    @DisplayName("Should format wind speed correctly")
    void testWindSpeedFormatting() {
        String formatted = conversionService.formatWindSpeed(10.0, "mph");
        assertEquals("22.4 mph", formatted);

        formatted = conversionService.formatWindSpeed(10.0, "kmh");
        assertEquals("36.0 km/h", formatted);

        formatted = conversionService.formatWindSpeed(10.0, "ms");
        assertEquals("10.0 m/s", formatted);
    }
}
