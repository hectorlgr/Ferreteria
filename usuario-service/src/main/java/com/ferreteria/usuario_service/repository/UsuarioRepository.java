package com.ferreteria.usuario_service.repository;

import com.ferreteria.usuario_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Más adelante, para el login, podríamos necesitar buscar al usuario por su email.
    // Spring Data JPA es tan inteligente que solo con escribir esta línea, 
    // crea la consulta SQL automáticamente:
    Usuario findByEmail(String email);
}