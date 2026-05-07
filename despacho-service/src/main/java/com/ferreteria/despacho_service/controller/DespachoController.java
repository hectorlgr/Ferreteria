package com.ferreteria.despacho_service.controller;


import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @GetMapping
    public List<Despacho> obtenerTodos() {
        return despachoService.obtenerTodos();
    }

    @GetMapping("/venta/{ventaId}")
    public Despacho obtenerPorVentaId(@PathVariable Long ventaId) {
        return despachoService.obtenerPorVentaId(ventaId);
    }

    @GetMapping("/estado/{estado}")
    public Despacho obtenerPorEstado(@PathVariable String estado) {
        return despachoService.obtenerPorEstado(estado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Despacho crearDespacho(@RequestBody Despacho despacho) {
        return despachoService.crearDespacho(despacho);
    }

    @PutMapping("/{id}/estado")
    public Despacho actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return despachoService.actualizarEstado(id, estado);
    }
}