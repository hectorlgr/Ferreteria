package com.ferreteria.despacho_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.despacho_service.controller.DespachoController;
import com.ferreteria.despacho_service.model.Despacho;

@Component
public class DespachoModelAssembler implements RepresentationModelAssembler<Despacho, EntityModel<Despacho>> {

    @Override
    public EntityModel<Despacho> toModel(Despacho despacho) {
        return EntityModel.of(despacho,
                // Usamos obtenerPorPedidoId como su enlace principal "self"
                linkTo(methodOn(DespachoController.class).obtenerPorPedidoId(despacho.getPedidoId())).withSelfRel(),
                linkTo(methodOn(DespachoController.class).obtenerTodos()).withRel("todos-los-despachos"),
                linkTo(methodOn(DespachoController.class).actualizarEstado(despacho.getId(), "NUEVO_ESTADO"))
                        .withRel("actualizar-estado"));
    }
}