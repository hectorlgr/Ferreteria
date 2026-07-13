package com.ferreteria.promocion_service.config;

import com.ferreteria.promocion_service.model.Promocion;
import com.ferreteria.promocion_service.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromocionDataLoader implements CommandLineRunner {

    private final PromocionRepository promocionRepository;

    @Override
    public void run(String... args) throws Exception {

        if (promocionRepository.count() == 0) {
            log.info("Iniciando la carga de promociones por defecto...");

            Promocion promo1 = new Promocion();
            promo1.setCodigo("FERRETERIA10");
            promo1.setPorcentajeDescuento(10.0);
            promo1.setEstado(true);

            Promocion promo2 = new Promocion();
            promo2.setCodigo("DESCUENTO20");
            promo2.setPorcentajeDescuento(20.0);
            promo2.setEstado(true);

            Promocion promo3 = new Promocion();
            promo3.setCodigo("VERANO15");
            promo3.setPorcentajeDescuento(15.0);
            promo3.setEstado(true);

            Promocion promo4 = new Promocion();
            promo4.setCodigo("MITAD50");
            promo4.setPorcentajeDescuento(50.0);
            promo4.setEstado(true);

            promocionRepository.save(promo1);
            promocionRepository.save(promo2);
            promocionRepository.save(promo3);
            promocionRepository.save(promo4);

            log.info("¡Promociones cargadas exitosamente!");
        } else {
            log.info("La base de datos ya contiene promociones. Omitiendo carga inicial.");
        }
    }
}