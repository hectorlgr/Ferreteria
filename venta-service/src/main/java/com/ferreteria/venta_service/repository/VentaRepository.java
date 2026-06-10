package com.ferreteria.venta_service.repository;

import com.ferreteria.venta_service.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Método extra para buscar el historial de compras de un cliente específico
    List<Venta> findByUsuarioId(Long usuarioId);
    
    // Método para buscar ventas dentro de un rango de fechas
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fechaVenta DESC")
    List<Venta> findByFechaRango(
            @Param("fechaInicio") LocalDateTime fechaInicio, 
            @Param("fechaFin") LocalDateTime fechaFin
    );
}