package com.ferreteria.auth_service.repository;

import com.ferreteria.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Método clave: Buscar un usuario por su correo
    User findByEmail(String email);
}