package com.ferreteria.venta_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Bean para WebClient.Builder, que nos permitirá hacer llamadas HTTP a otros microservicios de forma reactiva
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}