package com.ferreteria.despacho_service.controller;

import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(DespachoController.class);

    private final DespachoService despachoService;

    @GetMapping
    public List<Despacho> obtenerTodos() {
        logger.info("GET /api/despachos - Solicitud para listar todos los despachos");
        List<Despacho> despachos = despachoService.obtenerTodos();
        logger.debug("Cantidad de despachos obtenidos: {}", despachos.size());
        return despachos;
    }

    @GetMapping("/venta/{ventaId}")
    public Despacho obtenerPorVentaId(@PathVariable Long ventaId) {
        logger.info("GET /api/despachos/venta/{} - Solicitud para buscar despacho por ID de venta", ventaId);
        return despachoService.obtenerPorVentaId(ventaId);
    }

    @GetMapping("/estado/{estado}")
    public Despacho obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/despachos/estado/{} - Solicitud para buscar despachos por estado", estado);
        return despachoService.obtenerPorEstado(estado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Despacho crearDespacho(@RequestBody Despacho despacho) {
        logger.info("POST /api/despachos - Solicitud para crear despacho para Venta ID: {}", despacho.getVentaId());
        Despacho nuevoDespacho = despachoService.crearDespacho(despacho);
        logger.info("Despacho creado exitosamente con ID: {}", nuevoDespacho.getId());
        return nuevoDespacho;
    }

    @PutMapping("/{id}/estado")
    public Despacho actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        logger.info("PUT /api/despachos/{}/estado - Solicitud para actualizar estado a: {}", id, estado);
        Despacho despachoActualizado = despachoService.actualizarEstado(id, estado);
        logger.info("Estado del despacho ID {} actualizado correctamente", id);
        return despachoActualizado;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDespacho(@PathVariable Long id) {
        logger.info("DELETE /api/despachos/{} - Solicitud para eliminar despacho", id);
        despachoService.eliminarDespacho(id);
        logger.info("Despacho ID {} eliminado correctamente", id);
    }
}