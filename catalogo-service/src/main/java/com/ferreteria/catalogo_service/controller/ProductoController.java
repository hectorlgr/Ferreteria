package com.ferreteria.catalogo_service.controller;

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

import com.ferreteria.catalogo_service.Dto.ProductoRequestDto;
import com.ferreteria.catalogo_service.model.Producto;
import com.ferreteria.catalogo_service.service.ProductoService;
import com.ferreteria.catalogo_service.assembler.ProductoModelAssembler;

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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Productos", description = "API para la gestión y consulta de artículos de ferretería")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    // GET: Obtener todos los productos
    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista completa de los productos registrados en el catálogo con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> obtenerTodos() {
        logger.info("GET /api/productos - Solicitud para listar todos los productos");
        List<Producto> productos = productoService.obtenerTodos();

        List<EntityModel<Producto>> productosModel = productos.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).obtenerTodos());
        logger.debug("Cantidad de productos obtenidos: {}", productos.size());

        return ResponseEntity.ok(CollectionModel.of(productosModel, linkSelf.withSelfRel()));
    }

    // GET: Obtener producto por ID
    @Operation(summary = "Buscar producto por ID", description = "Retorna los detalles de un único producto basado en su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto localizado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "El producto no fue encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/productos/{} - Solicitud para obtener producto por ID", id);
        Producto producto = productoService.obtenerPorId(id);

        return ResponseEntity.ok(assembler.toModel(producto));
    }

    // POST: Crear un nuevo producto
    @Operation(summary = "Registrar un nuevo producto", description = "Crea un nuevo artículo en el catálogo. Requiere validar los datos de entrada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Producto> guardarProducto(
            @Parameter(description = "Objeto con los datos del nuevo producto") @Valid @RequestBody ProductoRequestDto dto) {
        logger.info("POST /api/productos - Solicitud para registrar un nuevo producto: {}", dto.getNombre());

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setPrecio(dto.getPrecio());

        Producto nuevoProducto = productoService.guardarProducto(producto);
        logger.info("Producto registrado exitosamente con ID: {}", nuevoProducto.getId());

        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "El producto a actualizar no fue encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevos datos del producto") @Valid @RequestBody ProductoRequestDto dto) {
        logger.info("PUT /api/productos/{} - Solicitud para actualizar datos del producto", id);

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setMarca(dto.getMarca());
        producto.setPrecio(dto.getPrecio());

        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        logger.info("Producto ID {} actualizado correctamente", id);

        return ResponseEntity.ok(productoActualizado);
    }

    // DELETE: Eliminar un producto
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El producto a eliminar no fue encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/productos/{} - Solicitud para eliminar producto", id);
        productoService.eliminarProducto(id);
        logger.info("Producto ID {} eliminado exitosamente", id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}