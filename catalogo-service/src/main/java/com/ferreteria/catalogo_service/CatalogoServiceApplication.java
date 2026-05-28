package com.ferreteria.catalogo_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CatalogoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogoServiceApplication.class, args);
	}

	// Bean para RestTemplate, que nos permitirá hacer llamadas HTTP a otros servicios
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}