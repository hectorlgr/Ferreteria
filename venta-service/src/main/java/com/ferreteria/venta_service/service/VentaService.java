package com.ferreteria.venta_service.service;

import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;
    private final WebClient.Builder webClientBuilder;

    // Método para obtener todas las ventas
    public List<Venta> obtenerTodas() {
        logger.info("Listando todas las ventas desde la base de datos");
        return ventaRepository.findAll();
    }
    
    // Método para obtener ventas por ID de usuario
    public List<Venta> obtenerPorUsuario(Long usuarioId) {
        logger.info("Buscando ventas asociadas al usuario ID: {}", usuarioId);
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    // Método para procesar una nueva venta
    public Venta procesarVenta(Venta venta) {
        logger.info("Iniciando procesamiento de venta para Usuario ID: {}", venta.getUsuarioId());

        // 1. Verificar si el usuario existe
        try {
            logger.debug("Validando existencia de usuario en usuario-service (Puerto 9092)");
            webClientBuilder.build().get()
                    .uri("http://localhost:9092/api/usuarios/" + venta.getUsuarioId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            logger.debug("Validación exitosa: Usuario ID {} existe.", venta.getUsuarioId());
        } catch (Exception e) {
            logger.error("Error al validar usuario ID: {}. Excepción: {}", venta.getUsuarioId(), e.getMessage());
            throw new RuntimeException("Error: El usuario no existe o el servicio de usuarios está caído.");
        }

        // 2. Preparar los datos internos de la venta
        logger.debug("Calculando totales y enlazando detalles de la venta...");
        venta.setFecha(LocalDateTime.now());
        int totalVenta = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
            totalVenta += detalle.getSubtotal();
        }
        venta.setTotal(totalVenta);
        logger.debug("Cálculos finalizados. Total calculado: {}", totalVenta);

        // 3. Guardar la venta en la db
        logger.info("Guardando datos de la venta en base de datos...");
        Venta ventaGuardada = ventaRepository.save(venta);
        logger.debug("Venta guardada temporalmente con ID: {}", ventaGuardada.getId());

        // 4. Descontar el stock en el inventario
        logger.info("Iniciando actualización de stock en inventario-service (Puerto 9093) para {} productos", venta.getDetalles().size());
        for (DetalleVenta detalle : venta.getDetalles()) {
            try {
                logger.debug("Descontando {} unidades del Producto ID: {}", detalle.getCantidad(), detalle.getProductoId());
                webClientBuilder.build().put()
                        .uri("http://localhost:9093/api/inventario/producto/" + 
                             detalle.getProductoId() + "/descontar?cantidad=" + detalle.getCantidad())
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
            } catch (Exception e) {
                logger.error("Error crítico al descontar stock del Producto ID: {}. Excepción: {}", detalle.getProductoId(), e.getMessage());
                // En un sistema avanzado haríamos un "Rollback", pero por ahora lanzamos la alerta
                throw new RuntimeException("Error descontando stock del producto ID: " + detalle.getProductoId());
            }
        }

        logger.info("Procesamiento de venta finalizado exitosamente. ID final: {}", ventaGuardada.getId());
        return ventaGuardada;
    }

    // Método para obtener una venta por ID
    public Venta obtenerPorId(Long id) {
        logger.info("Buscando venta con ID: {}", id);
        return ventaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se encontró ninguna venta con el ID: {}", id);
                    return new RuntimeException("Venta no encontrada con ID: " + id);
                });
    }

    // Método para actualizar una venta existente
    public Venta actualizarVenta(Long id, Venta ventaActualizada) {
        logger.info("Iniciando actualización de venta con ID: {}", id);
        
        Venta venta = obtenerPorId(id);
        logger.debug("Venta encontrada. Aplicando cambios...");
        
        venta.setUsuarioId(ventaActualizada.getUsuarioId());
        venta.setTotal(ventaActualizada.getTotal());
        
        logger.info("Guardando cambios de venta en base de datos...");
        Venta ventaGuardada = ventaRepository.save(venta);
        logger.info("Venta ID {} actualizada correctamente", id);
        
        return ventaGuardada;
    }

    // Método para eliminar una venta
    public void eliminarVenta(Long id) {
        logger.info("Iniciando eliminación de venta con ID: {}", id);
        
        Venta venta = obtenerPorId(id);
        logger.debug("Venta encontrada. Procediendo a eliminar...");
        
        ventaRepository.delete(venta);
        logger.info("Venta ID {} eliminada correctamente", id);
    }

    // Método para obtener ventas por rango de fechas
    public List<Venta> obtenerPorRangoFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
    logger.info("Procesando rango de fechas: {} al {}", inicio, fin);

    if (inicio.isAfter(fin)) {
        throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    // Convertimos LocalDate a LocalDateTime para la consulta en la BD
    java.time.LocalDateTime fechaInicioCompleta = inicio.atStartOfDay(); // 00:00:00
    java.time.LocalDateTime fechaFinCompleta = fin.atTime(java.time.LocalTime.MAX); // 23:59:59.999

    logger.debug("Buscando en BD entre {} y {}", fechaInicioCompleta, fechaFinCompleta);
    
    return ventaRepository.findByFechaRango(fechaInicioCompleta, fechaFinCompleta);
}
}