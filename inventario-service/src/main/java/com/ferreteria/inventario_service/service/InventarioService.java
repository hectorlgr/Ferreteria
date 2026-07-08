package com.ferreteria.inventario_service.service;

import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;
import com.ferreteria.inventario_service.exception.ResourceNotFoundException;
import com.ferreteria.inventario_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;
    private final WebClient.Builder webClientBuilder;

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
                    return new ResourceNotFoundException(
                            "No se encontró inventario para el producto ID: " + productoId);
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
            throw new BadRequestException("Stock insuficiente para el producto ID: " + productoId);
        }

        // Descontar el stock
        int nuevoStock = inventario.getCantidad() - cantidadComprada;
        inventario.setCantidad(nuevoStock);

        // Guardar en la base de datos de inventario
        logger.info("Actualizando inventario en la base de datos...");
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        // Validar si llegó a cero
        if (nuevoStock == 0) {
            logger.info("El stock del producto ID: {} llegó a CERO. Notificando a catalogo-service...", productoId);
            try {
                webClientBuilder.build().put()
                        .uri("http://catalogo-service/api/productos/" + productoId + "/agotar")
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                logger.info("Catalogo-service notificado con éxito. Producto deshabilitado.");
            } catch (Exception e) {
                logger.error("No se pudo contactar al catalogo-service para agotar el producto: {}", e.getMessage());
                throw new BadRequestException(
                        "El stock llegó a cero, pero falló la notificación al catálogo: " + e.getMessage());
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
            throw new BadRequestException("La cantidad a ingresar debe ser mayor a cero");
        }

        Inventario inventario = obtenerPorProductoId(productoId);
        logger.debug("Stock actual: {}. Cantidad a ingresar: {}", inventario.getCantidad(), cantidadAgregada);

        // Sumar el stock
        int nuevoStock = inventario.getCantidad() + cantidadAgregada;
        inventario.setCantidad(nuevoStock);

        // Guardar en la base de datos de inventario
        logger.info("Actualizando inventario en la base de datos...");
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        // Si el stock subió (y por ende es mayor a 0), el catálogo habilita el producto
        // automáticamente (en caso de que estuviera deshabilitado por falta de stock)
        if (nuevoStock > 0) {
            logger.info("El stock del producto ID: {} subió a {}. Notificando a catalogo-service...", productoId,
                    nuevoStock);
            try {
                webClientBuilder.build().put()
                        .uri("http://catalogo-service/api/productos/" + productoId + "/habilitar")
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                logger.info("Catalogo-service notificado con éxito. Producto reactivado.");
            } catch (Exception e) {
                logger.error("No se pudo contactar al catalogo-service para habilitar el producto: {}", e.getMessage());
                throw new BadRequestException(
                        "Stock ingresado, pero falló la reactivación del producto en catálogo: " + e.getMessage());
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