package com.ferreteria.inventario_service.repository;


import com.ferreteria.inventario_service.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    // Es vital poder buscar el stock usando el ID del producto, no el ID del inventario
    Optional<Inventario> findByProductoId(Long productoId);
}