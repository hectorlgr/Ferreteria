package com.ferreteria.api_gateway.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String secret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Rutas públicas: Cualquiera puede hacer login o registrarse
                .pathMatchers("/auth/**").permitAll()
                
                // Ejemplo de Roles: Solo los ADMIN pueden ver/modificar el inventario
                .pathMatchers("/api/inventario/**").hasRole("ADMIN")
                
                // Ejemplo: Ventas pueden ser vistas por ADMIN o VENDEDOR
                .pathMatchers("/api/ventas/**").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                
                // Cualquier otra ruta requiere al menos estar logueado con un token válido
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                // Le decimos a Spring que use nuestro convertidor de roles
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    // Bean del profe para leer la firma del token
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    // --- LEER LOS ROLES ---
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        // 1. Configuramos el extractor de roles estándar
        org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter authoritiesConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        
        authoritiesConverter.setAuthoritiesClaimName("role"); // Busca el campo "role" en tu token
        authoritiesConverter.setAuthorityPrefix("ROLE_");     // Spring requiere este prefijo internamente

        // 2. Armamos el conversor reactivo usando nuestro extractor
        org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter jwtConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter();
            
        jwtConverter.setJwtGrantedAuthoritiesConverter(
            new org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter)
        );

        return jwtConverter;
    }
}