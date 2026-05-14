package com.ferreteria.api_gateway.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // 1. RUTAS PÚBLICAS
                .pathMatchers("/auth/**").permitAll()
                
                // 2. PRODUCTOS (CATÁLOGO)
                // Cualquiera logueado puede VER productos
                .pathMatchers(HttpMethod.GET, "/api/productos/**").authenticated()
                // Solo ADMIN y OPERADOR pueden CREAR o EDITAR productos
                .pathMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                .pathMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                // Solo ADMIN puede BORRAR productos
                .pathMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                
                // 3. INVENTARIO
                // El CLIENTE no tiene acceso aquí
                .pathMatchers("/api/inventario/**").hasAnyRole("ADMIN", "OPERADOR")

                // 4. VENTAS
                // Clientes pueden ver y comprar
                .pathMatchers(HttpMethod.GET, "/api/ventas/**").authenticated()
                .pathMatchers(HttpMethod.POST, "/api/ventas/**").authenticated()
                // Pero solo ADMIN y OPERADOR pueden modificar o borrar ventas
                .pathMatchers(HttpMethod.PUT, "/api/ventas/**").hasAnyRole("ADMIN", "OPERADOR")
                .pathMatchers(HttpMethod.DELETE, "/api/ventas/**").hasAnyRole("ADMIN", "OPERADOR")
                
                // 5. DESPACHOS
                // El cliente necesita ver cómo va su envío
                .pathMatchers(HttpMethod.GET, "/api/despachos/**").authenticated()
                // Solo el sistema o el personal crea, actualiza o borra despachos
                .pathMatchers("/api/despachos/**").hasAnyRole("ADMIN", "OPERADOR")

                // 6. USUARIOS
                .pathMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()
                .pathMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "OPERADOR")

                // Cualquier otra ruta requiere autenticación
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