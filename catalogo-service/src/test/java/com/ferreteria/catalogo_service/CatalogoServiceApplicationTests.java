package com.ferreteria.catalogo_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Deshabilitado para no requerir conexión a BD durante los tests unitarios")
class CatalogoServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}