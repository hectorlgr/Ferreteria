package com.ferreteria.usuario_service.service;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado con el ID " + id));
    }

    public Usuario guardarUsuario(Usuario usuario) {
        // Todo: Aquí implementaremos la encriptación de la contraseña antes de guardar
        // usando BCrypt, para cumplir con los requisitos de seguridad.
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario detallesUsuario) {
        Usuario usuarioExistente = obtenerPorId(id);
        
        usuarioExistente.setNombre(detallesUsuario.getNombre());
        usuarioExistente.setApellido(detallesUsuario.getApellido());
        usuarioExistente.setEmail(detallesUsuario.getEmail());
        usuarioExistente.setRol(detallesUsuario.getRol());
        
        // Si el usuario envía una nueva contraseña, habría que encriptarla de nuevo.
        // Por ahora, solo actualizamos si viene con datos.
        if (detallesUsuario.getPassword() != null && !detallesUsuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(detallesUsuario.getPassword());
        }
        
        return usuarioRepository.save(usuarioExistente);
    }

    public void eliminarUsuario(Long id) {
        Usuario usuarioExistente = obtenerPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }
}