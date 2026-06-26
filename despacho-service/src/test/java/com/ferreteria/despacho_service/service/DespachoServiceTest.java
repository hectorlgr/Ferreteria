package com.ferreteria.despacho_service.service;

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
    void testCrearDespacho_AsignaEstadoInicialPorDefecto() {
        // GIVEN
        Despacho despachoEntrada = new Despacho();
        despachoEntrada.setPedidoId(1024L);
        despachoEntrada.setDireccion("Av. Providencia 123");

        Despacho despachoGuardado = new Despacho();
        despachoGuardado.setId(1L);
        despachoGuardado.setPedidoId(1024L);
        despachoGuardado.setDireccion("Av. Providencia 123");
        despachoGuardado.setEstado("RECIBIDO_EN_BODEGA");

        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoGuardado);

        // WHEN
        Despacho resultado = despachoService.crearDespacho(despachoEntrada);

        // THEN
        assertNotNull(resultado);
        assertEquals("RECIBIDO_EN_BODEGA", resultado.getEstado());
        verify(despachoRepository, times(1)).save(despachoEntrada);
    }

    @Test
    void testActualizarEstado_Exito() {
        // GIVEN
        Despacho despachoExistente = new Despacho();
        despachoExistente.setId(5L);
        despachoExistente.setEstado("PREPARANDO_PAQUETE");

        when(despachoRepository.findById(5L)).thenReturn(Optional.of(despachoExistente));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoExistente);

        // WHEN
        Despacho resultado = despachoService.actualizarEstado(5L, "EN_RUTA");

        // THEN
        assertEquals("EN_RUTA", resultado.getEstado());
        verify(despachoRepository, times(1)).findById(5L);
        verify(despachoRepository, times(1)).save(despachoExistente);
    }

}