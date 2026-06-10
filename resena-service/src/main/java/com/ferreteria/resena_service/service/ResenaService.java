package com.ferreteria.resena_service.service;

import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private static final Logger logger = LoggerFactory.getLogger(ResenaService.class);

    private final ResenaRepository resenaRepository;
    private final WebClient.Builder webClientBuilder;

    public record DetalleVentaDto(Long productoId) {}
    public record VentaDto(List<DetalleVentaDto> detalles) {}

    public Resena crearResena(Resena resena) {
        logger.info("Iniciando validaciones para crear reseña del Producto ID: {} por Usuario ID: {}", 
                resena.getIdProducto(), resena.getIdUsuario());

        // Verificar si el usuario ya reseñó este producto
        if (resenaRepository.existsByIdProductoAndIdUsuario(resena.getIdProducto(), resena.getIdUsuario())) {
            logger.warn("Reseña rechazada: El usuario {} ya reseñó el producto {}", resena.getIdUsuario(), resena.getIdProducto());
            throw new RuntimeException("Ya has publicado una reseña para este producto. Solo se permite una por cliente.");
        }

        // Validar que el Usuario existe
        try {
            webClientBuilder.build().get()
                    .uri("http://usuario-service/api/usuarios/" + resena.getIdUsuario())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error: El usuario ID {} no existe.", resena.getIdUsuario());
            throw new RuntimeException("El usuario no existe.");
        }

        // Validar que el Producto existe
        try {
            webClientBuilder.build().get()
                    .uri("http://catalogo-service/api/productos/" + resena.getIdProducto())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error: El producto ID {} no existe en el catálogo.", resena.getIdProducto());
            throw new RuntimeException("El producto no existe.");
        }

        logger.info("Verificando si el usuario compró el producto en venta-service...");
        try {
            VentaDto[] historialVentas = webClientBuilder.build().get()
                    .uri("http://venta-service/api/ventas/usuario/" + resena.getIdUsuario())
                    .retrieve()
                    .bodyToMono(VentaDto[].class)
                    .block();

            boolean productoComprado = false;
            if (historialVentas != null) {
                for (VentaDto venta : historialVentas) {
                    for (DetalleVentaDto detalle : venta.detalles()) {
                        if (detalle.productoId().equals(resena.getIdProducto())) {
                            productoComprado = true;
                            break;
                        }
                    }
                    if (productoComprado) break;
                }
            }

            if (!productoComprado) {
                logger.warn("Reseña rechazada: El usuario {} no ha comprado el producto {}", resena.getIdUsuario(), resena.getIdProducto());
                throw new RuntimeException("No puedes reseñar un producto que no has comprado.");
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al contactar a venta-service: {}", e.getMessage());
            throw new RuntimeException("Error al verificar el historial de compras del usuario.");
        }

        // Guardar la reseña si pasó todas las pruebas
        resena.setFechaResena(LocalDateTime.now());
        logger.info("Validaciones superadas. Guardando reseña en base de datos...");
        return resenaRepository.save(resena);
    }

    public List<Resena> obtenerResenasPorProducto(Long idProducto) {
        logger.info("Consultando reseñas para el producto ID: {}", idProducto);
        return resenaRepository.findByIdProducto(idProducto);
    }

    public double calcularPromedioProducto(Long idProducto) {
        List<Resena> resenas = obtenerResenasPorProducto(idProducto);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        double suma = resenas.stream().mapToInt(Resena::getCalificacion).sum();
        double promedio = suma / resenas.size();
        
        return Math.round(promedio * 10.0) / 10.0;
    }
}