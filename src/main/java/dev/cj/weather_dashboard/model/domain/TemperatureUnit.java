package dev.cj.weather_dashboard.model.domain;

/**
 * Temperature unit enumeration for unit conversion.
 * Supports Celsius, Fahrenheit, and Kelvin temperature scales.
 */
public enum TemperatureUnit {
    CELSIUS("°C", "metric"),
    FAHRENHEIT("°F", "imperial"),
    KELVIN("K", "standard");

    private final String symbol;
    private final String apiUnit;

    TemperatureUnit(String symbol, String apiUnit) {
        this.symbol = symbol;
        this.apiUnit = apiUnit;
    }

    /**
     * Gets the display symbol for this temperature unit.
     * @return temperature symbol (°C, °F, or K)
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the OpenWeatherMap API units parameter value.
     * @return API unit parameter (metric, imperial, or standard)
     */
    public String getApiUnit() {
        return apiUnit;
    }

    /**
     * Converts a temperature value from Kelvin to this unit.
     *
     * @param kelvin temperature in Kelvin
     * @return converted temperature value
     */
    public double fromKelvin(double kelvin) {
        return switch (this) {
            case CELSIUS -> kelvin - 273.15;
            case FAHRENHEIT -> (kelvin - 273.15) * 9/5 + 32;
            case KELVIN -> kelvin;
        };
    }

    /**
     * Converts a temperature value from this unit to Kelvin.
     *
     * @param value temperature in current unit
     * @return temperature in Kelvin
     */
    public double toKelvin(double value) {
        return switch (this) {
            case CELSIUS -> value + 273.15;
            case FAHRENHEIT -> (value - 32) * 5/9 + 273.15;
            case KELVIN -> value;
        };
    }
}
