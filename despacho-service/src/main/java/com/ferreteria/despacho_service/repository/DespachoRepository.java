package com.ferreteria.despacho_service.repository;

import com.ferreteria.despacho_service.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    // Encontrar por Pedido en lugar de Venta
    Optional<Despacho> findByPedidoId(Long pedidoId);

    Optional<Despacho> findByEstado(String estado);
}