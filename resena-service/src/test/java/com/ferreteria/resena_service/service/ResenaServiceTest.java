package com.ferreteria.resena_service.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.repository.ResenaRepository;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void testObtenerResenasPorProducto_Exito() {
        // GIVEN
        Resena resena = new Resena();
        resena.setIdProducto(1L);
        when(resenaRepository.findByIdProducto(1L)).thenReturn(Arrays.asList(resena));

        // WHEN
        List<Resena> resultado = resenaService.obtenerResenasPorProducto(1L);

        // THEN
        assertEquals(1, resultado.size());
        verify(resenaRepository, times(1)).findByIdProducto(1L);
    }

    @Test
    void testCalcularPromedioProducto_ConResenas() {
        // GIVEN
        Resena r1 = new Resena();
        r1.setCalificacion(5);
        Resena r2 = new Resena();
        r2.setCalificacion(4);
        Resena r3 = new Resena();
        r3.setCalificacion(3);

        when(resenaRepository.findByIdProducto(2L)).thenReturn(Arrays.asList(r1, r2, r3));

        // WHEN
        Double promedio = resenaService.calcularPromedioProducto(2L);

        // THEN
        assertEquals(4.0, promedio);
        verify(resenaRepository, times(1)).findByIdProducto(2L);
    }

    @Test
    void testCalcularPromedioProducto_SinResenas() {
        // GIVEN
        when(resenaRepository.findByIdProducto(3L)).thenReturn(Collections.emptyList());

        // WHEN
        Double promedio = resenaService.calcularPromedioProducto(3L);

        // THEN
        assertEquals(0.0, promedio);
        verify(resenaRepository, times(1)).findByIdProducto(3L);
    }
}