package com.ferreteria.inventario_service.repository;


import com.ferreteria.inventario_service.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    // Metodo para buscar un inventario por el ID del producto
    Optional<Inventario> findByProductoId(Long productoId);
}