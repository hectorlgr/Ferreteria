package com.ferreteria.pedido_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void testCancelarPedido_RechazadoPorEstadoAvanzado() {
        // GIVEN: Un pedido que ya está "EN_PROCESO"
        Pedido pedidoAvanzado = new Pedido();
        pedidoAvanzado.setId(1L);
        pedidoAvanzado.setEstado("EN_PROCESO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoAvanzado));

        // WHEN & THEN: Verificamos que lance la excepción de negocio
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pedidoService.cancelarPedido(1L);
        });
        
        assertEquals("No puedes cancelar un pedido que ya está en proceso de entrega o completado.", excepcion.getMessage());
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