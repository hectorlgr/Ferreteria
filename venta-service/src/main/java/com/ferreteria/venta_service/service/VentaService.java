package com.ferreteria.venta_service.service;

import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final WebClient.Builder webClientBuilder;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }
    
    public List<Venta> obtenerPorUsuario(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    public Venta procesarVenta(Venta venta) {
        // 1. Verificar si el usuario existe (Llamada HTTP a usuario-service 9092)
        try {
            webClientBuilder.build().get()
                    .uri("http://localhost:9092/api/usuarios/" + venta.getUsuarioId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block(); // block() lo hace síncrono para esperar la respuesta
        } catch (Exception e) {
            throw new RuntimeException("Error: El usuario no existe o el servicio de usuarios está caído.");
        }

        // 2. Preparar los datos internos de la venta
        venta.setFecha(LocalDateTime.now());
        int totalVenta = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta); // Enlazamos el detalle con la cabecera
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
            totalVenta += detalle.getSubtotal();
        }
        venta.setTotal(totalVenta);

        // 3. Guardar la venta en nuestra base de datos (db_venta)
        Venta ventaGuardada = ventaRepository.save(venta);

        // 4. Descontar el stock en el inventario (Llamada HTTP a inventario-service 9093)
        for (DetalleVenta detalle : venta.getDetalles()) {
            try {
                webClientBuilder.build().put()
                        .uri("http://localhost:9093/api/inventario/producto/" + 
                             detalle.getProductoId() + "/descontar?cantidad=" + detalle.getCantidad())
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
            } catch (Exception e) {
                // En un sistema avanzado haríamos un "Rollback", pero por ahora lanzamos la alerta
                throw new RuntimeException("Error descontando stock del producto ID: " + detalle.getProductoId());
            }
        }

        return ventaGuardada;
    }
}