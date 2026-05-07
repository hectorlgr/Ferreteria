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

    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(DespachoService.class);

    private final DespachoRepository despachoRepository;
    private final WebClient.Builder webClientBuilder;

    public List<Despacho> obtenerTodos() {
        logger.info("Listando todos los despachos desde la base de datos");
        return despachoRepository.findAll();
    }

    public Despacho obtenerPorVentaId(Long ventaId) {
        logger.info("Buscando despacho asociado a la Venta ID: {}", ventaId);
        return despachoRepository.findByVentaId(ventaId)
                .orElseThrow(() -> {
                    logger.warn("No se encontró ningún despacho para la Venta ID: {}", ventaId);
                    return new RuntimeException("No se encontró un despacho asociado a la venta ID: " + ventaId);
                });
    }

    public Despacho obtenerPorEstado(String estado) {
        logger.info("Buscando primer despacho con el estado: {}", estado);
        return despachoRepository.findByEstado(estado)
                .orElseThrow(() -> {
                    logger.warn("No se encontró ningún pedido con el estado: {}", estado);
                    return new RuntimeException("No se encontró el pedido con el estado: " + estado);
                });
    }

    public Despacho crearDespacho(Despacho despacho) {
        logger.info("Iniciando creación de despacho para la Venta ID: {}", despacho.getVentaId());

        // Validación con Venta Service
        try {
            logger.debug("Validando existencia de Venta ID {} en venta-service (Puerto 9094)", despacho.getVentaId());
            webClientBuilder.build().get()
                    .uri("http://localhost:9094/api/ventas/" + despacho.getVentaId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            logger.debug("Validación exitosa: La Venta ID {} existe.", despacho.getVentaId());
        } catch (Exception e) {
            logger.error("Error al validar Venta ID {}. Excepción: {}", despacho.getVentaId(), e.getMessage());
            throw new RuntimeException("Error: La venta ID " + despacho.getVentaId() + " no existe.");
        }

        despacho.setEstado("PREPARANDO");
        logger.info("Guardando nuevo despacho en la base de datos con estado PREPARANDO...");
        
        Despacho despachoGuardado = despachoRepository.save(despacho);
        logger.debug("Despacho guardado temporalmente con ID: {}", despachoGuardado.getId());
        
        return despachoGuardado;
    }

    public Despacho actualizarEstado(Long id, String nuevoEstado) {
        logger.info("Iniciando actualización de estado para Despacho ID: {}", id);
        
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar estado en un despacho inexistente (ID: {})", id);
                    return new RuntimeException("Despacho no encontrado");
                });
        
        despacho.setEstado(nuevoEstado);
        logger.debug("Guardando actualización en la base de datos...");
        
        return despachoRepository.save(despacho);
    }

    public void eliminarDespacho(Long id) {
        logger.info("Iniciando eliminación de despacho ID: {}", id);

        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de eliminar un despacho inexistente (ID: {})", id);
                    return new RuntimeException("Despacho no encontrado");
                });

        despachoRepository.delete(despacho);
        logger.info("Despacho ID {} eliminado correctamente", id);
    }
}