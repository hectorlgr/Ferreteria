package com.ferreteria.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtServiceTest {

    private JwtService jwtService;
    private final String LLAVE_SECRETA_TEST = "FerreteriaSuperSecretaKeyParaTokens12345!";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", LLAVE_SECRETA_TEST);
    }

    @Test
    void testGenerarYValidarToken() {
        // GIVEN
        String email = "hector@ferreteria.com";
        String rol = "ADMIN";

        // WHEN - Generar
        String token = jwtService.generateToken(email, rol);

        // THEN - Validar que se creó
        assertNotNull(token);

        // Validar sin el prefijo Bearer
        assertTrue(jwtService.isValid(token));
        assertEquals(email, jwtService.getEmailFromToken(token));

        // Validar con el prefijo Bearer (simulando un header real)
        assertTrue(jwtService.isValid("Bearer " + token));
        assertEquals(email, jwtService.getEmailFromToken("Bearer " + token));
    }

    @Test
    void testValidarTokenInvalido() {
        // Tokens nulos o vacíos deben retornar false y no romper la aplicación
        assertFalse(jwtService.isValid(null));
        assertFalse(jwtService.isValid(""));

        // Tokens alterados o inventados deben capturar la excepción y retornar
        // false/null
        String tokenFalso = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.falso.firma";
        assertFalse(jwtService.isValid(tokenFalso));
        assertNull(jwtService.getEmailFromToken(tokenFalso));
    }
}