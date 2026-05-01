package com.ferreteria.venta_service.controller;

import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<Venta> obtenerTodas() {
        return ventaService.obtenerTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Venta> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ventaService.obtenerPorUsuario(usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta procesarVenta(@RequestBody Venta venta) {
        return ventaService.procesarVenta(venta);
    }
}