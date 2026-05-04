package br.tec.suportes.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class PortalClientConfig {

    @Value("${portal.timeout-seconds:30}")
    private int timeoutSeconds;

    /**
     * RestClient base: sem base URL e sem autenticação fixa.
     * Cada chamada fornece o host e a apikey da empresa ativa do usuário.
     */
    @Bean
    public RestClient portalRestClient() {
        var factory = new SimpleClientHttpRequestFactory();
        int ms = (int) Duration.ofSeconds(timeoutSeconds).toMillis();
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);

        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
