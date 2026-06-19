package com.ferreteria.despacho_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<CollectionModel<EntityModel<Despacho>>> obtenerTodos() {
        logger.info("GET /api/despachos - Solicitud para listar todos los despachos");
        List<Despacho> despachos = despachoService.obtenerTodos();
        
        List<EntityModel<Despacho>> despachosModel = despachos.stream()
            .map(despacho -> {
                EntityModel<Despacho> recurso = EntityModel.of(despacho);
                // Usamos el pedidoId como referencia para el "self" ya que no hay GET /{id}
                recurso.add(linkTo(methodOn(this.getClass()).obtenerPorPedidoId(despacho.getPedidoId())).withSelfRel());
                recurso.add(linkTo(methodOn(this.getClass()).actualizarEstado(despacho.getId(), "NUEVO_ESTADO")).withRel("actualizar-estado"));
                return recurso;
            })
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
        logger.debug("Cantidad de despachos obtenidos: {}", despachos.size());
        
        return ResponseEntity.ok(CollectionModel.of(despachosModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener despacho por ID de pedido
    // http://localhost:9090/api/despachos/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EntityModel<Despacho>> obtenerPorPedidoId(@PathVariable Long pedidoId) {
        logger.info("GET /api/despachos/pedido/{} - Solicitud para buscar despacho", pedidoId);
        Despacho despacho = despachoService.obtenerPorPedidoId(pedidoId);
        
        EntityModel<Despacho> recurso = EntityModel.of(despacho);
        recurso.add(linkTo(methodOn(this.getClass()).obtenerPorPedidoId(pedidoId)).withSelfRel());
        recurso.add(linkTo(methodOn(this.getClass()).obtenerTodos()).withRel("todos-los-despachos"));
        recurso.add(linkTo(methodOn(this.getClass()).actualizarEstado(despacho.getId(), "NUEVO_ESTADO")).withRel("actualizar-estado"));
        
        return ResponseEntity.ok(recurso);
    }

    // GET: Obtener despacho por estado
    // http://localhost:9090/api/despachos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<EntityModel<Despacho>> obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/despachos/estado/{} - Solicitud para buscar despachos por estado", estado);
        Despacho despacho = despachoService.obtenerPorEstado(estado);
        
        EntityModel<Despacho> recurso = EntityModel.of(despacho);
        recurso.add(linkTo(methodOn(this.getClass()).obtenerPorEstado(estado)).withSelfRel());
        recurso.add(linkTo(methodOn(this.getClass()).obtenerTodos()).withRel("todos-los-despachos"));
        
        return ResponseEntity.ok(recurso);
    }

    // http://localhost:9090/api/despachos
    // POST: Crear un nuevo despacho
    @PostMapping
    public ResponseEntity<Despacho> crearDespacho(@Valid @RequestBody DespachoRequestDto dto) {
        logger.info("POST /api/despachos - Creando despacho para Pedido ID: {}", dto.getIdPedido());
        
        Despacho despacho = new Despacho();
        despacho.setPedidoId(dto.getIdPedido());
        despacho.setDireccion(dto.getDireccion());
        
        Despacho nuevoDespacho = despachoService.crearDespacho(despacho);
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

    // DELETE: Eliminar un despacho por ID
    // http://localhost:9090/api/despachos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDespacho(@PathVariable Long id) {
        logger.info("DELETE /api/despachos/{} - Solicitud para eliminar despacho", id);
        despachoService.eliminarDespacho(id);
        logger.info("Despacho ID {} eliminado correctamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}