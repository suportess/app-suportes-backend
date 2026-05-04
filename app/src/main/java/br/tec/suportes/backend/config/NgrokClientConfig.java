package br.tec.suportes.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class NgrokClientConfig {

    @Value("${ngrok.api-key}")
    private String apiKey;

    @Value("${ngrok.base-url:https://api.ngrok.com}")
    private String baseUrl;

    @Value("${ngrok.timeout-seconds:30}")
    private int timeoutSeconds;

    @Bean
    public RestClient ngrokRestClient() {
        var factory = new SimpleClientHttpRequestFactory();
        int ms = (int) Duration.ofSeconds(timeoutSeconds).toMillis();
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("ngrok-version", "2")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
