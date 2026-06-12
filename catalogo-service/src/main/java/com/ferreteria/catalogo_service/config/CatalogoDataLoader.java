package com.ferreteria.catalogo_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j 
public class CatalogoDataLoader implements CommandLineRunner {

    // Cambiado a minúscula inicial (convención de Java)
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Verifica si la base de datos está vacía para no duplicar datos
        if (productoRepository.count() == 0) {
            log.info("Iniciando la carga de datos maestros en el Catálogo...");

            Producto prod1 = new Producto();
            prod1.setNombre("Taladro Inalámbrico 20V");
            prod1.setDescripcion("Taladro percutor con 2 baterías de litio");
            prod1.setPrecio(65000); 
            prod1.setMarca("Makita"); 

            Producto prod2 = new Producto();
            prod2.setNombre("Set Llaves Punta Corona");
            prod2.setDescripcion("Juego de 12 llaves métricas de acero cromo vanadio");
            prod2.setPrecio(18500); 
            prod2.setMarca("Stanley");

            Producto prod3 = new Producto();
            prod3.setNombre("Huincha de medir 5m");
            prod3.setDescripcion("Cinta métrica retráctil con freno automático");
            prod3.setPrecio(4500); 
            prod3.setMarca("Bosch");

            productoRepository.save(prod1);
            productoRepository.save(prod2);
            productoRepository.save(prod3);

            log.info("¡Datos de Catálogo cargados exitosamente de forma automática!");
        } else {
            log.info("El catálogo ya contiene herramientas. Omitiendo carga inicial.");
        }
    }
}