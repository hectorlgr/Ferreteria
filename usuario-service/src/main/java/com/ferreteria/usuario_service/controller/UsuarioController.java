package com.ferreteria.usuario_service.controller;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET: Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // GET: Obtener un usuario por ID
    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    // POST: Crear un nuevo usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve 201 Created
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    // PUT: Actualizar un usuario existente
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    // DELETE: Eliminar un usuario
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve 204 No Content
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}