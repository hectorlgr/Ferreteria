package com.ferreteria.inventario_service.controller;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<Inventario> obtenerTodos() {
        return inventarioService.obtenerTodos();
    }

    // Buscamos por el ID del producto (es más útil que buscar por el ID del registro de inventario)
    @GetMapping("/producto/{productoId}")
    public Inventario obtenerPorProductoId(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventario guardarInventario(@RequestBody Inventario inventario) {
        return inventarioService.guardarInventario(inventario);
    }

    // Endpoint específico para descontar stock (lo usará venta-service)
    @PutMapping("/producto/{productoId}/descontar")
    public Inventario descontarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        return inventarioService.actualizarStock(productoId, cantidad);
    }
}