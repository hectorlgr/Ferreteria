package com.ferreteria.resena_service.repository;

import com.ferreteria.resena_service.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Obtener todas las reseñas de un producto específico
    List<Resena> findByIdProducto(Long idProducto);

    // Obtener todas las reseñas dejadas por un usuario específico
    List<Resena> findByIdUsuario(Long idUsuario);

    // Verificar si ya existe una reseña de este usuario para este producto
    boolean existsByIdProductoAndIdUsuario(Long idProducto, Long idUsuario);
}