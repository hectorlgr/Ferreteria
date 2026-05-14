package com.ferreteria.usuario_service.controller;

import com.ferreteria.usuario_service.Dto.UsuarioRequestDto;
import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    // GET: Obtener todos los usuarios
    // http://localhost:9090/api/usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        logger.info("GET /api/usuarios - Solicitud para listar todos los usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        logger.debug("Cantidad de usuarios obtenidos: {}", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    // GET: Obtener un usuario por ID
    // http://localhost:9090/api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/usuarios/{} - Solicitud para obtener usuario por ID", id);
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // POST: Crear un nuevo usuario
    // http://localhost:9090/api/usuarios
    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@Valid @RequestBody UsuarioRequestDto dto) {
        logger.info("POST /api/usuarios - Solicitud para registrar un nuevo usuario: {}", dto.getEmail());
        
        // Convertir DTO a Entidad para no romper el servicio
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", nuevoUsuario.getId());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // PUT: Actualizar un usuario existente
    // http://localhost:9090/api/usuarios/{id} 
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDto dto) {
        logger.info("PUT /api/usuarios/{} - Solicitud para actualizar datos del usuario", id);
        
        // Convertir DTO a Entidad para no romper el servicio
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        logger.info("Usuario ID {} actualizado correctamente", id);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // DELETE: Eliminar un usuario
    // http://localhost:9090/api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        logger.info("DELETE /api/usuarios/{} - Solicitud para eliminar usuario", id);
        usuarioService.eliminarUsuario(id);
        logger.info("Usuario ID {} eliminado exitosamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Devuelve 204 No Content
    }
}ackage com.ferreteria.usuario_service.controller;

import com.ferreteria.usuario_service.dto.UsuarioRequestDTO;import com.ferreteria.usuario_service.service.UsuarioService;
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

    // Declarar el Logger
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    // GET: Obtener todos los usuarios
    // http://localhost:9090/api/usuarios
    @GetMapping
    private final UsuarioService usuarioService   logger.info("GET /api/usuarios - Solicitud para listar todos los usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        logger.debug("Cantidad de usuarios obtenidos: {}", usuarios.size());
        return usuarios;
    }

    // GET: Obtener un usuario por ID
    // http://localhost:9090/api/usuarios/{id}
    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/usuarios/{} - Solicitud para obtener usuario por ID", id);
        return usuarioService.obtenerPorId(id);
    }

    // POST: Crear un nuevo usuario
    // http://localhost:9090/api/usuarios
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        logger.info("POST /api/usuarios - Solicitud para registrar un nuevo usuario: {}", usuario.getEmail());
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", nuevoUsuario.getId());
        return nuevoUsuario;
    }

    // PUT: Actualizar un usuario existente
    /tohttp://localhost:9090/api/uTOarios/{id} 
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        logger.info("PUT /api/usuarios/{} - Solicitud para actualizar datos del usuario", id);
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        logger.info("Usuario ID {} actualizado correctamente", id);
        return usuarioActualizado;
    }

    // DELETE: Eliminar un usuario
    // http://localhost:9090/api/usuarios/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve 204 No Content
    public void eliminarUsuario(@PathVariable Long id) {
        logger.info("DELETE /api/usuarios/{} - Solicitto para eliminar usuario", id);
        usuarioService.elTOinarUsuario(id);
        logger.info("Usuario ID {} eliminado exitosamente", id);
    }
}