package com.ferreteria.catalogo_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.catalogo_service.Dto.ProductoRequestDto;
import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;

    // GET: Obtener todos los productos
    // http://localhost:9091/api/productos
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> obtenerTodos() {
        logger.info("GET /api/productos - Solicitud para listar todos los productos");
        List<Producto> productos = productoService.obtenerTodos();
        
        List<EntityModel<Producto>> productosModel = productos.stream()
            .map(producto -> EntityModel.of(producto,
                linkTo(methodOn(this.getClass()).obtenerPorId(producto.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        logger.debug("Cantidad de productos obtenidos: {}", productos.size());
        return ResponseEntity.ok(CollectionModel.of(productosModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener producto por ID
    // http://localhost:9091/api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/productos/{} - Solicitud para obtener producto por ID", id);
        Producto producto = productoService.obtenerPorId(id);
        
        EntityModel<Producto> recurso = EntityModel.of(producto);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorId(id));
        WebMvcLinkBuilder linkTodos = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodos.withRel("todos-los-productos"));
        
        return ResponseEntity.ok(recurso);
    }

    // POST: Crear un nuevo producto
    // http://localhost:9091/api/productos
    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@Valid @RequestBody ProductoRequestDto dto) {
        logger.info("POST /api/productos - Solicitud para registrar un nuevo producto: {}", dto.getNombre());
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setPrecio(dto.getPrecio());
        
        Producto nuevoProducto = productoService.guardarProducto(producto);
        logger.info("Producto registrado exitosamente con ID: {}", nuevoProducto.getId());
        
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    // http://localhost:9091/api/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDto dto) {
        logger.info("PUT /api/productos/{} - Solicitud para actualizar datos del producto", id);
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setPrecio(dto.getPrecio());
        
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        logger.info("Producto ID {} actualizado correctamente", id);
        
        return ResponseEntity.ok(productoActualizado);
    }

    // DELETE: Eliminar un producto
    // http://localhost:9091/api/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("DELETE /api/productos/{} - Solicitud para eliminar producto", id);
        productoService.eliminarProducto(id);
        logger.info("Producto ID {} eliminado exitosamente", id);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}