package com.ferreteria.resena_service.controller;

import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Reseñas", description = "API para la administración de comentarios y valoraciones de los productos")
public class ResenaController {

    private final ResenaService resenaService;

    // POST: Crear una nueva reseña
    @Operation(summary = "Crear una nueva reseña", description = "Registra la valoración y comentario de un usuario sobre un producto específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error de validación del servicio", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crearResena(
            @Parameter(description = "Objeto con los datos de la reseña a crear") @Valid @RequestBody com.ferreteria.resena_service.Dto.ResenaRequestDto dto) {
        try {
            Resena resena = new Resena();
            resena.setIdProducto(dto.getIdProducto());
            resena.setIdUsuario(dto.getIdUsuario());
            resena.setCalificacion(dto.getCalificacion());
            resena.setComentario(dto.getComentario());

            Resena nuevaResena = resenaService.crearResena(resena);
            return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: Obtener todas las reseñas de un producto
    @Operation(summary = "Obtener reseñas por producto", description = "Devuelve una lista de todas las reseñas asociadas a un ID de producto específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida correctamente")
    })
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> obtenerPorProducto(
            @Parameter(description = "ID del producto para consultar sus reseñas", example = "1") @PathVariable Long idProducto) {
        List<Resena> resenas = resenaService.obtenerResenasPorProducto(idProducto);

        List<EntityModel<Resena>> resenasModel = resenas.stream()
    .map(resena -> {
        EntityModel<Resena> modelo = EntityModel.of(resena);
        modelo.add(linkTo(methodOn(this.getClass()).obtenerPromedioProducto(resena.getIdProducto())).withRel("ver-promedio"));
        return modelo;
    })
    .collect(Collectors.toList());

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorProducto(idProducto));
        WebMvcLinkBuilder linkPromedio = linkTo(methodOn(this.getClass()).obtenerPromedioProducto(idProducto));

        return ResponseEntity.ok(CollectionModel.of(resenasModel, 
            linkSelf.withSelfRel(),
            linkPromedio.withRel("ver-promedio-calificacion")));
    }

    // GET: Obtener el promedio de calificación de un producto
    @Operation(summary = "Obtener el promedio de calificaciones", description = "Calcula y retorna el promedio numérico de todas las calificaciones válidas de un producto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)))
    })
    @GetMapping("/producto/{idProducto}/promedio")
    public ResponseEntity<EntityModel<Double>> obtenerPromedioProducto(
            @Parameter(description = "ID del producto para calcular el promedio", example = "1") @PathVariable Long idProducto) {
        Double promedio = resenaService.calcularPromedioProducto(idProducto);
        
        EntityModel<Double> recurso = EntityModel.of(promedio != null ? promedio : 0.0);
        
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPromedioProducto(idProducto));
        WebMvcLinkBuilder linkResenas = linkTo(methodOn(this.getClass()).obtenerPorProducto(idProducto));
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkResenas.withRel("ver-todas-las-resenas"));
        
        return ResponseEntity.ok(recurso);
    }
}