package com.ferreteria.venta_service.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    // GET: Obtener todas las ventas
    // http://localhost:9090/api/ventas
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerTodas() {
        logger.info("GET /api/ventas - Solicitud para listar todas las ventas");
        List<Venta> ventas = ventaService.obtenerTodas();
        
        List<EntityModel<Venta>> ventasModel = ventas.stream()
            .map(venta -> EntityModel.of(venta,
                linkTo(methodOn(this.getClass()).obtenerPorId(venta.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodas());
        return ResponseEntity.ok(CollectionModel.of(ventasModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener ventas por ID de usuario
    // http://localhost:9090/api/ventas/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        logger.info("GET /api/ventas/usuario/{} - Solicitud para listar ventas por usuario", usuarioId);
        List<Venta> ventas = ventaService.obtenerPorUsuario(usuarioId);
        
        List<EntityModel<Venta>> ventasModel = ventas.stream()
            .map(venta -> EntityModel.of(venta,
                linkTo(methodOn(this.getClass()).obtenerPorId(venta.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorUsuario(usuarioId));
        return ResponseEntity.ok(CollectionModel.of(ventasModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener el historial de ventas por el correo del cliente
    // http://localhost:9090/api/ventas/cliente/email/{email}
    @GetMapping("/cliente/email/{email}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerVentasPorEmail(@PathVariable String email) {
        logger.info("GET /api/ventas/cliente/email/{} - Solicitud de historial de compras", email);
        List<Venta> ventas = ventaService.obtenerVentasPorEmailUsuario(email);
        
        List<EntityModel<Venta>> ventasModel = ventas.stream()
            .map(venta -> EntityModel.of(venta,
                linkTo(methodOn(this.getClass()).obtenerPorId(venta.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerVentasPorEmail(email));
        return ResponseEntity.ok(CollectionModel.of(ventasModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener ventas por rango de fechas
    // http://localhost:9090/api/ventas/rango-fechas?fechaInicio={2024-01-01}&fechaFin={2024-12-31}
    @GetMapping("/rango-fechas")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerPorRangoFechas(
        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
    
        logger.info("GET /api/ventas/rango-fechas - Solicitud de búsqueda. Desde: {} Hasta: {}", fechaInicio, fechaFin);
        List<Venta> ventas = ventaService.obtenerPorRangoFechas(fechaInicio, fechaFin);
        
        List<EntityModel<Venta>> ventasModel = ventas.stream()
            .map(venta -> EntityModel.of(venta,
                linkTo(methodOn(this.getClass()).obtenerPorId(venta.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorRangoFechas(fechaInicio, fechaFin));
        return ResponseEntity.ok(CollectionModel.of(ventasModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener venta por ID
    // http://localhost:9090/api/ventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/ventas/{} - Solicitud para obtener venta por ID", id);
        Venta venta = ventaService.obtenerPorId(id);
        
        EntityModel<Venta> recurso = EntityModel.of(venta);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorId(id));
        WebMvcLinkBuilder linkTodas = linkTo(methodOn(this.getClass()).obtenerTodas());
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodas.withRel("todas-las-ventas"));
        
        return ResponseEntity.ok(recurso);
    }

    // POST: Crear una nueva venta
    // http://localhost:9090/api/ventas
    @PostMapping
    public ResponseEntity<Venta> procesarVenta(@Valid @RequestBody VentaRequestDto dto) {
        logger.info("POST /api/ventas - Solicitud para crear venta. Usuario ID: {}", dto.getUsuarioId());
        
        Venta venta = new Venta();
        venta.setUsuarioId(dto.getUsuarioId());
        venta.setCostoDespacho(dto.getCostoDespacho());
        
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
        
        Venta nuevaVenta = ventaService.procesarVenta(venta, dto.getDireccion(), dto.getCodigoPromocion());
        
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