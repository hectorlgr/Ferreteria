package com.ferreteria.inventario_service.controller;

import com.ferreteria.inventario_service.Dto.InventarioRequestDto;
import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    private final InventarioService inventarioService;

    // GET todo el inventario
    @GetMapping
    public ResponseEntity<List<Inventario>> obtenerTodos() {
        logger.info("GET /api/inventario - Solicitud para listar todo el inventario");
        List<Inventario> inventarios = inventarioService.obtenerTodos();
        logger.debug("Cantidad de registros obtenidos: {}", inventarios.size());
        return ResponseEntity.ok(inventarios);
    }

    // GET inventario por ID de producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerPorProductoId(@PathVariable Long productoId) {
        logger.info("GET /api/inventario/producto/{} - Buscando inventario por ID de producto", productoId);
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    // POST para registrar nuevo inventario usando DTO
    @PostMapping
    public ResponseEntity<Inventario> guardarInventario(@Valid @RequestBody InventarioRequestDto dto) {
        logger.info("POST /api/inventario - Solicitud para Producto ID: {}", dto.getProductoId());
        
        Inventario inventario = new Inventario();
        inventario.setProductoId(dto.getProductoId());
        inventario.setCantidad(dto.getCantidad());
        
        Inventario nuevoInventario = inventarioService.guardarInventario(inventario);
        logger.info("Inventario registrado con ID interno: {}", nuevoInventario.getId());
        return new ResponseEntity<>(nuevoInventario, HttpStatus.CREATED);
    }

    // PUT para actualizar el stock (Descontar)
    @PutMapping("/producto/{productoId}/descontar")
    public ResponseEntity<Inventario> descontarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        logger.info("PUT /api/inventario/producto/{}/descontar - Cantidad: {}", productoId, cantidad);
        Inventario actualizado = inventarioService.actualizarStock(productoId, cantidad);
        return ResponseEntity.ok(actualizado);
    }

    // PUT para actualizar el stock (Agregar)
    @PutMapping("/producto/{productoId}/agregar")
    public ResponseEntity<Inventario> agregarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        logger.info("PUT /api/inventario/producto/{}/agregar - Cantidad: {}", productoId, cantidad);
        Inventario actualizado = inventarioService.agregarStock(productoId, cantidad);
        return ResponseEntity.ok(actualizado);
    }

    // PUT para resetear stock
    @PutMapping("/reset/{productoId}")
    public ResponseEntity<Void> resetearStock(@PathVariable Long productoId) {
        logger.info("PUT /api/inventario/reset/{} - Reseteando stock a cero", productoId);
        inventarioService.resetearStock(productoId);
        return ResponseEntity.noContent().build();
    }

    // DELETE para eliminar inventario
    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarPorProductoId(@PathVariable Long productoId) {
        logger.info("DELETE /api/inventario/producto/{} - Eliminando registro", productoId);
        inventarioService.eliminarPorProductoId(productoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}