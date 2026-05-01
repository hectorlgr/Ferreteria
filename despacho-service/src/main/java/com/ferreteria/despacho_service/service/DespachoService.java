package com.ferreteria.despacho_service.service;

import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;

    public List<Despacho> obtenerTodos() {
        return despachoRepository.findAll();
    }

    public Despacho obtenerPorSeguimiento(Integer numeroSeguimiento) {
        return despachoRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con el seguimiento: " + numeroSeguimiento));
    }

    public Despacho crearDespacho(Despacho despacho) {
        // Buscamos cuál es el número más alto actualmente
        Integer maximoActual = despachoRepository.obtenerMaximoSeguimiento();
        
        // Si la base de datos está vacía (maximoActual es null), empezamos en 1.
        // Si ya existen registros, le sumamos 1 al número mayor.
        int siguienteNumero = (maximoActual == null) ? 1 : maximoActual + 1;
        
        // Por defecto, todo despacho nuevo parte en estado "PREPARANDO"
        despacho.setNumeroSeguimiento(siguienteNumero);
        despacho.setEstado("PREPARANDO");
        
        return despachoRepository.save(despacho);
    }

    public Despacho actualizarEstado(Long id, String nuevoEstado) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado"));
        
        despacho.setEstado(nuevoEstado);
        return despachoRepository.save(despacho);
    }
}