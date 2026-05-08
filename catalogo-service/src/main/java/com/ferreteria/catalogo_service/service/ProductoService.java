package com.ferreteria.catalogo_service.service;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        logger.info("Consultando todos los productos en la base de datos");
        return productoRepository.findAll();
    }

    // Método para obtener un producto por su ID, con manejo de excepción si no se encuentra
    public Producto obtenerPorId(Long id) {
        logger.info("Buscando producto en base de datos con ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró el producto con el ID: {}", id);
                    return new RuntimeException("Error: Producto no encontrado con el ID " + id);
                });
    }

    // Método para guardar un nuevo producto
    public Producto guardarProducto(Producto producto) {
        logger.info("Iniciando guardado de producto: {} (Marca: {})", producto.getNombre(), producto.getMarca());
        logger.debug("Precio a registrar: {}", producto.getPrecio());
        
        Producto productoGuardado = productoRepository.save(producto);
        logger.debug("Producto guardado temporalmente con ID interno: {}", productoGuardado.getId());
        
        return productoGuardado;
    }

    // Método para actualizar un producto existente
    public Producto actualizarProducto(Long id, Producto detallesProducto) {
        logger.info("Iniciando actualización para el producto ID: {}", id);
        
        Producto productoExistente = obtenerPorId(id);
        
        logger.debug("Nuevos datos a aplicar -> Nombre: {}, Marca: {}, Precio: {}", 
                detallesProducto.getNombre(), detallesProducto.getMarca(), detallesProducto.getPrecio());
                
        productoExistente.setNombre(detallesProducto.getNombre());
        productoExistente.setDescripcion(detallesProducto.getDescripcion());
        productoExistente.setMarca(detallesProducto.getMarca());
        productoExistente.setPrecio(detallesProducto.getPrecio());
        
        logger.info("Guardando producto actualizado en la base de datos...");
        return productoRepository.save(productoExistente);
    }

    // Método para eliminar un producto por su ID
    public void eliminarProducto(Long id) {
        logger.info("Iniciando proceso de eliminación para el producto ID: {}", id);
        
        Producto productoExistente = obtenerPorId(id);
        
        logger.debug("Procediendo a eliminar el producto de la base de datos...");
        productoRepository.delete(productoExistente);
    }
}