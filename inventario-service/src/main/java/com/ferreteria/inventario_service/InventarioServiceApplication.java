package com.ferreteria.inventario_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class InventarioServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventarioServiceApplication.class, args);
	}

	// Bean para RestTemplate, que nos permitirá hacer llamadas HTTP a otros microservicios
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}