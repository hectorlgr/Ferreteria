package com.ferreteria.venta_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class VentaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VentaServiceApplication.class, args);
	}

	// Bean para RestTemplate, que se usará para hacer llamadas HTTP a otros microservicios
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
