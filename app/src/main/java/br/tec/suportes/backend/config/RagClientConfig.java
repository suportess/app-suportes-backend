package br.tec.suportes.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RagClientConfig {

    @Value("${rag.base-url:http://localhost:8032}")
    private String baseUrl;

    @Value("${rag.timeout-seconds:60}")
    private int timeoutSeconds;

    @Bean
    public RestClient ragRestClient() {
        var factory = new SimpleClientHttpRequestFactory();
        int ms = (int) Duration.ofSeconds(timeoutSeconds).toMillis();
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
