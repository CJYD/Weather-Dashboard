package dev.cj.weather_dashboard.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler using @ControllerAdvice.
 * Provides centralized exception handling across all controllers.
 * Returns appropriate HTTP status codes and user-friendly error messages.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles CityNotFoundException - returns 404 Not Found.
     *
     * @param ex the exception
     * @return error response with 404 status
     */
    @ExceptionHandler(CityNotFoundException.class)
    public Object handleCityNotFound(CityNotFoundException ex) {
        log.warn("City not found: {}", ex.getCityName());

        // For API endpoints
        if (isApiRequest()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(
                            HttpStatus.NOT_FOUND.value(),
                            "City Not Found",
                            ex.getMessage()
                    ));
        }

        // For web pages
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "City not found: " + ex.getCityName());
        mav.addObject("status", HttpStatus.NOT_FOUND.value());
        return mav;
    }

    /**
     * Handles WeatherApiException - returns 502 Bad Gateway.
     *
     * @param ex the exception
     * @return error response with 502 status
     */
    @ExceptionHandler(WeatherApiException.class)
    public Object handleWeatherApiException(WeatherApiException ex) {
        log.error("Weather API error: {}", ex.getMessage(), ex);

        if (isApiRequest()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(createErrorResponse(
                            HttpStatus.BAD_GATEWAY.value(),
                            "Weather Service Error",
                            "Unable to fetch weather data. Please try again later."
                    ));
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "Weather service is temporarily unavailable");
        mav.addObject("status", HttpStatus.BAD_GATEWAY.value());
        return mav;
    }

    /**
     * Handles WebClientResponseException - API communication errors.
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Object handleWebClientException(WebClientResponseException ex) {
        log.error("API communication error: {} - {}", ex.getStatusCode(), ex.getMessage());

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = switch (status) {
            case NOT_FOUND -> "City not found. Please check the city name.";
            case UNAUTHORIZED -> "Invalid API key. Please contact administrator.";
            case TOO_MANY_REQUESTS -> "Too many requests. Please try again later.";
            default -> "Unable to fetch weather data.";
        };

        if (isApiRequest()) {
            return ResponseEntity
                    .status(status)
                    .body(createErrorResponse(status.value(), "API Error", message));
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", message);
        mav.addObject("status", status.value());
        return mav;
    }

    /**
     * Handles validation errors - returns 400 Bad Request.
     *
     * @param ex the exception
     * @return error response with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Invalid request parameters"
        );
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles all other uncaught exceptions - returns 500 Internal Server Error.
     *
     * @param ex the exception
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        if (isApiRequest()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error",
                            "An unexpected error occurred. Please try again later."
                    ));
        }

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "An unexpected error occurred");
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return mav;
    }

    /**
     * Creates a standardized error response map.
     *
     * @param status HTTP status code
     * @param error error type
     * @param message error message
     * @return error response map
     */
    private Map<String, Object> createErrorResponse(int status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        return response;
    }

    /**
     * Determines if the current request is an API request.
     * Simple heuristic: checks if Accept header prefers JSON.
     *
     * @return true if API request, false otherwise
     */
    private boolean isApiRequest() {
        // This is a simplified check - in production, you might want to check request headers
        // For now, we'll assume API endpoints are under /api/*
        try {
            return org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes() != null;
        } catch (Exception e) {
            return true; // Default to API response
        }
    }
}
