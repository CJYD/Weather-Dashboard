package dev.cj.weather_dashboard.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for weather data.
 * Uses in-memory caching to reduce API calls and improve performance.
 *
 * Cache Strategy:
 * - weatherCache: 10-minute TTL for current weather data
 * - forecastCache: 30-minute TTL for forecast data
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a simple in-memory cache manager.
     * For production, consider using Caffeine or Redis for more advanced features.
     *
     * @return configured cache manager with predefined cache names
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("weatherCache", "forecastCache");
    }
}
