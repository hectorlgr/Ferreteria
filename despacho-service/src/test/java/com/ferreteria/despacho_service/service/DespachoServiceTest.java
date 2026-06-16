package com.ferreteria.despacho_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.repository.DespachoRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private DespachoService despachoService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.put()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testCrearDespacho_Exito() {
        // GIVEN
        Despacho despachoNuevo = new Despacho();
        despachoNuevo.setPedidoId(500L);

        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoNuevo);

        // WHEN
        Despacho resultado = despachoService.crearDespacho(despachoNuevo);

        // THEN
        assertEquals("RECIBIDO_EN_BODEGA", resultado.getEstado());
        verify(despachoRepository, times(1)).save(despachoNuevo);
    }

    @Test
    void testActualizarEstado_AEntregado_Exito() {
        // GIVEN
        Despacho despacho = new Despacho();
        despacho.setId(1L);
        despacho.setPedidoId(500L);
        despacho.setEstado("RECIBIDO_EN_BODEGA");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

        // WHEN
        Despacho resultado = despachoService.actualizarEstado(1L, "ENTREGADO");

        // THEN
        assertEquals("ENTREGADO", resultado.getEstado());
        verify(despachoRepository, times(1)).save(despacho);
    }

    @Test
    void testObtenerPorPedidoId_NoEncontrado_LanzaExcepcion() {
        // GIVEN
        when(despachoRepository.findByPedidoId(999L)).thenReturn(Optional.empty());

        // WHEN / THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            despachoService.obtenerPorPedidoId(999L);
        });

        assertEquals("No se encontró un despacho asociado al pedido ID: 999", excepcion.getMessage());
        verify(despachoRepository, times(1)).findByPedidoId(999L);
    }
}