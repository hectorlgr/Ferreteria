package com.ferreteria.usuario_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.usuario_service.Dto.UsuarioRequestDTO;
import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> obtenerTodos() {
        logger.info("GET /api/usuarios - Solicitud para listar todos los usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        
        List<EntityModel<Usuario>> usuariosModel = usuarios.stream()
            .map(usuario -> EntityModel.of(usuario,
                linkTo(methodOn(this.getClass()).obtenerPorId(usuario.getId())).withSelfRel()))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        logger.debug("Cantidad de usuarios obtenidos: {}", usuarios.size());
        return ResponseEntity.ok(CollectionModel.of(usuariosModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener un usuario por ID
    // http://localhost:9090/api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/usuarios/{} - Solicitud para obtener usuario por ID", id);
        Usuario usuario = usuarioService.obtenerPorId(id);
        
        EntityModel<Usuario> recurso = EntityModel.of(usuario);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorId(id));
        WebMvcLinkBuilder linkTodos = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodos.withRel("todos-los-usuarios"));
        
        return ResponseEntity.ok(recurso);
    }

    // GET: Obtener un usuario por email
    // http://localhost:9090/api/usuarios/email/{email} 
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorEmail(@PathVariable String email) {
        logger.info("GET /api/usuarios/email/{} - Solicitud para obtener usuario por email", email);
        Usuario usuario = usuarioService.obtenerPorEmail(email);
        
        EntityModel<Usuario> recurso = EntityModel.of(usuario);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorEmail(email));
        WebMvcLinkBuilder linkTodos = linkTo(methodOn(this.getClass()).obtenerTodos());
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkTodos.withRel("todos-los-usuarios"));
        
        return ResponseEntity.ok(recurso);
    }

    // POST: Crear un nuevo usuario
    // http://localhost:9090/api/usuarios
    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
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
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
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
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}