package com.ferreteria.catalogo_service.controller;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    // GET: Obtener todos los productos
    // http://localhost:9090/api/productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        logger.info("GET /api/productos - Solicitud para listar todo el catálogo");
        List<Producto> productos = productoService.obtenerTodos();
        logger.debug("Cantidad de productos obtenidos: {}", productos.size());
        return productos;
    }

    // GET: Obtener un producto por ID
    // http://localhost:9090/api/productos/{id}
    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/productos/{} - Solicitud para obtener producto por ID", id);
        return productoService.obtenerPorId(id);
    }

    // POST: Crear un nuevo producto
    // http://localhost:9090/api/productos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto guardarProducto(@RequestBody Producto producto) {
        logger.info("POST /api/productos - Solicitud para registrar nuevo producto: {}", producto.getNombre());
        Producto nuevoProducto = productoService.guardarProducto(producto);
        logger.info("Producto registrado exitosamente con ID: {}", nuevoProducto.getId());
        return nuevoProducto;
    }

    // PUT: Actualizar un producto existente
    // http://localhost:9090/api/productos/{id}
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        logger.info("PUT /api/productos/{} - Solicitud para actualizar datos del producto", id);
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        logger.info("Producto ID {} actualizado correctamente", id);
        return productoActualizado;
    }

    // DELETE: Eliminar un producto
    // http://localhost:9090/api/productos/{id} 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        logger.info("DELETE /api/productos/{} - Solicitud para eliminar producto", id);
        productoService.eliminarProducto(id);
        logger.info("Producto ID {} eliminado exitosamente", id);
    }
}