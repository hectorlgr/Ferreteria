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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios", description = "API para la administración y consulta de perfiles de usuarios del sistema")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    // GET: Obtener todos los usuarios
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista completa de los usuarios registrados con enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
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
    @Operation(summary = "Buscar usuario por ID", description = "Retorna los detalles de un único usuario basado en su identificador numérico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario localizado correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "El usuario no fue encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorId(
            @Parameter(description = "ID único del usuario a buscar", example = "1") @PathVariable Long id) {
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
    @Operation(summary = "Buscar usuario por email", description = "Permite buscar un usuario específico utilizando su dirección de correo electrónico exacto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario localizado correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "El usuario no fue encontrado con el email proporcionado", content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<Usuario>> obtenerPorEmail(
            @Parameter(description = "Correo electrónico del usuario a buscar", example = "juan.perez@email.com") @PathVariable String email) {
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
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema. Validará que el formato del correo sea correcto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(
            @Parameter(description = "Objeto con los datos del nuevo usuario") @Valid @RequestBody UsuarioRequestDTO dto) {
        logger.info("POST /api/usuarios - Solicitud para registrar un nuevo usuario: {}", dto.getEmail());
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", nuevoUsuario.getId());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // PUT: Actualizar un usuario existente
    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente identificado por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "El usuario a actualizar no fue encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del usuario") @Valid @RequestBody UsuarioRequestDTO dto) {
        logger.info("PUT /api/usuarios/{} - Solicitud para actualizar datos del usuario", id);
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        logger.info("Usuario ID {} actualizado correctamente", id);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // DELETE: Eliminar un usuario
    @Operation(summary = "Eliminar usuario", description = "Elimina de forma permanente un usuario del sistema utilizando su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "El usuario a eliminar no fue encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/usuarios/{} - Solicitud para eliminar usuario", id);
        usuarioService.eliminarUsuario(id);
        logger.info("Usuario ID {} eliminado exitosamente", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}