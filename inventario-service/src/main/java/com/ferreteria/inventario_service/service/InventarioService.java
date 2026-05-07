package com.ferreteria.inventario_service.service;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;

    // Método para obtener todos los registros de inventario
    public List<Inventario> obtenerTodos() {
        logger.info("Consultando todos los registros de inventario en la base de datos");
        return inventarioRepository.findAll();
    }

    // Método para obtener un inventario por el ID del producto
    public Inventario obtenerPorProductoId(Long productoId) {
        logger.info("Buscando inventario para el Producto ID: {}", productoId);
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> {
                    logger.warn("No se encontró ningún registro de inventario para el Producto ID: {}", productoId);
                    return new RuntimeException("No se encontró inventario para el producto ID: " + productoId);
                });
    }

    // Método para guardar un nuevo registro de inventario
    public Inventario guardarInventario(Inventario inventario) {
        logger.info("Iniciando guardado de inventario para Producto ID: {}", inventario.getProductoId());
        logger.debug("Cantidad inicial a registrar: {}", inventario.getCantidad());
        
        Inventario inventarioGuardado = inventarioRepository.save(inventario);
        logger.debug("Inventario guardado en base de datos con ID: {}", inventarioGuardado.getId());
        
        return inventarioGuardado;
    }

    // Método para actualizar el stock de un producto después de una compra
    public Inventario actualizarStock(Long productoId, Integer cantidadComprada) {
        logger.info("Iniciando proceso de descuento de stock para Producto ID: {}", productoId);
        
        Inventario inventario = obtenerPorProductoId(productoId);
        logger.debug("Stock actual: {}. Cantidad a descontar: {}", inventario.getCantidad(), cantidadComprada);
        
        if (inventario.getCantidad() < cantidadComprada) {
            logger.error("Operación rechazada: Stock insuficiente. Stock actual ({}) es menor a lo solicitado ({})", 
                         inventario.getCantidad(), cantidadComprada);
            throw new RuntimeException("Stock insuficiente para el producto ID: " + productoId);
        }
        
        inventario.setCantidad(inventario.getCantidad() - cantidadComprada);
        logger.info("Actualizando inventario en la base de datos...");
        
        return inventarioRepository.save(inventario);
    }

    // Método para eliminar un inventario por el ID del producto
    public void eliminarPorProductoId(Long productoId) {
        logger.info("Eliminando inventario para Producto ID: {}", productoId);
        Inventario inventario = obtenerPorProductoId(productoId);
        inventarioRepository.delete(inventario);
        logger.info("Inventario eliminado satisfactoriamente para Producto ID: {}", productoId);
    }
}