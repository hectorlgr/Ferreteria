package com.ferreteria.promocion_service.repository;

import com.ferreteria.promocion_service.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Optional<Promocion> findByCodigoAndEstadoTrue(String codigo);
}