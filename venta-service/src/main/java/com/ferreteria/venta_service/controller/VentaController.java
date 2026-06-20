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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Procesamiento de Ventas", description = "API principal para la gestión de compras, carritos y facturación")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    // GET: Obtener todas las ventas
    @Operation(summary = "Obtener historial general de ventas", description = "Retorna una lista con todas las transacciones de venta registradas en la ferretería.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    })
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
    @Operation(summary = "Obtener compras de un usuario", description = "Filtra y retorna todas las transacciones de venta asociadas a un ID de cliente específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas del usuario obtenidas exitosamente")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario comprador", example = "5") @PathVariable Long usuarioId) {
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
    @Operation(summary = "Obtener compras por email de cliente", description = "Realiza una búsqueda cruzada para obtener el historial de compras utilizando el correo electrónico del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial localizado exitosamente")
    })
    @GetMapping("/cliente/email/{email}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerVentasPorEmail(
            @Parameter(description = "Correo electrónico del cliente", example = "juan.perez@email.com") @PathVariable String email) {
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
    @Operation(summary = "Filtrar ventas por fechas", description = "Genera un reporte de ventas realizadas entre dos fechas específicas (formato ISO YYYY-MM-DD).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    })
    @GetMapping("/rango-fechas")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerPorRangoFechas(
        @Parameter(description = "Fecha inicial del rango", example = "2024-01-01") @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
        @Parameter(description = "Fecha final del rango", example = "2024-12-31") @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
    
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
    @Operation(summary = "Obtener detalles de una venta", description = "Retorna la información completa de una transacción específica, incluyendo sus ítems, totales e impuestos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta encontrada", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerPorId(
            @Parameter(description = "ID de la transacción", example = "1024") @PathVariable Long id) {
        logger.info("GET /api/ventas/{} - Solicitud para obtener venta por ID", id);
        Venta venta = ventaService.obtenerPorId(id);
        
        EntityModel<Venta> recurso = EntityModel.of(venta);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorId(id));
        WebMvcLinkBuilder linkTodas = linkTo(methodOn(this.getClass()).obtenerTodas());
        
        // --- LA MEJORA HATEOAS: Descubrimiento del recurso relacionado (Historial del cliente) ---
        WebMvcLinkBuilder linkHistorialUsuario = linkTo(methodOn(this.getClass()).obtenerPorUsuario(venta.getUsuarioId()));
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodas.withRel("todas-las-ventas"));
        recurso.add(linkHistorialUsuario.withRel("otras-compras-del-usuario")); // <--- Se inyecta aquí
        
        return ResponseEntity.ok(recurso);
    }

    // POST: Crear una nueva venta
    @Operation(summary = "Procesar nueva compra", description = "Registra una nueva transacción. Orquesta la validación de promociones, creación del pedido y notificación a despachos internamente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transacción aprobada y registrada exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Venta> procesarVenta(
            @Parameter(description = "Estructura completa de la compra (Usuario, ítems, despacho y promos)") @Valid @RequestBody VentaRequestDto dto) {
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
    @Operation(summary = "Actualizar datos base de venta", description = "Permite modificar datos específicos de una venta (como el usuario o costo de despacho). Nota: Generalmente las ventas finalizadas no deben modificarse.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizarVenta(
            @Parameter(description = "ID de la transacción", example = "1024") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos de la venta") @Valid @RequestBody VentaRequestDto dto) {
        logger.info("PUT /api/ventas/{} - Solicitud para actualizar venta", id);
        
        Venta venta = new Venta();
        venta.setUsuarioId(dto.getUsuarioId());
        venta.setCostoDespacho(dto.getCostoDespacho());
        
        Venta ventaActualizada = ventaService.actualizarVenta(id, venta);
        logger.info("Venta ID {} actualizada correctamente", id);
        return ResponseEntity.ok(ventaActualizada);
    }

    // DELETE: Eliminar una venta
    @Operation(summary = "Anular y eliminar venta", description = "Elimina físicamente el registro de la transacción de la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Venta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(
            @Parameter(description = "ID de la transacción a eliminar", example = "1024") @PathVariable Long id) {
        logger.info("DELETE /api/ventas/{} - Solicitud para eliminar venta", id);
        ventaService.eliminarVenta(id);
        logger.info("Venta ID {} eliminada correctamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}