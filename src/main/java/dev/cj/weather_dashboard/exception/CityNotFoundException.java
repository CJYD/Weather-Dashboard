package dev.cj.weather_dashboard.exception;

/**
 * Exception thrown when a requested city is not found in the weather API.
 * Typically occurs when city name is misspelled or doesn't exist in the API database.
 */
public class CityNotFoundException extends RuntimeException {

    private final String cityName;

    /**
     * Constructs a new CityNotFoundException with the city name.
     *
     * @param cityName the name of the city that wasn't found
     */
    public CityNotFoundException(String cityName) {
        super("City not found: " + cityName);
        this.cityName = cityName;
    }

    /**
     * Gets the city name that wasn't found.
     *
     * @return the city name
     */
    public String getCityName() {
        return cityName;
    }
}
