package com.ferreteria.soporte_service.controller;

import com.ferreteria.soporte_service.Dto.TicketRequestDto;
import com.ferreteria.soporte_service.assembler.TicketModelAssembler;
import com.ferreteria.soporte_service.model.Ticket;
import com.ferreteria.soporte_service.service.SoporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Tag(name = "Soporte y Atención al Cliente", description = "API para la gestión de tickets y reclamos post-venta")
public class SoporteController {

    private static final Logger logger = LoggerFactory.getLogger(SoporteController.class);

    private final SoporteService soporteService;
    private final TicketModelAssembler assembler;

    // POST: Crear un nuevo ticket
    @Operation(summary = "Crear un nuevo ticket de soporte", description = "Registra un reclamo o consulta de un usuario sobre un pedido específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o el pedido no pertenece al usuario", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario o pedido no encontrados en el sistema", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Ticket> crearTicket(
            @Parameter(description = "Objeto con los datos del ticket a crear") @Valid @RequestBody TicketRequestDto dto) {

        logger.info("POST /api/soporte - Solicitud para crear ticket. Usuario ID: {}", dto.getUsuarioId());

        Ticket ticket = new Ticket();
        ticket.setUsuarioId(dto.getUsuarioId());
        ticket.setPedidoId(dto.getPedidoId());
        ticket.setCategoria(dto.getCategoria());
        ticket.setAsunto(dto.getAsunto());
        ticket.setMensaje(dto.getMensaje());

        Ticket nuevoTicket = soporteService.crearTicket(ticket);
        return new ResponseEntity<>(nuevoTicket, HttpStatus.CREATED);
    }

    // GET: Obtener todos los tickets de un usuario
    @Operation(summary = "Obtener tickets por usuario", description = "Devuelve una lista de todos los tickets asociados a un cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida correctamente")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario para consultar su historial de soporte", example = "1") @PathVariable Long usuarioId) {

        logger.info("GET /api/soporte/usuario/{} - Solicitud para listar tickets", usuarioId);

        List<EntityModel<Ticket>> ticketsModel = soporteService.obtenerTicketsPorUsuario(usuarioId).stream()
                .map(assembler::toModel) // HATEOAS delegado al Assembler
                .collect(Collectors.toList());

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorUsuario(usuarioId));

        return ResponseEntity.ok(CollectionModel.of(ticketsModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener el detalle de un ticket por ID
    @Operation(summary = "Obtener detalle de un ticket", description = "Retorna toda la información de un ticket específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> obtenerPorId(
            @Parameter(description = "ID interno del ticket", example = "1") @PathVariable Long id) {

        logger.info("GET /api/soporte/{} - Solicitando detalle de ticket", id);

        Ticket ticket = soporteService.obtenerTicketPorId(id);
        return ResponseEntity.ok(assembler.toModel(ticket));
    }

    // PUT: Actualizar el estado de un ticket
    @Operation(summary = "Actualizar estado del ticket", description = "Modifica la etapa de gestión de un ticket de soporte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))),
            @ApiResponse(responseCode = "404", description = "El ticket a actualizar no fue encontrado", content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<Ticket> actualizarEstado(
            @Parameter(description = "ID interno del ticket", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado (Ej: ABIERTO, EN_REVISION, RESUELTO)", example = "EN_REVISION") @RequestParam String estado) {

        logger.info("PUT /api/soporte/{}/estado - Solicitud para actualizar estado a: {}", id, estado);

        Ticket ticketActualizado = soporteService.actualizarEstado(id, estado);
        logger.info("Estado del ticket ID {} actualizado correctamente", id);

        return ResponseEntity.ok(ticketActualizado);
    }
}