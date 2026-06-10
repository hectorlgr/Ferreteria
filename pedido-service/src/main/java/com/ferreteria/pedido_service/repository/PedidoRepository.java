package com.ferreteria.pedido_service.repository;

import com.ferreteria.pedido_service.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    
    List<Pedido> findByIdUsuario(Long idUsuario);

    // Para uso interno de los microservicios
    Optional<Pedido> findByIdVenta(Long idVenta);
}