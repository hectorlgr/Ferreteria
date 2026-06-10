package com.ferreteria.catalogo_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ferreteria.catalogo_service.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Devuelve solo los productos que tienen habilitado = true
    List<Producto> findByHabilitadoTrue();

    // Buscar por ID pero solo si está habilitado
    java.util.Optional<Producto> findByIdAndHabilitadoTrue(Long id);
}