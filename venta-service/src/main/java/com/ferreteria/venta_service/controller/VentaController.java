package com.ferreteria.venta_service.controller;

import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    @GetMapping
    public List<Venta> obtenerTodas() {
        logger.info("GET /api/ventas - Solicitud para listar todas las ventas");
        List<Venta> ventas = ventaService.obtenerTodas();
        logger.debug("Cantidad de ventas obtenidas: {}", ventas.size());
        return ventas;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Venta> obtenerPorUsuario(@PathVariable Long usuarioId) {
        logger.info("GET /api/ventas/usuario/{} - Solicitud para listar ventas por usuario", usuarioId);
        List<Venta> ventas = ventaService.obtenerPorUsuario(usuarioId);
        logger.debug("Cantidad de ventas obtenidas para el usuario {}: {}", usuarioId, ventas.size());
        return ventas;
    }

    @GetMapping("/rango-fechas")
public List<Venta> obtenerPorRangoFechas(
        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
    
    logger.info("GET /api/ventas/rango-fechas - Solicitud de búsqueda. Desde: {} Hasta: {}", fechaInicio, fechaFin);
    
    return ventaService.obtenerPorRangoFechas(fechaInicio, fechaFin);
}

    @GetMapping("/{id}")
    public Venta obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/ventas/{} - Solicitud para obtener venta por ID", id);
        return ventaService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta procesarVenta(@RequestBody Venta venta) {
        logger.info("POST /api/ventas - Solicitud para crear venta. Usuario ID: {}", venta.getUsuarioId());
        
        Venta nuevaVenta = ventaService.procesarVenta(venta);
        
        logger.info("Venta creada exitosamente con ID: {}", nuevaVenta.getId());
        return nuevaVenta;
    }

    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable Long id, @RequestBody Venta venta) {
        logger.info("PUT /api/ventas/{} - Solicitud para actualizar venta", id);
        Venta ventaActualizada = ventaService.actualizarVenta(id, venta);
        logger.info("Venta ID {} actualizada correctamente", id);
        return ventaActualizada;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarVenta(@PathVariable Long id) {
        logger.info("DELETE /api/ventas/{} - Solicitud para eliminar venta", id);
        ventaService.eliminarVenta(id);
        logger.info("Venta ID {} eliminada correctamente", id);
    }
}