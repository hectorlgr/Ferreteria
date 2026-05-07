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

    public Despacho obtenerPorVentaId(Long ventaId) {
    return despachoRepository.findByVentaId(ventaId)
            .orElseThrow(() -> new RuntimeException("No se encontró un despacho asociado a la venta ID: " + ventaId));
}

    public Despacho obtenerPorEstado(String estado) {
        return despachoRepository.findByEstado(estado)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con el estado: " + estado));
    }

    public Despacho crearDespacho(Despacho despacho) {
        // Validación con Venta Service
        try {
            webClientBuilder.build().get()
                    .uri("http://localhost:9094/api/ventas/" + despacho.getVentaId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error: La venta ID " + despacho.getVentaId() + " no existe.");
        }

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