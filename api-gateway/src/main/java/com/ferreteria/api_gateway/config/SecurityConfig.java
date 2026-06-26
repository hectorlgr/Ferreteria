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
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                // RUTAS PÚBLICAS
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers(
                    "/doc/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/api/*/v3/api-docs",
                    "/api/*/v3/api-docs/**"
                ).permitAll()
                
                // PRODUCTOS (CATÁLOGO)
                .pathMatchers(HttpMethod.GET, "/api/productos/**").authenticated()
                // DELETE permitido a ADMIN y OPERADOR (Soft Delete)
                .pathMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                .pathMatchers("/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                
                // INVENTARIO
                .pathMatchers(HttpMethod.DELETE, "/api/inventario/**").hasRole("ADMIN")
                .pathMatchers("/api/inventario/**").hasAnyRole("ADMIN", "OPERADOR")

                // VENTAS
                .pathMatchers(HttpMethod.GET, "/api/ventas/**").authenticated()
                .pathMatchers(HttpMethod.POST, "/api/ventas/**").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/ventas/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/ventas/**").hasAnyRole("ADMIN", "OPERADOR")
                
                // DESPACHOS
                .pathMatchers(HttpMethod.GET, "/api/despachos/**").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/despachos/**").hasRole("ADMIN")
                .pathMatchers("/api/despachos/**").hasAnyRole("ADMIN", "OPERADOR")

                // USUARIOS
                .pathMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .pathMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "OPERADOR")

                // PROMOCIONES
                .pathMatchers(HttpMethod.GET, "/api/promociones/**").authenticated()
                // REGLA GENERAL: Solo ADMIN borra
                .pathMatchers(HttpMethod.DELETE, "/api/promociones/**").hasRole("ADMIN")
                .pathMatchers("/api/promociones/**").hasAnyRole("ADMIN", "OPERADOR")

                // PEDIDOS
                .pathMatchers(HttpMethod.GET, "/api/pedidos/**").authenticated()
                .pathMatchers(HttpMethod.PUT, "/api/pedidos/*/cancelar").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasRole("ADMIN")
                .pathMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "OPERADOR")
                
                // RESEÑAS
                .pathMatchers(HttpMethod.GET, "/api/resenas/**").authenticated()
                .pathMatchers(HttpMethod.POST, "/api/resenas/**").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/resenas/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/resenas/**").hasAnyRole("ADMIN", "OPERADOR")

                // Cualquier otra ruta requiere autenticación
                .anyExchange().hasRole("ADMIN")
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                // Configurar JWT como método de autenticación
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    // Bean para leer la firma del token
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    // Leer roles
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        // Extractor de roles estándar
        org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter authoritiesConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        
        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        // Conversor reactivo usando nuestro extractor
        org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter jwtConverter = 
            new org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter();
            
        jwtConverter.setJwtGrantedAuthoritiesConverter(
            new org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter)
        );

        return jwtConverter;
    }
}