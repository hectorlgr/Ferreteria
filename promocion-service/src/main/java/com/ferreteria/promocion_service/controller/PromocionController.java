package com.ferreteria.promocion_service.controller;

import com.ferreteria.promocion_service.Dto.PromocionRequestDto;
import com.ferreteria.promocion_service.model.Promocion;
import com.ferreteria.promocion_service.service.PromocionService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService promocionService;

    // POST: Crear promoción
    // http://localhost:9090/api/promociones
    @PostMapping
    public ResponseEntity<Promocion> crearPromocion(@Valid @RequestBody PromocionRequestDto dto) {
        Promocion promocion = new Promocion();
        promocion.setCodigo(dto.getCodigo());
        promocion.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        promocion.setEstado(dto.getEstado());

        return new ResponseEntity<>(promocionService.crearPromocion(promocion), HttpStatus.CREATED);
    }

    // GET: Ver todas las promociones
    // http://localhost:9090/api/promociones
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Promocion>>> obtenerTodas() {
        List<Promocion> promociones = promocionService.obtenerTodas();
        
        // Envolvemos cada promoción y le agregamos un link para validar su propio código
        List<EntityModel<Promocion>> promocionesModel = promociones.stream()
            .map(promocion -> EntityModel.of(promocion,
                linkTo(methodOn(this.getClass()).validarCodigo(promocion.getCodigo())).withRel("validar-codigo")))
            .collect(Collectors.toList());
            
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodas());
        return ResponseEntity.ok(CollectionModel.of(promocionesModel, linkSelf.withSelfRel()));
    }

    // GET: Validar código y obtener descuento
    // http://localhost:9090/api/promociones/validar/{codigo}
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validarCodigo(@PathVariable String codigo) {
        try {
            Double porcentaje = promocionService.validarYObtenerDescuento(codigo);
            
            // Retornamos un Map para que sea un JSON fácil de leer por el Venta-Service
            Map<String, Double> response = new HashMap<>();
            response.put("descuento", porcentaje);
            
            // Envolvemos el Map en HATEOAS
            EntityModel<Map<String, Double>> recurso = EntityModel.of(response);
            WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).validarCodigo(codigo));
            WebMvcLinkBuilder linkTodas = linkTo(methodOn(this.getClass()).obtenerTodas());
            
            recurso.add(linkSelf.withSelfRel());
            recurso.add(linkTodas.withRel("todas-las-promociones"));
            
            return ResponseEntity.ok(recurso);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: Botón de Activar
    // http://localhost:9090/api/promociones/{id}/activar
    @PutMapping("/{id}/activar")
    public ResponseEntity<Promocion> activarPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.activarPromocion(id));
    }

    // PUT: Botón de Desactivar
    // http://localhost:9090/api/promociones/{id}/desactivar
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Promocion> desactivarPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.desactivarPromocion(id));
    }
}