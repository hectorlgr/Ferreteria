package com.ferreteria.inventario_service.controller;

import com.ferreteria.inventario_service.Dto.InventarioRequestDto;
import com.ferreteria.inventario_service.assembler.InventarioModelAssembler;
import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Tag(name = "Control de Inventario", description = "API para la gestión del stock y disponibilidad física de los productos")
public class InventarioController {

        private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

        private final InventarioService inventarioService;
        private final InventarioModelAssembler assembler;

        // GET: Obtener todo el inventario
        @Operation(summary = "Obtener todo el inventario", description = "Retorna una lista con el stock actual de todos los productos registrados.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida exitosamente")
        })
        @GetMapping
        public ResponseEntity<CollectionModel<EntityModel<Inventario>>> obtenerTodos() {
                logger.info("GET /api/inventario - Solicitud para listar todo el inventario");

                List<EntityModel<Inventario>> inventarioModels = inventarioService.obtenerTodos().stream()
                                .map(assembler::toModel) // <--- Delega en el Assembler
                                .collect(Collectors.toList());

                WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
                return ResponseEntity.ok(CollectionModel.of(inventarioModels, linkSelf.withSelfRel()));
        }

        // GET: Obtener inventario por ID de producto
        @Operation(summary = "Consultar stock por ID de Producto", description = "Retorna el registro de inventario asociado a un producto específico.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
                        @ApiResponse(responseCode = "404", description = "No existe registro de inventario para este producto", content = @Content)
        })
        @GetMapping("/producto/{productoId}")
        public ResponseEntity<EntityModel<Inventario>> obtenerPorProductoId(@PathVariable Long productoId) {
                logger.info("GET /api/inventario/producto/{} - Solicitud para buscar inventario por ID", productoId);

                Inventario inventario = inventarioService.obtenerPorProductoId(productoId);
                return ResponseEntity.ok(assembler.toModel(inventario)); // <--- Delega en el Assembler
        }

        // POST para registrar nuevo inventario
        @Operation(summary = "Inicializar inventario", description = "Crea el registro inicial de stock para un producto recién agregado al catálogo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Inventario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
        })
        @PostMapping
        public ResponseEntity<Inventario> guardarInventario(
                        @Parameter(description = "Objeto con el ID del producto y la cantidad inicial") @Valid @RequestBody InventarioRequestDto dto) {
                logger.info("POST /api/inventario - Solicitud para registrar inventario del Producto ID: {}",
                                dto.getProductoId());

                Inventario inventario = new Inventario();
                inventario.setProductoId(dto.getProductoId());
                inventario.setCantidad(dto.getCantidad());

                Inventario nuevoInventario = inventarioService.guardarInventario(inventario);
                logger.info("Inventario registrado exitosamente con ID interno: {}", nuevoInventario.getId());
                return new ResponseEntity<>(nuevoInventario, HttpStatus.CREATED);
        }

        // PUT para actualizar el stock de un producto
        @Operation(summary = "Descontar stock de producto", description = "Disminuye la cantidad de unidades en el inventario al registrar una venta o pérdida.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
                        @ApiResponse(responseCode = "400", description = "Stock insuficiente para realizar el descuento", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado en el inventario", content = @Content)
        })
        @PutMapping("/producto/{productoId}/descontar")
        public ResponseEntity<Inventario> descontarStock(
                        @Parameter(description = "ID del producto", example = "1") @PathVariable Long productoId,
                        @Parameter(description = "Cantidad de unidades a descontar", example = "2") @RequestParam Integer cantidad) {
                logger.info("PUT /api/inventario/producto/{}/descontar - Solicitud para descontar {} unidades",
                                productoId,
                                cantidad);
                Inventario inventarioActualizado = inventarioService.actualizarStock(productoId, cantidad);
                logger.info("Stock descontado exitosamente. Nuevo stock para Producto ID {}: {}", productoId,
                                inventarioActualizado.getCantidad());
                return ResponseEntity.ok(inventarioActualizado);
        }

        // PUT para agregar stock a un producto
        @Operation(summary = "Agregar stock de producto", description = "Incrementa la cantidad de unidades en el inventario, usualmente tras la recepción de un pedido al proveedor.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock agregado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado en el inventario", content = @Content)
        })
        @PutMapping("/producto/{productoId}/agregar")
        public ResponseEntity<Inventario> agregarStock(
                        @Parameter(description = "ID del producto", example = "1") @PathVariable Long productoId,
                        @Parameter(description = "Cantidad de unidades a ingresar", example = "10") @RequestParam Integer cantidad) {
                logger.info("PUT /api/inventario/producto/{}/agregar - Solicitud para ingresar {} unidades", productoId,
                                cantidad);
                Inventario inventarioActualizado = inventarioService.agregarStock(productoId, cantidad);
                logger.info("Stock ingresado exitosamente. Nuevo stock para Producto ID {}: {}", productoId,
                                inventarioActualizado.getCantidad());
                return ResponseEntity.ok(inventarioActualizado);
        }

        // PUT para resetear el stock a cero desde el catalogo-service
        @Operation(summary = "Resetear stock a cero", description = "Establece el stock de un producto en cero. Este endpoint es típicamente consumido internamente al descontinuar un artículo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Stock reseteado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado en el inventario", content = @Content)
        })
        @PutMapping("/reset/{productoId}")
        public ResponseEntity<Void> resetearStock(
                        @Parameter(description = "ID del producto a resetear", example = "1") @PathVariable Long productoId) {
                logger.info("PUT /api/inventario/reset/{} - Solicitud de catálogo para resetear stock", productoId);
                inventarioService.resetearStock(productoId);
                return ResponseEntity.noContent().build();
        }

        // DELETE para eliminar inventario por ID de producto
        @Operation(summary = "Eliminar registro de inventario", description = "Borra físicamente el registro de stock de un producto de la base de datos.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Inventario eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado en el inventario", content = @Content)
        })
        @DeleteMapping("/producto/{productoId}")
        public ResponseEntity<Void> eliminarPorProductoId(
                        @Parameter(description = "ID del producto cuyo inventario se eliminará", example = "1") @PathVariable Long productoId) {
                logger.info("DELETE /api/inventario/producto/{} - Solicitud para eliminar inventario", productoId);
                inventarioService.eliminarPorProductoId(productoId);
                logger.info("Inventario eliminado para Producto ID: {}", productoId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}