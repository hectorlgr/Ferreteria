package com.ferreteria.catalogo_service.service;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Obtener todos los productos (Para el GET general)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por su ID (Para el GET por ID)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado con el ID " + id));
    }

    // Crear un nuevo producto (Para el POST)
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // Actualizar un producto existente (Para el PUT)
    public Producto actualizarProducto(Long id, Producto detallesProducto) {
        Producto productoExistente = obtenerPorId(id);
        
        productoExistente.setNombre(detallesProducto.getNombre());
        productoExistente.setDescripcion(detallesProducto.getDescripcion());
        productoExistente.setMarca(detallesProducto.getMarca());
        productoExistente.setPrecio(detallesProducto.getPrecio());
        
        return productoRepository.save(productoExistente);
    }

    // Eliminar un producto (Para el DELETE)
    public void eliminarProducto(Long id) {
        Producto productoExistente = obtenerPorId(id);
        productoRepository.delete(productoExistente);
    }
}