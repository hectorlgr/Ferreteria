package com.ferreteria.promocion_service.service;

import com.ferreteria.promocion_service.model.Promocion;
import com.ferreteria.promocion_service.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocionService {

    private static final Logger logger = LoggerFactory.getLogger(PromocionService.class);
    private final PromocionRepository promocionRepository;

    // Crear una nueva promoción
    public Promocion crearPromocion(Promocion promocion) {
        promocion.setCodigo(promocion.getCodigo().toUpperCase());
        logger.info("Creando nueva promoción con código: {}", promocion.getCodigo());
        return promocionRepository.save(promocion);
    }

    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    public Double validarYObtenerDescuento(String codigo) {
        logger.info("Validando cupón de descuento: {}", codigo);
        
        Promocion promocion = promocionRepository.findByCodigoAndEstadoTrue(codigo.toUpperCase())
                .orElseThrow(() -> {
                    logger.warn("Intento de uso de cupón inválido o inactivo: {}", codigo);
                    return new RuntimeException("El código de descuento no existe o no está activo en este momento.");
                });
                
        logger.info("Cupón {} validado exitosamente. Descuento: {}%", promocion.getCodigo(), promocion.getPorcentajeDescuento());
        return promocion.getPorcentajeDescuento();
    }

    public Promocion activarPromocion(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        
        promocion.setEstado(true);
        logger.info("Promoción ID {} ACTIVADA manualmente.", id);
        return promocionRepository.save(promocion);
    }

    public Promocion desactivarPromocion(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        
        promocion.setEstado(false);
        logger.info("Promoción ID {} DESACTIVADA manualmente.", id);
        return promocionRepository.save(promocion);
    }
}