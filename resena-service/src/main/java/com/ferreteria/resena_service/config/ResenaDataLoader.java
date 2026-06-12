package com.ferreteria.resena_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.repository.ResenaRepository;

@Component
@RequiredArgsConstructor
@Slf4j 
public class ResenaDataLoader implements CommandLineRunner {

    private final ResenaRepository resenaRepository;

    @Override
    public void run(String... args) throws Exception {
        
        if (resenaRepository.count() == 0) {
            log.info("Iniciando la carga de reseñas por defecto...");

            Resena resena1 = new Resena();
            resena1.setIdProducto(1L); 
            resena1.setIdUsuario(2L);  
            resena1.setCalificacion(5);
            resena1.setComentario("Excelente taladro, perfora concreto sin problemas.");

            Resena resena2 = new Resena();
            resena2.setIdProducto(2L); 
            resena2.setIdUsuario(2L);  
            resena2.setCalificacion(4);
            resena2.setComentario("Buenas llaves, pero la caja llegó un poco abollada.");

            resenaRepository.save(resena1);
            resenaRepository.save(resena2);

            log.info("¡Reseñas cargadas exitosamente!");
        } else {
            log.info("La base de datos ya contiene reseñas. Omitiendo carga inicial.");
        }
    }
}