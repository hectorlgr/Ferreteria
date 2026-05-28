package com.ferreteria.catalogo_service.service;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    @Autowired
    private RestTemplate restTemplate;

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;

    // Método para obtener todos los productos habilitados
    public List<Producto> obtenerTodos() {
        logger.info("Consultando todos los productos en la base de datos");
        return productoRepository.findByHabilitadoTrue();
    }

    // Método para obtener un producto por su ID, con manejo de excepción si no se encuentra
    public Producto obtenerPorId(Long id) {
        logger.info("Buscando producto activo con ID: {}", id);
        return productoRepository.findByIdAndHabilitadoTrue(id)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: El producto con ID {} no existe o está deshabilitado", id);
                    return new RuntimeException("Error: Producto no encontrado con el ID " + id);
                });
    }

    // Método para guardar un nuevo producto
    public Producto guardarProducto(Producto producto) {
        logger.info("Registrando nuevo producto: {}", producto.getNombre());
        logger.debug("Precio a registrar: {}", producto.getPrecio());
        
        producto.setHabilitado(true);
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

    // Método para deshabilitar un producto (baja lógica) y resetear su stock en el inventario
    public void eliminarProducto(Long id) {
        logger.info("Iniciando deshabilitación y reseteo de stock para ID: {}", id);
    
        Producto productoExistente = obtenerPorId(id);
        productoExistente.setHabilitado(false);
        productoRepository.save(productoExistente);

        // Llamar al inventario-service: Poner el stock en 0
        try {
            String url = "http://localhost:9093/api/inventario/reset/" + id;
            restTemplate.put(url, null);
            logger.info("Stock reseteado en inventario-service para producto ID: {}", id);
        } catch (Exception e) {
            logger.error("No se pudo resetear el stock: {}", e.getMessage());
        }
    }

    // Método para que otros servicios deshabiliten productos automáticamente
    public void marcarComoAgotado(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    
        if (producto.getHabilitado()) {
            producto.setHabilitado(false);
            productoRepository.save(producto);
            logger.info("Producto ID: {} deshabilitado automáticamente por falta de stock", id);
        }
    }

    // Método para reactivar un producto (por ejemplo, cuando vuelve a tener stock)
    public void habilitarProducto(Long id) {
        logger.info("Iniciando habilitación para el producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado en la BD general con ID: " + id));
            
        if (!producto.getHabilitado()) {
            producto.setHabilitado(true);
            productoRepository.save(producto);
            logger.info("Producto ID: {} reactivado y visible en el catálogo", id);
        } else {
            logger.info("El producto ID: {} ya estaba habilitado", id);
        }
    }
}