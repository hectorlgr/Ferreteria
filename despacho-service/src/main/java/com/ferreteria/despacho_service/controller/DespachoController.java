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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
@Tag(name = "Gestión de Despachos", description = "API para la coordinación, seguimiento y actualización del estado logístico de las entregas")
public class DespachoController {

    private static final Logger logger = LoggerFactory.getLogger(DespachoController.class);

    private final DespachoService despachoService;

    // GET: Obtener todos los despachos
    @Operation(summary = "Obtener todos los despachos", description = "Retorna una lista completa con la información de todos los despachos registrados en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de despachos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Despacho>>> obtenerTodos() {
        logger.info("GET /api/despachos - Solicitud para listar todos los despachos");
        List<Despacho> despachos = despachoService.obtenerTodos();
        
        List<EntityModel<Despacho>> despachosModel = despachos.stream()
            .map(despacho -> {
                EntityModel<Despacho> recurso = EntityModel.of(despacho);
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
    @Operation(summary = "Buscar despacho por ID de Pedido", description = "Localiza la información de envío asociada a un número de pedido específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Despacho encontrado correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Despacho.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró un despacho para el pedido indicado", content = @Content)
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EntityModel<Despacho>> obtenerPorPedidoId(
            @Parameter(description = "ID del pedido a consultar", example = "1024") @PathVariable Long pedidoId) {
        logger.info("GET /api/despachos/pedido/{} - Solicitud para buscar despacho", pedidoId);
        Despacho despacho = despachoService.obtenerPorPedidoId(pedidoId);
        
        EntityModel<Despacho> recurso = EntityModel.of(despacho);
        recurso.add(linkTo(methodOn(this.getClass()).obtenerPorPedidoId(pedidoId)).withSelfRel());
        recurso.add(linkTo(methodOn(this.getClass()).obtenerTodos()).withRel("todos-los-despachos"));
        recurso.add(linkTo(methodOn(this.getClass()).actualizarEstado(despacho.getId(), "NUEVO_ESTADO")).withRel("actualizar-estado"));
        
        return ResponseEntity.ok(recurso);
    }

    // GET: Obtener despacho por estado
    @Operation(summary = "Filtrar despachos por estado", description = "Obtiene los despachos que se encuentran en un estado logístico determinado (ej. EN_RUTA).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Despacho(s) encontrado(s) correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Despacho.class))),
        @ApiResponse(responseCode = "404", description = "No existen despachos en el estado especificado", content = @Content)
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<EntityModel<Despacho>> obtenerPorEstado(
            @Parameter(description = "Estado logístico a buscar", example = "EN_RUTA") @PathVariable String estado) {
        logger.info("GET /api/despachos/estado/{} - Solicitud para buscar despachos por estado", estado);
        Despacho despacho = despachoService.obtenerPorEstado(estado);
        
        EntityModel<Despacho> recurso = EntityModel.of(despacho);
        recurso.add(linkTo(methodOn(this.getClass()).obtenerPorEstado(estado)).withSelfRel());
        recurso.add(linkTo(methodOn(this.getClass()).obtenerTodos()).withRel("todos-los-despachos"));
        
        return ResponseEntity.ok(recurso);
    }

    // POST: Crear un nuevo despacho
    @Operation(summary = "Generar orden de despacho", description = "Crea un nuevo registro de entrega logística para un pedido que acaba de ser confirmado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden de despacho generada exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Despacho.class))),
        @ApiResponse(responseCode = "400", description = "Datos requeridos inválidos o faltantes", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Despacho> crearDespacho(
            @Parameter(description = "Datos base para registrar la entrega") @Valid @RequestBody DespachoRequestDto dto) {
        logger.info("POST /api/despachos - Creando despacho para Pedido ID: {}", dto.getIdPedido());
        
        Despacho despacho = new Despacho();
        despacho.setPedidoId(dto.getIdPedido());
        despacho.setDireccion(dto.getDireccion());
        
        Despacho nuevoDespacho = despachoService.crearDespacho(despacho);
        return new ResponseEntity<>(nuevoDespacho, HttpStatus.CREATED);
    }

    // PUT: Actualizar el estado de un despacho
    @Operation(summary = "Actualizar estado logístico", description = "Modifica la etapa de envío de un paquete.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Despacho.class))),
        @ApiResponse(responseCode = "404", description = "El despacho a actualizar no fue encontrado", content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(
            @Parameter(description = "ID interno del despacho", example = "1") @PathVariable Long id, 
            @Parameter(description = "Nuevo estado (Ej: RECIBIDO_EN_BODEGA, PREPARANDO_PAQUETE, EN_RUTA, ENTREGADO)", example = "EN_RUTA") @RequestParam String estado) {
        logger.info("PUT /api/despachos/{}/estado - Solicitud para actualizar estado a: {}", id, estado);
        Despacho despachoActualizado = despachoService.actualizarEstado(id, estado);
        logger.info("Estado del despacho ID {} actualizado correctamente", id);
        return ResponseEntity.ok(despachoActualizado);
    }

    // DELETE: Eliminar un despacho por ID
    @Operation(summary = "Eliminar un despacho", description = "Borra el registro logístico del sistema utilizando su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Despacho eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "El despacho a eliminar no fue encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDespacho(
            @Parameter(description = "ID interno del despacho a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/despachos/{} - Solicitud para eliminar despacho", id);
        despachoService.eliminarDespacho(id);
        logger.info("Despacho ID {} eliminado correctamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}