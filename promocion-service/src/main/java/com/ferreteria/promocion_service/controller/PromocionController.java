package com.ferreteria.promocion_service.controller;

import com.ferreteria.promocion_service.Dto.PromocionRequestDto;
import com.ferreteria.promocion_service.assembler.PromocionModelAssembler;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
@Tag(name = "Gestión de Promociones", description = "API para la administración de códigos de descuento y campañas promocionales")
public class PromocionController {

    private final PromocionService promocionService;
    private final PromocionModelAssembler assembler;

    // POST: Crear promoción
    @Operation(summary = "Crear promoción", description = "Registra un nuevo código de descuento en la base de datos definiendo su porcentaje y estado inicial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Promocion.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Promocion> crearPromocion(
            @Parameter(description = "Datos de la nueva promoción") @Valid @RequestBody PromocionRequestDto dto) {
        Promocion promocion = new Promocion();
        promocion.setCodigo(dto.getCodigo());
        promocion.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        promocion.setEstado(dto.getEstado());

        return new ResponseEntity<>(promocionService.crearPromocion(promocion), HttpStatus.CREATED);
    }

    // GET: Ver todas las promociones
    @Operation(summary = "Ver todas las promociones", description = "Devuelve una lista con todos los códigos de descuento registrados, independientemente de su estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Promocion>>> obtenerTodas() {

        List<EntityModel<Promocion>> promocionesModel = promocionService.obtenerTodas().stream()
                .map(assembler::toModel) // HATEOAS delegado al Assembler
                .collect(Collectors.toList());

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodas());
        return ResponseEntity.ok(CollectionModel.of(promocionesModel, linkSelf.withSelfRel()));
    }

    // GET: Validar código y obtener descuento
    @Operation(summary = "Validar código promocional", description = "Verifica si un código de descuento existe y está activo. Retorna el porcentaje aplicable si es válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código válido, retorna el porcentaje de descuento"),
            @ApiResponse(responseCode = "400", description = "El código es inválido, no existe o se encuentra desactivado", content = @Content)
    })
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validarCodigo(
            @Parameter(description = "El texto del código promocional a validar", example = "VERANO2026") @PathVariable String codigo) {
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
    @Operation(summary = "Activar promoción", description = "Habilita un código promocional que previamente estaba inactivo para que pueda ser usado en ventas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción activada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Promocion.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @PutMapping("/{id}/activar")
    public ResponseEntity<Promocion> activarPromocion(
            @Parameter(description = "ID interno de la promoción", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(promocionService.activarPromocion(id));
    }

    // PUT: Botón de Desactivar
    @Operation(summary = "Desactivar promoción", description = "Deshabilita un código promocional para impedir su uso en futuras ventas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción desactivada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Promocion.class))),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada", content = @Content)
    })
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Promocion> desactivarPromocion(
            @Parameter(description = "ID interno de la promoción", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(promocionService.desactivarPromocion(id));
    }
}