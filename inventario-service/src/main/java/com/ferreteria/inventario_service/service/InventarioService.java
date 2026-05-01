package com.ferreteria.inventario_service.service;


import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public List<Inventario> obtenerTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario obtenerPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No se encontró inventario para el producto ID: " + productoId));
    }

    public Inventario guardarInventario(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    // Método pensado para cuando se realice una venta
    public Inventario actualizarStock(Long productoId, Integer cantidadComprada) {
        Inventario inventario = obtenerPorProductoId(productoId);
        
        if (inventario.getCantidad() < cantidadComprada) {
            throw new RuntimeException("Stock insuficiente para el producto ID: " + productoId);
        }
        
        inventario.setCantidad(inventario.getCantidad() - cantidadComprada);
        return inventarioRepository.save(inventario);
    }
}