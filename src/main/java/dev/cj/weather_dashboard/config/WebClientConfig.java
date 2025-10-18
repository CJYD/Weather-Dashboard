package dev.cj.weather_dashboard.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient configuration for reactive HTTP requests to OpenWeatherMap API.
 * Implements connection pooling, timeouts, and error handling.
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WeatherProperties weatherProperties;

    /**
     * Creates a configured WebClient for OpenWeatherMap API.
     *
     * Features:
     * - Connection timeout: 5 seconds
     * - Read/Write timeout: 5 seconds
     * - Base URL configuration
     * - Default headers for JSON responses
     *
     * @return configured WebClient instance
     */
    @Bean
    public WebClient weatherWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, weatherProperties.getTimeout())
                .responseTimeout(Duration.ofMillis(weatherProperties.getTimeout()))
                .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(weatherProperties.getTimeout(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(weatherProperties.getTimeout(), TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(weatherProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
