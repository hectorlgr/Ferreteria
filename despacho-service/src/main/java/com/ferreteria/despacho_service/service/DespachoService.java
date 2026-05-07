package com.ferreteria.despacho_service.service;

import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final WebClient.Builder webClientBuilder;

    public List<Despacho> obtenerTodos() {
        return despachoRepository.findAll();
    }

    public Despacho obtenerPorSeguimiento(Integer numeroSeguimiento) {
        return despachoRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con el seguimiento: " + numeroSeguimiento));
    }

    public Despacho crearDespacho(Despacho despacho) {
        // 1. VALIDACIÓN: Comprobar que la venta existe en el venta-service (Puerto 9094)
        try {
            webClientBuilder.build().get()
                    .uri("http://localhost:9094/api/ventas/" + despacho.getVentaId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block(); // block() espera la respuesta de forma síncrona
        } catch (Exception e) {
            throw new RuntimeException("Error: La venta ID " + despacho.getVentaId() + " no existe. No se puede crear el despacho.");
        }

        // 2. LÓGICA DE SEGUIMIENTO: Buscar el número más alto y sumarle 1
        Integer maximoActual = despachoRepository.obtenerMaximoSeguimiento();
        int siguienteNumero = (maximoActual == null) ? 1 : maximoActual + 1;
        
        // 3. ASIGNACIÓN DE DATOS
        despacho.setNumeroSeguimiento(siguienteNumero);
        despacho.setEstado("PREPARANDO"); // Estado inicial por defecto
        
        // 4. GUARDAR EN BASE DE DATOS
        return despachoRepository.save(despacho);
    }

    public Despacho actualizarEstado(Long id, String nuevoEstado) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado"));
        
        despacho.setEstado(nuevoEstado);
        return despachoRepository.save(despacho);
    }
}