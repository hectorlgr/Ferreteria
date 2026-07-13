package com.ferreteria.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class HashServiceTest {
    private final HashService hashService = new HashService();

    @Test
    void testSha1_HasheaCorrectamente() {
        // GIVEN
        String passwordPura = "123456";

        // Hash SHA-1 universal de "123456"
        String hashEsperado = "7c4a8d09ca3762af61e59520943dc26494f8941b";

        // WHEN
        String resultado = hashService.sha1(passwordPura);

        // THEN
        assertNotNull(resultado);
        assertEquals(hashEsperado, resultado);
    }
}