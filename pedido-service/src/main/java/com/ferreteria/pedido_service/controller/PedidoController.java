package com.ferreteria.pedido_service.controller;

import com.ferreteria.pedido_service.Dto.PedidoRequestDto;
import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Pedido>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(idUsuario));
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