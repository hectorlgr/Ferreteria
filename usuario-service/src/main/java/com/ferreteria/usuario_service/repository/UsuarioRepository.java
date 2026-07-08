package com.ferreteria.usuario_service.repository;

import com.ferreteria.usuario_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Metodo para buscar un usuario por email, lo cual es necesario para el proceso
    // de login
    Usuario findByEmail(String email);
}