package com.ferreteria.venta_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.venta_service.Dto.VentaRequestDto;
import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    //GET todas las ventas
    // http://localhost:9090/api/ventas
    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        logger.info("GET /api/ventas - Solicitud para listar todas las ventas");
        List<Venta> ventas = ventaService.obtenerTodas();
        logger.debug("Cantidad de ventas obtenidas: {}", ventas.size());
        return ResponseEntity.ok(ventas);
    }

    // GET: Obtener ventas por ID de usuario
    // http://localhost:9090/api/ventas/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        logger.info("GET /api/ventas/usuario/{} - Solicitud para listar ventas por usuario", usuarioId);
        List<Venta> ventas = ventaService.obtenerPorUsuario(usuarioId);
        logger.debug("Cantidad de ventas obtenidas para el usuario {}: {}", usuarioId, ventas.size());
        return ResponseEntity.ok(ventas);
    }

    // GET: Obtener ventas por rango de fechas
    // http://localhost:9090/api/ventas/rango-fechas?fechaInicio={2024-01-01}&fechaFin={2024-12-31}
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<Venta>> obtenerPorRangoFechas(
        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
    
        logger.info("GET /api/ventas/rango-fechas - Solicitud de búsqueda. Desde: {} Hasta: {}", fechaInicio, fechaFin);
    
        return ResponseEntity.ok(ventaService.obtenerPorRangoFechas(fechaInicio, fechaFin));
    }

    // GET: Obtener una venta por ID
    // http://localhost:9090/api/ventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/ventas/{} - Solicitud para obtener venta por ID", id);
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    // POST: Crear una nueva venta
    // http://localhost:9090/api/ventas
    @PostMapping
    public ResponseEntity<Venta> procesarVenta(@Valid @RequestBody VentaRequestDto dto) {
        logger.info("POST /api/ventas - Solicitud para crear venta. Usuario ID: {}", dto.getUsuarioId());
        
        // Mapeo manual del DTO a la Entidad Venta
        Venta venta = new Venta();
        venta.setUsuarioId(dto.getUsuarioId());
        venta.setCostoDespacho(dto.getCostoDespacho());
        
        // Mapeo de los detalles
        List<DetalleVenta> detalles = dto.getDetalles().stream().map(dDto -> {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(dDto.getProductoId());
            detalle.setCantidad(dDto.getCantidad());
            detalle.setPrecioUnitario(dDto.getPrecioUnitario());
            detalle.setSubtotal(dDto.getCantidad() * dDto.getPrecioUnitario());
            detalle.setVenta(venta);
            return detalle;
        }).collect(Collectors.toList());
        
        venta.setDetalles(detalles);
        
        Venta nuevaVenta = ventaService.procesarVenta(venta);
        
        logger.info("Venta creada exitosamente con ID: {}", nuevaVenta.getId());
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    // PUT: Actualizar una venta existente
    // http://localhost:9090/api/ventas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizarVenta(@PathVariable Long id, @Valid @RequestBody VentaRequestDto dto) {
        logger.info("PUT /api/ventas/{} - Solicitud para actualizar venta", id);
        
        Venta venta = new Venta();
        venta.setUsuarioId(dto.getUsuarioId());
        venta.setCostoDespacho(dto.getCostoDespacho());
        // (La lógica de actualizar detalles suele ser más compleja en el Service, 
        // pero mapeamos lo básico para el controlador)
        
        Venta ventaActualizada = ventaService.actualizarVenta(id, venta);
        logger.info("Venta ID {} actualizada correctamente", id);
        return ResponseEntity.ok(ventaActualizada);
    }

    // DELETE: Eliminar una venta
    // http://localhost:9090/api/ventas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        logger.info("DELETE /api/ventas/{} - Solicitud para eliminar venta", id);
        ventaService.eliminarVenta(id);
        logger.info("Venta ID {} eliminada correctamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}