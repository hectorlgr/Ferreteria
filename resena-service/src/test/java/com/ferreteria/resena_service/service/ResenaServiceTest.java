package com.ferreteria.resena_service.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.repository.ResenaRepository;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void testCalcularPromedioProducto_ConResenas() {
        // GIVEN
        Resena r1 = new Resena(); r1.setCalificacion(5);
        Resena r2 = new Resena(); r2.setCalificacion(3);
        when(resenaRepository.findByIdProducto(1L)).thenReturn(Arrays.asList(r1, r2));

        // WHEN
        double promedio = resenaService.calcularPromedioProducto(1L);

        // THEN
        assertEquals(4.0, promedio);
    }

    @Test
    void testCalcularPromedioProducto_SinResenas() {
        // GIVEN
        when(resenaRepository.findByIdProducto(1L)).thenReturn(List.of());

        // WHEN
        double promedio = resenaService.calcularPromedioProducto(1L);

        // THEN
        assertEquals(0.0, promedio);
    }

    @Test
    void testCrearResena_YaExiste_LanzaExcepcion() {
        // GIVEN
        Resena resena = new Resena();
        resena.setIdProducto(1L);
        resena.setIdUsuario(1L);
        when(resenaRepository.existsByIdProductoAndIdUsuario(1L, 1L)).thenReturn(true);

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            resenaService.crearResena(resena);
        });
        assertEquals("Ya has publicado una reseña para este producto. Solo se permite una por cliente.", excepcion.getMessage());
    }
}