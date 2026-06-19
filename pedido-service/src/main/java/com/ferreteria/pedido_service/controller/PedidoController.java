package com.ferreteria.pedido_service.controller;

import com.ferreteria.pedido_service.Dto.PedidoRequestDto;
import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Gestión de Pedidos", description = "API para el registro, seguimiento y cancelación de órdenes de compra")
public class PedidoController {

    private final PedidoService pedidoService;

    // POST: Crear un nuevo pedido (Generalmente será llamado internamente por venta-service)
    @Operation(summary = "Crear un nuevo pedido", description = "Genera un registro de pedido asociado a una venta. Este endpoint está diseñado principalmente para ser consumido internamente por el microservicio de Ventas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido generado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar el pedido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crearPedido(
            @Parameter(description = "Objeto con los datos base del pedido (Venta, Usuario y Dirección)") @Valid @RequestBody PedidoRequestDto dto) {
        try {
            // Se añade dto.getDireccion() como tercer parámetro
            Pedido nuevoPedido = pedidoService.crearPedido(dto.getIdUsuario(), dto.getIdVenta(), dto.getDireccion());
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // GET: Historial de compras de un cliente
    @Operation(summary = "Obtener historial de un cliente", description = "Retorna una lista completa de todos los pedidos asociados al identificador de un usuario, junto con sus enlaces HATEOAS de acciones permitidas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial recuperado exitosamente")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario a consultar", example = "5") @PathVariable Long idUsuario) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(idUsuario);

        List<EntityModel<Pedido>> pedidosModel = pedidos.stream()
            .map(pedido -> {
                EntityModel<Pedido> recurso = EntityModel.of(pedido);
                
                // Le inyectamos las "acciones" posibles para este pedido
                recurso.add(linkTo(methodOn(this.getClass()).cancelarPedido(pedido.getId())).withRel("cancelar-pedido"));
                // Le pasamos un estado genérico al link para que sepa qué ruta usar
                recurso.add(linkTo(methodOn(this.getClass()).actualizarEstado(pedido.getId(), "NUEVO_ESTADO")).withRel("actualizar-estado"));
                
                return recurso;
            })
            .collect(Collectors.toList());

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorUsuario(idUsuario));
        return ResponseEntity.ok(CollectionModel.of(pedidosModel, linkSelf.withSelfRel()));
    }

    // PUT: Actualizar el estado del pedido (Llamado internamente por despacho-service)
    @Operation(summary = "Actualizar estado de envío", description = "Cambia el estado actual del pedido (ej. PENDIENTE, EN_RUTA, ENTREGADO). Suele ser consumido por el microservicio de Despachos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode = "400", description = "El pedido no existe o el cambio de estado no es válido", content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @Parameter(description = "ID interno del pedido a modificar", example = "1024") @PathVariable Long id, 
            @Parameter(description = "Nombre del nuevo estado a asignar", example = "EN_RUTA") @RequestParam String nuevoEstado) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: El cliente decide cancelar su orden
    @Operation(summary = "Cancelar pedido", description = "Permite a un cliente o administrador anular un pedido, sujeto a las reglas de negocio (ej. que no haya sido despachado aún).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode = "400", description = "No es posible cancelar el pedido en su estado actual", content = @Content)
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(
            @Parameter(description = "ID interno del pedido a cancelar", example = "1024") @PathVariable Long id) {
        try {
            Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedidoCancelado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}