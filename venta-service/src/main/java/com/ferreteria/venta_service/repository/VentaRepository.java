package com.ferreteria.venta_service.repository;

import com.ferreteria.venta_service.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Método extra para buscar el historial de compras de un cliente específico
    List<Venta> findByUsuarioId(Long usuarioId);
}