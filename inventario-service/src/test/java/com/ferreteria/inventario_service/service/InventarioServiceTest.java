package com.ferreteria.inventario_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testGuardarInventario_Exito() {
        // GIVEN
        Inventario nuevoInventario = new Inventario();
        nuevoInventario.setProductoId(10L);
        nuevoInventario.setCantidad(50);

        when(inventarioRepository.save(any(Inventario.class))).thenReturn(nuevoInventario);

        // WHEN
        Inventario resultado = inventarioService.guardarInventario(nuevoInventario);

        // THEN
        assertNotNull(resultado);
        assertEquals(50, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(nuevoInventario);
    }

    // --- PRUEBAS DE NEGOCIO: MATEMÁTICA Y REGLAS DE STOCK ---

    @Test
    void testAgregarStock_SumaCorrectamente() {
        // GIVEN
        Inventario inventarioActual = new Inventario();
        inventarioActual.setProductoId(5L);
        inventarioActual.setCantidad(20);

        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inventarioActual));
        // Simulamos que al guardar retorna el mismo objeto actualizado
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        Inventario resultado = inventarioService.agregarStock(5L, 30);

        // THEN
        // Tenía 20, agrego 30 -> Debe quedar en 50
        assertEquals(50, resultado.getCantidad());
        verify(inventarioRepository, times(1)).findByProductoId(5L);
        verify(inventarioRepository, times(1)).save(inventarioActual);
    }

    @Test
    void testDescontarStock_RestaCorrectamente() {
        // GIVEN
        Inventario inventarioActual = new Inventario();
        inventarioActual.setProductoId(5L);
        inventarioActual.setCantidad(20);

        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inventarioActual));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        // Método actualizarStock es el que descuenta según tu controlador anterior
        Inventario resultado = inventarioService.actualizarStock(5L, 5);

        // THEN
        // Tenía 20, descuento 5 -> Debe quedar en 15
        assertEquals(15, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(inventarioActual);
    }

    @Test
    void testDescontarStock_StockInsuficiente_LanzaExcepcion() {
        // GIVEN
        Inventario inventarioActual = new Inventario();
        inventarioActual.setProductoId(5L);
        inventarioActual.setCantidad(10); // Solo hay 10 unidades

        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inventarioActual));

        // WHEN & THEN
        // Intento descontar 15, lo cual debe fallar
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inventarioService.actualizarStock(5L, 15);
        });

        assertEquals("Stock insuficiente para el producto", excepcion.getMessage());
        // Verificamos que NUNCA se llame al método save si falla la validación
        verify(inventarioRepository, times(0)).save(any(Inventario.class));
    }

    @Test
    void testObtenerPorProductoId_NoEncontrado_LanzaExcepcion() {
        // GIVEN
        when(inventarioRepository.findByProductoId(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inventarioService.obtenerPorProductoId(99L);
        });

        assertEquals("No se encontró inventario para el producto ID: 99", excepcion.getMessage());
    }
}