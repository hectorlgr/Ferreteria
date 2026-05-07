package com.ferreteria.despacho_service.repository;

import com.ferreteria.despacho_service.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    Optional<Despacho> findByVentaId(Long ventaId);
    Optional<Despacho> findByEstado(String estado);

}