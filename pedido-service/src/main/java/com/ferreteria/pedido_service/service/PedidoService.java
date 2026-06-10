package com.ferreteria.pedido_service.service;

import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public Pedido crearPedido(Long idUsuario, Long idVenta) {
        logger.info("Iniciando orquestación de nuevo pedido para Usuario ID: {} y Venta ID: {}", idUsuario, idVenta);

        // Crear y guardar el estado inicial
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setIdUsuario(idUsuario);
        nuevoPedido.setIdVenta(idVenta);
        nuevoPedido.setEstado("CONFIRMADO");
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        logger.info("Pedido interno creado con ID: {}", pedidoGuardado.getId());

        try {
            Map<String, Object> despachoPayload = new HashMap<>();
            despachoPayload.put("idPedido", pedidoGuardado.getId());
            despachoPayload.put("idUsuario", idUsuario);

            logger.info("Enviando orden a despacho-service...");
            webClientBuilder.build().post()
                    .uri("http://despacho-service/api/despachos")
                    .bodyValue(despachoPayload)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            logger.info("Orden recibida exitosamente por despacho-service.");

        } catch (Exception e) {
            logger.error("Error crítico al contactar a despacho-service: {}", e.getMessage());
            throw new RuntimeException("El pedido fue creado, pero falló la comunicación con el sistema de despachos.");
        }

        return pedidoGuardado;
    }

    public List<Pedido> obtenerPedidosPorUsuario(Long idUsuario) {
        logger.info("Consultando historial de pedidos para el Usuario ID: {}", idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario);
    }

    public Pedido actualizarEstado(Long idPedido, String nuevoEstado) {
        logger.info("Actualizando pedido ID: {} a nuevo estado MACRO: {}", idPedido, nuevoEstado);
        
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
                
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    // Cancelar Pedido (Solo si no está en ruta ni entregado)
    public Pedido cancelarPedido(Long idPedido) {
        logger.info("Solicitud de cancelación para Pedido ID: {}", idPedido);

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado."));

        if (pedido.getEstado().equals("EN_PROCESO") || pedido.getEstado().equals("COMPLETADO")) {
            logger.warn("Cancelación rechazada. El pedido ya está en una etapa logística avanzada.");
            throw new RuntimeException("No puedes cancelar un pedido que ya está en proceso de entrega o completado.");
        }

        pedido.setEstado("CANCELADO");
        Pedido pedidoCancelado = pedidoRepository.save(pedido);

        logger.info("Pedido marcado como CANCELADO. (Pendiente: Notificar a Venta y Despacho para reversar operaciones).");
        
        return pedidoCancelado;
    }
}