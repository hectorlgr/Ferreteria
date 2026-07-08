package com.ferreteria.venta_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.venta_service.controller.VentaController;
import com.ferreteria.venta_service.model.Venta;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).obtenerTodas()).withRel("todas-las-ventas"),
                linkTo(methodOn(VentaController.class).obtenerPorUsuario(venta.getUsuarioId()))
                        .withRel("otras-compras-del-usuario"));
    }
}