package com.ferreteria.inventario_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        // Configuramos mocks indulgentes para evitar NullPointer en los WebClient.put()
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.put()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testActualizarStock_Exito() {
        // GIVEN
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(100L);
        inventario.setCantidad(50);
        
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // WHEN
        Inventario resultado = inventarioService.actualizarStock(100L, 10);

        // THEN
        assertEquals(40, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void testActualizarStock_Insuficiente_LanzaExcepcion() {
        // GIVEN
        Inventario inventario = new Inventario();
        inventario.setProductoId(100L);
        inventario.setCantidad(5);

        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));

        // WHEN / THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inventarioService.actualizarStock(100L, 10);
        });
        
        assertEquals("Stock insuficiente para el producto ID: 100", excepcion.getMessage());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testAgregarStock_Exito() {
        // GIVEN
        Inventario inventario = new Inventario();
        inventario.setProductoId(200L);
        inventario.setCantidad(10);

        when(inventarioRepository.findByProductoId(200L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // WHEN
        Inventario resultado = inventarioService.agregarStock(200L, 20);

        // THEN
        assertEquals(30, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void testAgregarStock_Invalido_LanzaExcepcion() {
        // GIVEN / WHEN / THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inventarioService.agregarStock(200L, 0);
        });
        
        assertEquals("La cantidad a ingresar debe ser mayor a cero", excepcion.getMessage());
        verify(inventarioRepository, never()).findByProductoId(anyLong());
    }
}