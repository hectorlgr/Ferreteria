package com.ferreteria.inventario_service.controller;

import com.ferreteria.inventario_service.Dto.InventarioRequestDto;
import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    private final InventarioService inventarioService;

    // GET: Obtener todo el inventario
    // http://localhost:9090/api/inventario
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> obtenerTodos() {
        logger.info("GET /api/inventario - Solicitud para listar todo el inventario");
        List<Inventario> inventarios = inventarioService.obtenerTodos();
        
        List<EntityModel<Inventario>> inventarioModels = inventarios.stream()
            .map(inventario -> EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerPorProductoId(inventario.getProductoId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        logger.debug("Cantidad de registros de inventario obtenidos: {}", inventarios.size());
        return ResponseEntity.ok(CollectionModel.of(inventarioModels, linkSelf.withSelfRel()));
    }

    // GET: Obtener inventario por ID de producto
    // http://localhost:9090/api/inventario/producto/{productoId}
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<EntityModel<Inventario>> obtenerPorProductoId(@PathVariable Long productoId) {
        logger.info("GET /api/inventario/producto/{} - Solicitud para buscar inventario por ID de producto", productoId);
        Inventario inventario = inventarioService.obtenerPorProductoId(productoId);
        
        EntityModel<Inventario> recurso = EntityModel.of(inventario);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorProductoId(productoId));
        WebMvcLinkBuilder linkTodos = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        // Agregamos el self y el volver a la lista
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodos.withRel("todo-el-inventario"));
        
        // Enlaces de acción (ponemos un 0 por defecto para que Spring genere la estructura de la URL)
        recurso.add(linkTo(methodOn(this.getClass()).agregarStock(productoId, 0)).withRel("agregar-stock"));
        recurso.add(linkTo(methodOn(this.getClass()).descontarStock(productoId, 0)).withRel("descontar-stock"));
        
        return ResponseEntity.ok(recurso);
    }

    // POST para registrar nuevo inventario
    // http://localhost:9090/api/inventario
    @PostMapping
    public ResponseEntity<Inventario> guardarInventario(@Valid @RequestBody InventarioRequestDto dto) {
        logger.info("POST /api/inventario - Solicitud para registrar inventario del Producto ID: {}", dto.getProductoId());
        
        // Mapeo manual de DTO a Entidad
        Inventario inventario = new Inventario();
        inventario.setProductoId(dto.getProductoId());
        inventario.setCantidad(dto.getCantidad());
        
        Inventario nuevoInventario = inventarioService.guardarInventario(inventario);
        logger.info("Inventario registrado exitosamente con ID interno: {}", nuevoInventario.getId());
        return new ResponseEntity<>(nuevoInventario, HttpStatus.CREATED);
    }

    // PUT para actualizar el stock de un producto
    // http://localhost:9090/api/inventario/producto/{productoId}/descontar?cantidad={cantidad}
    @PutMapping("/producto/{productoId}/descontar")
    public ResponseEntity<Inventario> descontarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        logger.info("PUT /api/inventario/producto/{}/descontar - Solicitud para descontar {} unidades", productoId, cantidad);
        Inventario inventarioActualizado = inventarioService.actualizarStock(productoId, cantidad);
        logger.info("Stock descontado exitosamente. Nuevo stock para Producto ID {}: {}", productoId, inventarioActualizado.getCantidad());
        return ResponseEntity.ok(inventarioActualizado);
    }

    // PUT para agregar stock a un producto
    // http://localhost:9090/api/inventario/producto/{productoId}/agregar?cantidad={cantidad}
    @PutMapping("/producto/{productoId}/agregar")
    public ResponseEntity<Inventario> agregarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        logger.info("PUT /api/inventario/producto/{}/agregar - Solicitud para ingresar {} unidades", productoId, cantidad);
        Inventario inventarioActualizado = inventarioService.agregarStock(productoId, cantidad);
        logger.info("Stock ingresado exitosamente. Nuevo stock para Producto ID {}: {}", productoId, inventarioActualizado.getCantidad());
        return ResponseEntity.ok(inventarioActualizado);
    }

    // PUT para resetear el stock a cero desde el catalogo-service
    // http://localhost:9090/api/inventario/reset/{productoId}
    @PutMapping("/reset/{productoId}")
    public ResponseEntity<Void> resetearStock(@PathVariable Long productoId) {
        logger.info("PUT /api/inventario/reset/{} - Solicitud de catálogo para resetear stock", productoId);
        inventarioService.resetearStock(productoId);
        return ResponseEntity.noContent().build();
    }
    
    // DELETE para eliminar inventario por ID de producto
    // http://localhost:9090/api/inventario/producto/{productoId} 
    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarPorProductoId(@PathVariable Long productoId) {
        logger.info("DELETE /api/inventario/producto/{} - Solicitud para eliminar inventario", productoId);
        inventarioService.eliminarPorProductoId(productoId);
        logger.info("Inventario eliminado para Producto ID: {}", productoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}