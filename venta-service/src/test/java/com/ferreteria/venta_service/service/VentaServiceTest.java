package com.ferreteria.venta_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private VentaService ventaService;

    private Venta ventaPrueba = crearVentaPrueba();

    private Venta crearVentaPrueba() {
        Venta v = new Venta();
        v.setId(1L);
        v.setUsuarioId(99L);
        v.setTotal(15000);
        return v;
    }

    @Test
    void testObtenerTodas_Exito() {
        // GIVEN
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(ventaPrueba));

        // WHEN
        List<Venta> resultado = ventaService.obtenerTodas();

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(15000, resultado.get(0).getTotal());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_Exito() {
        // GIVEN
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaPrueba));

        // WHEN
        Venta resultado = ventaService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(99L, resultado.getUsuarioId());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_NoEncontrada_LanzaExcepcion() {
        // GIVEN
        when(ventaRepository.findById(50L)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ventaService.obtenerPorId(50L);
        });

        assertEquals("Venta no encontrada con ID: 50", excepcion.getMessage());
        verify(ventaRepository, times(1)).findById(50L);
    }

    @Test
    void testObtenerPorRangoFechas_FechasInvalidas_LanzaExcepcion() {
        // GIVEN
        LocalDate inicio = LocalDate.of(2026, 12, 31);
        LocalDate fin = LocalDate.of(2026, 1, 1);

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ventaService.obtenerPorRangoFechas(inicio, fin);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin", excepcion.getMessage());
        verify(ventaRepository, never()).findByFechaRango(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}