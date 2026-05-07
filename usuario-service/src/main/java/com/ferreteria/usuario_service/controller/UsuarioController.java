package com.ferreteria.usuario_service.controller;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // 1. Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    // GET: Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodos() {
        logger.info("GET /api/usuarios - Solicitud para listar todos los usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        logger.debug("Cantidad de usuarios obtenidos: {}", usuarios.size());
        return usuarios;
    }

    // GET: Obtener un usuario por ID
    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/usuarios/{} - Solicitud para obtener usuario por ID", id);
        return usuarioService.obtenerPorId(id);
    }

    // POST: Crear un nuevo usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve 201 Created
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        logger.info("POST /api/usuarios - Solicitud para registrar un nuevo usuario: {}", usuario.getEmail());
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", nuevoUsuario.getId());
        return nuevoUsuario;
    }

    // PUT: Actualizar un usuario existente
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        logger.info("PUT /api/usuarios/{} - Solicitud para actualizar datos del usuario", id);
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        logger.info("Usuario ID {} actualizado correctamente", id);
        return usuarioActualizado;
    }

    // DELETE: Eliminar un usuario
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve 204 No Content
    public void eliminarUsuario(@PathVariable Long id) {
        logger.info("DELETE /api/usuarios/{} - Solicitud para eliminar usuario", id);
        usuarioService.eliminarUsuario(id);
        logger.info("Usuario ID {} eliminado exitosamente", id);
    }
}