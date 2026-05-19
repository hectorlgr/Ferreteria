package com.ferreteria.despacho_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.despacho_service.Dto.DespachoRequestDto;
import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.service.DespachoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(DespachoController.class);

    private final DespachoService despachoService;

    // GET: Obtener todos los despachos
    // http://localhost:9090/api/despachos
    @GetMapping
    public ResponseEntity<List<Despacho>> obtenerTodos() {
        logger.info("GET /api/despachos - Solicitud para listar todos los despachos");
        List<Despacho> despachos = despachoService.obtenerTodos();
        logger.debug("Cantidad de despachos obtenidos: {}", despachos.size());
        return ResponseEntity.ok(despachos);
    }

    // GET: Obtener despacho por ID de venta
    // http://localhost:9090/api/despachos/venta/{ventaId}
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<Despacho> obtenerPorVentaId(@PathVariable Long ventaId) {
        logger.info("GET /api/despachos/venta/{} - Solicitud para buscar despacho por ID de venta", ventaId);
        return ResponseEntity.ok(despachoService.obtenerPorVentaId(ventaId));
    }

    // GET: Obtener despacho por estado
    // http://localhost:9090/api/despachos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Despacho> obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/despachos/estado/{} - Solicitud para buscar despachos por estado", estado);
        return ResponseEntity.ok(despachoService.obtenerPorEstado(estado));
    }

    // POST: Crear un nuevo despacho
    // http://localhost:9090/api/despachos
    @PostMapping
    public ResponseEntity<Despacho> crearDespacho(@Valid @RequestBody DespachoRequestDto dto) {
        logger.info("POST /api/despachos - Solicitud para crear despacho para Venta ID: {}", dto.getVentaId());
        
        // Mapeo manual del DTO a la Entidad
        Despacho despacho = new Despacho();
        despacho.setVentaId(dto.getVentaId());
        despacho.setDireccion(dto.getDireccion());
        
        Despacho nuevoDespacho = despachoService.crearDespacho(despacho);
        logger.info("Despacho creado exitosamente con ID: {}", nuevoDespacho.getId());
        return new ResponseEntity<>(nuevoDespacho, HttpStatus.CREATED);
    }

    // PUT: Actualizar el estado de un despacho
    // http://localhost:9090/api/despachos/{id}/estado?estado={nuevoEstado} (Ej: "EN_RUTA", "ENTREGADO")
    @PutMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        logger.info("PUT /api/despachos/{}/estado - Solicitud para actualizar estado a: {}", id, estado);
        Despacho despachoActualizado = despachoService.actualizarEstado(id, estado);
        logger.info("Estado del despacho ID {} actualizado correctamente", id);
        return ResponseEntity.ok(despachoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDespacho(@PathVariable Long id) {
        logger.info("DELETE /api/despachos/{} - Solicitud para eliminar despacho", id);
        despachoService.eliminarDespacho(id);
        logger.info("Despacho ID {} eliminado correctamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}