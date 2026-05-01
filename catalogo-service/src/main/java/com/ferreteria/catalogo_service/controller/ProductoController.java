package com.ferreteria.catalogo_service.controller;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // GET: Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    // GET: Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    // POST: Crear un nuevo producto
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve un código 201 (Created) si tiene éxito
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    // PUT: Actualizar un producto existente
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto);
    }

    // DELETE: Eliminar un producto
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve un código 204 (No Content) si tiene éxito
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }
}