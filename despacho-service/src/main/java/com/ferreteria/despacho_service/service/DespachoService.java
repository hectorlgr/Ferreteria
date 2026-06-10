package com.ferreteria.despacho_service.service;

import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private static final Logger logger = LoggerFactory.getLogger(DespachoService.class);

    private final DespachoRepository despachoRepository;
    private final WebClient.Builder webClientBuilder;

    public List<Despacho> obtenerTodos() {
        return despachoRepository.findAll();
    }

    public Despacho obtenerPorPedidoId(Long pedidoId) {
        logger.info("Buscando despacho asociado al Pedido ID: {}", pedidoId);
        return despachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("No se encontró un despacho asociado al pedido ID: " + pedidoId));
    }

    public Despacho obtenerPorEstado(String estado) {
        return despachoRepository.findByEstado(estado)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con el estado: " + estado));
    }

    public Despacho crearDespacho(Despacho despacho) {
        logger.info("Iniciando creación de despacho para el Pedido ID: {}", despacho.getPedidoId());

        despacho.setEstado("RECIBIDO_EN_BODEGA");
        
        Despacho despachoGuardado = despachoRepository.save(despacho);
        logger.info("Despacho creado exitosamente con ID: {}", despachoGuardado.getId());
        
        return despachoGuardado;
    }

    public Despacho actualizarEstado(Long id, String nuevoEstadoInterno) {
        logger.info("Operario de bodega actualizando Despacho ID: {} a estado logístico: {}", id, nuevoEstadoInterno);
        
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado"));
        
        despacho.setEstado(nuevoEstadoInterno);
        Despacho despachoGuardado = despachoRepository.save(despacho);

        try {
            String estadoMacro = "EN_PROCESO";
            if (nuevoEstadoInterno.equals("ENTREGADO")) {
                estadoMacro = "COMPLETADO";
            }

            logger.info("Notificando a pedido-service el cambio de estado Macro a: {}", estadoMacro);
            webClientBuilder.build().put()
                    .uri("http://pedido-service/api/pedidos/" + despachoGuardado.getPedidoId() + "/estado?nuevoEstado=" + estadoMacro)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            logger.info("Sincronización con pedido-service exitosa.");
        } catch (Exception e) {
            logger.error("No se pudo notificar a pedido-service sobre el cambio de estado: {}", e.getMessage());
        }
        
        return despachoGuardado;
    }

    public void eliminarDespacho(Long id) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado"));
        despachoRepository.delete(despacho);
    }
}