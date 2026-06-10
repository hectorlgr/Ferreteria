package com.ferreteria.resena_service.controller;

import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Resena>> obtenerPorProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(resenaService.obtenerResenasPorProducto(idProducto));
    }

    // GET: Obtener el promedio de calificación de un producto
    // http://localhost:9090/api/resenas/producto/{idProducto}/promedio
    @GetMapping("/producto/{idProducto}/promedio")
    public ResponseEntity<Double> obtenerPromedioProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(resenaService.calcularPromedioProducto(idProducto));
    }
}