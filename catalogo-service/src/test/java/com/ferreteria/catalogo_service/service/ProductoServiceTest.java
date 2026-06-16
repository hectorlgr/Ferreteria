package com.ferreteria.catalogo_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.repository.ProductoRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.put()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testGuardarProducto_HabilitaPorDefecto() {
        // GIVEN
        Producto productoNuevo = new Producto();
        productoNuevo.setNombre("Taladro Makita");
        productoNuevo.setPrecio(50000);

        when(productoRepository.save(any(Producto.class))).thenReturn(productoNuevo);

        // WHEN
        Producto resultado = productoService.guardarProducto(productoNuevo);

        // THEN
        assertTrue(resultado.getHabilitado());
        verify(productoRepository, times(1)).save(productoNuevo);
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        // GIVEN
        when(productoRepository.findByIdAndHabilitadoTrue(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            productoService.obtenerPorId(99L);
        });

        assertEquals("Error: Producto no encontrado con el ID 99", excepcion.getMessage());
        verify(productoRepository, times(1)).findByIdAndHabilitadoTrue(99L);
    }

    @Test
    void testEliminarProducto_DeshabilitaYNotificaInventario() {
        // GIVEN
        Producto productoExistente = new Producto();
        productoExistente.setId(5L);
        productoExistente.setHabilitado(true);

        when(productoRepository.findByIdAndHabilitadoTrue(5L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        // WHEN
        productoService.eliminarProducto(5L);

        // THEN
        assertFalse(productoExistente.getHabilitado());
        verify(productoRepository, times(1)).save(productoExistente);
        verify(webClientBuilder, times(1)).build();
    }

    @Test
    void testMarcarComoAgotado_Exito() {
        // GIVEN
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setHabilitado(true);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // WHEN
        productoService.marcarComoAgotado(10L);

        // THEN
        assertFalse(producto.getHabilitado());
        verify(productoRepository, times(1)).save(producto);
    }
}