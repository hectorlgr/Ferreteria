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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // POST: Crear un nuevo pedido (Generalmente será llamado internamente por venta-service)
    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequestDto dto) {
        try {
            // Se añade dto.getDireccion() como tercer parámetro
            Pedido nuevoPedido = pedidoService.crearPedido(dto.getIdUsuario(), dto.getIdVenta(), dto.getDireccion());
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // GET: Historial de compras de un cliente
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorUsuario(@PathVariable Long idUsuario) {
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
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: El cliente decide cancelar su orden
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedidoCancelado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}