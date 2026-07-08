package com.ferreteria.pedido_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
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

import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.repository.PedidoRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);

        lenient().when(webClient.post()).thenReturn((WebClient.RequestBodyUriSpec) requestBodyUriSpec);

        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn((WebClient.RequestBodySpec) requestBodySpec);

        lenient().when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);

        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        lenient().when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testCrearPedido_AsignaEstadoConfirmadoYNotifica() {
        // GIVEN
        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(10L);
        pedidoGuardado.setIdUsuario(5L);
        pedidoGuardado.setIdVenta(20L);
        pedidoGuardado.setEstado("CONFIRMADO");

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // WHEN
        Pedido resultado = pedidoService.crearPedido(5L, 20L, "Av. Siempre Viva 123");

        // THEN
        assertNotNull(resultado);
        assertEquals("CONFIRMADO", resultado.getEstado());
        assertEquals(10L, resultado.getId());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(webClientBuilder, times(1)).build();
    }

    @Test
    void testCancelarPedido_RechazadoPorEstadoAvanzado() {
        // GIVEN
        Pedido pedidoAvanzado = new Pedido();
        pedidoAvanzado.setId(1L);
        pedidoAvanzado.setEstado("EN_PROCESO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoAvanzado));

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.cancelarPedido(1L);
        });

        assertEquals("No puedes cancelar un pedido que ya está en proceso de entrega o completado.",
                excepcion.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testActualizarEstado_Exito() {
        // GIVEN
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado("CONFIRMADO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // WHEN
        Pedido resultado = pedidoService.actualizarEstado(1L, "EN_CAMINO");

        // THEN
        assertEquals("EN_CAMINO", resultado.getEstado());
        verify(pedidoRepository, times(1)).save(pedido);
    }
}