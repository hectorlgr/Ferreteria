package com.ferreteria.usuario_service.service;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    // Método para obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        logger.info("Consultando todos los usuarios en la base de datos");
        return usuarioRepository.findAll();
    }

    // Método para obtener un usuario por su ID
    public Usuario obtenerPorId(Long id) {
        logger.info("Buscando usuario en base de datos con ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró ningún usuario con el ID: {}", id);
                    return new RuntimeException("Usuario no encontrado con ID: " + id);
                });
    }

    // Metodo para obtener un usuario por su email
    public Usuario obtenerPorEmail(String email) {
        logger.info("Buscando usuario en base de datos con email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            logger.warn("Búsqueda fallida: No se encontró ningún usuario con el email: {}", email);
            throw new RuntimeException("Error: Usuario no encontrado con el email " + email);
        }
        return usuario;
    }

    // Método para guardar un nuevo usuario
    public Usuario guardarUsuario(Usuario usuario) {
        logger.info("Iniciando guardado de nuevo usuario. Email: {}", usuario.getEmail());
        
        logger.debug("Guardando usuario en la base de datos...");
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.debug("Usuario guardado con ID interno: {}", usuarioGuardado.getId());
        
        return usuarioGuardado;
    }

    // Método para actualizar un usuario existente
    public Usuario actualizarUsuario(Long id, Usuario detallesUsuario) {
        logger.info("Iniciando actualización de datos para el usuario ID: {}", id);
        
        Usuario usuarioExistente = obtenerPorId(id);
        
        logger.debug("Aplicando nuevos datos: Nombre={}, Email={}", 
                detallesUsuario.getNombre(), detallesUsuario.getEmail());
                
        usuarioExistente.setNombre(detallesUsuario.getNombre());
        usuarioExistente.setEmail(detallesUsuario.getEmail());
        
        logger.info("Guardando usuario actualizado en la base de datos...");
        return usuarioRepository.save(usuarioExistente);
    }

    // Método para eliminar un usuario por su ID
    public void eliminarUsuario(Long id) {
        logger.info("Iniciando proceso de eliminación para el usuario ID: {}", id);
        
        Usuario usuarioExistente = obtenerPorId(id);
        
        logger.debug("Procediendo a eliminar el usuario de la base de datos...");
        usuarioRepository.delete(usuarioExistente);
    }
}