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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    // POST: Crear una nueva reseña
    // http://localhost:9090/api/resenas
    @PostMapping
    public ResponseEntity<?> crearResena(@Valid @RequestBody com.ferreteria.resena_service.Dto.ResenaRequestDto dto) {
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
    // http://localhost:9090/api/resenas/producto/{idProducto}
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> obtenerPorProducto(@PathVariable Long idProducto) {
        List<Resena> resenas = resenaService.obtenerResenasPorProducto(idProducto);

        // Envolvemos cada reseña en un EntityModel
        List<EntityModel<Resena>> resenasModel = resenas.stream()
            .map(resena -> EntityModel.of(resena))
            .collect(Collectors.toList());

        // Creamos los links para la colección general
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPorProducto(idProducto));
        WebMvcLinkBuilder linkPromedio = linkTo(methodOn(this.getClass()).obtenerPromedioProducto(idProducto));

        return ResponseEntity.ok(CollectionModel.of(resenasModel, 
            linkSelf.withSelfRel(),
            linkPromedio.withRel("ver-promedio-calificacion")));
    }

    // GET: Obtener el promedio de calificación de un producto
    // http://localhost:9090/api/resenas/producto/{idProducto}/promedio
    @GetMapping("/producto/{idProducto}/promedio")
    public ResponseEntity<EntityModel<Double>> obtenerPromedioProducto(@PathVariable Long idProducto) {
        Double promedio = resenaService.calcularPromedioProducto(idProducto);
        
        // Envolvemos el número Double en un EntityModel para poder pegarle links
        EntityModel<Double> recurso = EntityModel.of(promedio != null ? promedio : 0.0);
        
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerPromedioProducto(idProducto));
        WebMvcLinkBuilder linkResenas = linkTo(methodOn(this.getClass()).obtenerPorProducto(idProducto));
        
        recurso.add(linkSelf.withSelfRel());
        recurso.add(linkResenas.withRel("ver-todas-las-resenas"));
        
        return ResponseEntity.ok(recurso);
    }
}