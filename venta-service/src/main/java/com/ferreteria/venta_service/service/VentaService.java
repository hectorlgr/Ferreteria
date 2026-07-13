package com.ferreteria.venta_service.service;

import com.ferreteria.venta_service.Dto.UsuarioDto;
import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.repository.VentaRepository;
import com.ferreteria.venta_service.exception.ResourceNotFoundException;
import com.ferreteria.venta_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Venta procesarVenta(Venta venta, String direccion, String codigoPromocion) {
        logger.info("Iniciando procesamiento de venta para Usuario ID: {}", venta.getUsuarioId());

        // Verificar si el usuario existe
        try {
            logger.debug("Validando existencia de usuario en usuario-service (Puerto 9092)");
            webClientBuilder.build().get()
                    .uri("http://usuario-service/api/usuarios/" + venta.getUsuarioId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            logger.debug("Validación exitosa: Usuario ID {} existe.", venta.getUsuarioId());
        } catch (Exception e) {
            logger.error("Error al validar usuario ID: {}. Excepción: {}", venta.getUsuarioId(), e.getMessage());
            throw new ResourceNotFoundException("Error: El usuario no existe o el servicio de usuarios está caído.");
        }

        logger.debug("Calculando totales y enlazando detalles de la venta...");
        venta.setFechaVenta(LocalDateTime.now());

        // Sumar el subtotal neto de todos los productos
        int subtotalNetoProductos = 0;
        int costoDespachoIngresado = venta.getCostoDespacho() != null ? venta.getCostoDespacho() : 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
            subtotalNetoProductos += detalle.getSubtotal();
        }

        // Procesar Cupón de Descuento (Si existe)
        double porcentajeDescuento = 0.0;
        if (codigoPromocion != null && !codigoPromocion.trim().isEmpty()) {
            logger.info("Procesando cupón de descuento: {}", codigoPromocion);
            try {
                java.util.Map<?, ?> respuestaPromocion = webClientBuilder.build().get()
                        .uri("http://promocion-service/api/promociones/validar/" + codigoPromocion.trim())
                        .retrieve()
                        .bodyToMono(java.util.Map.class)
                        .block();

                if (respuestaPromocion != null && respuestaPromocion.containsKey("descuento")) {
                    porcentajeDescuento = (Double) respuestaPromocion.get("descuento");
                    logger.info("Cupón verificado con éxito. Descuento a aplicar: {}%", porcentajeDescuento);
                }
            } catch (Exception e) {
                logger.error("El cupón fue rechazado por el sistema de promociones: {}", e.getMessage());
                throw new BadRequestException(
                        "Error en la compra: El cupón es inválido o el sistema de promociones no responde.");
            }
        }

        // Aplicar Descuento al Subtotal Neto
        if (porcentajeDescuento > 0.0) {
            double descuentoDinero = subtotalNetoProductos * (porcentajeDescuento / 100.0);
            subtotalNetoProductos = (int) (subtotalNetoProductos - descuentoDinero);
            logger.info("Descuento aplicado. Subtotal Neto rebajado: ${}", subtotalNetoProductos);
        }

        // LOGICA NEGOCIO: CALCULO DEL IVA (19%)
        double iva = subtotalNetoProductos * 0.19;
        int totalConIva = (int) (subtotalNetoProductos + iva);
        logger.info("IVA (19%) calculado: ${}. Total productos con IVA: ${}", (int) iva, totalConIva);

        // Sumar Despacho al Total Final
        int totalVenta = totalConIva + costoDespachoIngresado;

        venta.setTotal(totalVenta);
        logger.debug("Cálculos finalizados. Total calculado: {} (incluye despacho de {})", totalVenta,
                costoDespachoIngresado);

        // Guardar la venta en la db
        logger.info("Guardando datos de la venta en base de datos...");
        Venta ventaGuardada = ventaRepository.save(venta);
        logger.debug("Venta guardada temporalmente con ID: {}", ventaGuardada.getId());

        // Descontar el stock en el inventario
        logger.info("Iniciando actualización de stock en inventario-service para {} productos",
                venta.getDetalles().size());
        for (DetalleVenta detalle : venta.getDetalles()) {
            try {
                logger.debug("Descontando {} unidades del Producto ID: {}", detalle.getCantidad(),
                        detalle.getProductoId());
                webClientBuilder.build().put()
                        .uri("http://inventario-service/api/inventario/producto/" + detalle.getProductoId()
                                + "/descontar?cantidad=" + detalle.getCantidad())
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
            } catch (Exception e) {
                logger.error("Error crítico al descontar stock del Producto ID: {}. Excepción: {}",
                        detalle.getProductoId(), e.getMessage());
                throw new BadRequestException("Error descontando stock del producto ID: " + detalle.getProductoId()
                        + ". Puede que no haya stock suficiente.");
            }
        }

        // 8. Notificar a pedido-service (Mantenemos tu lógica intacta)
        logger.info("Notificando a pedido-service para orquestar la logística...");
        try {
            java.util.Map<String, Object> pedidoPayload = new java.util.HashMap<>();
            pedidoPayload.put("idUsuario", ventaGuardada.getUsuarioId());
            pedidoPayload.put("idVenta", ventaGuardada.getId());
            pedidoPayload.put("direccion", direccion);

            webClientBuilder.build().post()
                    .uri("http://pedido-service/api/pedidos")
                    .bodyValue(pedidoPayload)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            logger.info("Pedido orquestado exitosamente en pedido-service.");
        } catch (Exception e) {
            logger.error("Error al crear el pedido en pedido-service: {}", e.getMessage());
            throw new BadRequestException(
                    "La venta se procesó en bodega, pero falló la creación logística del pedido: " + e.getMessage());
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
                    return new ResourceNotFoundException("Venta no encontrada con ID: " + id);
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
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // Convertir LocalDate a LocalDateTime para la consulta en la BD
        java.time.LocalDateTime fechaInicioCompleta = inicio.atStartOfDay(); // 00:00:00
        java.time.LocalDateTime fechaFinCompleta = fin.atTime(java.time.LocalTime.MAX); // 23:59:59.999

        logger.debug("Buscando en BD entre {} y {}", fechaInicioCompleta, fechaFinCompleta);

        return ventaRepository.findByFechaRango(fechaInicioCompleta, fechaFinCompleta);
    }

    public List<Venta> obtenerVentasPorEmailUsuario(String email) {
        logger.info("Iniciando búsqueda de ventas para el email: {}", email);
        Long idClienteObtenido;

        try {
            UsuarioDto usuario = webClientBuilder.build().get()
                    .uri("http://usuario-service/api/usuarios/email/" + email)
                    .retrieve()
                    .bodyToMono(UsuarioDto.class)
                    .block();

            idClienteObtenido = usuario.getId();
            logger.info("Usuario encontrado en usuario-service. ID mapeado: {}", idClienteObtenido);

        } catch (Exception e) {
            logger.error("Error al contactar a usuario-service o el email no existe: {}", email);
            throw new ResourceNotFoundException(
                    "No se encontró al usuario con el email " + email + " para buscar sus ventas.");
        }
        logger.info("Buscando las ventas asociadas al ID de cliente: {}", idClienteObtenido);
        return ventaRepository.findByUsuarioId(idClienteObtenido);
    }
}