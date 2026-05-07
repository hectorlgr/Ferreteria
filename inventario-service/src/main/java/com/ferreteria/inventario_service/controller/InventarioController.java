package com.ferreteria.inventario_service.controller;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    @GetMapping
    public List<Inventario> obtenerTodos() {
        logger.info("GET /api/inventario - Solicitud para listar todo el inventario");
        List<Inventario> inventarios = inventarioService.obtenerTodos();
        logger.debug("Cantidad de registros de inventario obtenidos: {}", inventarios.size());
        return inventarios;
    }

    @GetMapping("/producto/{productoId}")
    public Inventario obtenerPorProductoId(@PathVariable Long productoId) {
        logger.info("GET /api/inventario/producto/{} - Solicitud para buscar inventario por ID de producto", productoId);
        return inventarioService.obtenerPorProductoId(productoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventario guardarInventario(@RequestBody Inventario inventario) {
        logger.info("POST /api/inventario - Solicitud para registrar inventario del Producto ID: {}", inventario.getProductoId());
        Inventario nuevoInventario = inventarioService.guardarInventario(inventario);
        logger.info("Inventario registrado exitosamente con ID interno: {}", nuevoInventario.getId());
        return nuevoInventario;
    }

    @PutMapping("/producto/{productoId}/descontar")
    public Inventario descontarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        logger.info("PUT /api/inventario/producto/{}/descontar - Solicitud para descontar {} unidades", productoId, cantidad);
        Inventario inventarioActualizado = inventarioService.actualizarStock(productoId, cantidad);
        logger.info("Stock descontado exitosamente. Nuevo stock para Producto ID {}: {}", productoId, inventarioActualizado.getCantidad());
        return inventarioActualizado;
    }
    
    @DeleteMapping("/producto/{productoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPorProductoId(@PathVariable Long productoId) {
        logger.info("DELETE /api/inventario/producto/{} - Solicitud para eliminar inventario", productoId);
        inventarioService.eliminarPorProductoId(productoId);
        logger.info("Inventario eliminado para Producto ID: {}", productoId);
    }
}