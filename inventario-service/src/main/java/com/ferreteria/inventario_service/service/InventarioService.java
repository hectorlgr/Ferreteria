package com.ferreteria.inventario_service.service;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    @Autowired
    private RestTemplate restTemplate;

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
        
        // 1. Descontamos el stock
        int nuevoStock = inventario.getCantidad() - cantidadComprada;
        inventario.setCantidad(nuevoStock);
        
        // 2. Guardamos en la base de datos de inventario
        logger.info("Actualizando inventario en la base de datos...");
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        // 3. Validamos si llegó a cero
        if (nuevoStock == 0) {
            logger.info("El stock del producto ID: {} llegó a CERO. Notificando a catalogo-service...", productoId);
            try {
                // IMPORTANTE: Ajusta el puerto (ej: 9091) al que usa tu catalogo-service
                String url = "http://localhost:9091/api/productos/" + productoId + "/agotar";
                restTemplate.put(url, null);
                logger.info("Catalogo-service notificado con éxito. Producto deshabilitado.");
            } catch (Exception e) {
                // Solo logueamos el error para que la compra no se cancele si el catálogo falla un milisegundo
                logger.error("No se pudo contactar al catalogo-service para agotar el producto: {}", e.getMessage());
            }
        }
        return inventarioGuardado;
    }

    // Método para ingresar stock de un producto (Ej: cuando llega mercancía nueva)
    public Inventario agregarStock(Long productoId, Integer cantidadAgregada) {
        logger.info("Iniciando proceso de ingreso de stock para Producto ID: {}", productoId);

        // Validación básica de seguridad
        if (cantidadAgregada <= 0) {
            logger.error("Operación rechazada: La cantidad a agregar ({}) no es válida", cantidadAgregada);
            throw new RuntimeException("La cantidad a ingresar debe ser mayor a cero");
        }

        Inventario inventario = obtenerPorProductoId(productoId);
        logger.debug("Stock actual: {}. Cantidad a ingresar: {}", inventario.getCantidad(), cantidadAgregada);

        // 1. Sumamos el stock
        int nuevoStock = inventario.getCantidad() + cantidadAgregada;
        inventario.setCantidad(nuevoStock);

        // 2. Guardamos en la base de datos de inventario
        logger.info("Actualizando inventario en la base de datos...");
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        // 3. Si el stock subió (y por ende es mayor a 0), le pedimos al catálogo que despierte el producto
        if (nuevoStock > 0) {
            logger.info("El stock del producto ID: {} subió a {}. Notificando a catalogo-service...", productoId, nuevoStock);
            try {
                // IMPORTANTE: Revisa que el puerto (ej: 9091) sea el de tu catalogo-service
                String url = "http://localhost:9091/api/productos/" + productoId + "/habilitar";
                restTemplate.put(url, null);
                logger.info("Catalogo-service notificado con éxito. Producto reactivado.");
            } catch (Exception e) {
                // Logueamos el error sin detener el flujo, ya que el stock sí se guardó en la BD
                logger.error("No se pudo contactar al catalogo-service para habilitar el producto: {}", e.getMessage());
            }
        }

        return inventarioGuardado;
    }

    // Método para eliminar un inventario por el ID del producto
    public void eliminarPorProductoId(Long productoId) {
        logger.info("Eliminando inventario para Producto ID: {}", productoId);
        Inventario inventario = obtenerPorProductoId(productoId);
        inventarioRepository.delete(inventario);
        logger.info("Inventario eliminado satisfactoriamente para Producto ID: {}", productoId);
    }

    // Método para forzar el stock a cero cuando se deshabilita del catálogo
    public void resetearStock(Long productoId) {
        logger.info("Reseteando stock a cero para Producto ID: {}", productoId);
        Inventario inventario = obtenerPorProductoId(productoId);
        inventario.setCantidad(0);
        inventarioRepository.save(inventario);
    }
}