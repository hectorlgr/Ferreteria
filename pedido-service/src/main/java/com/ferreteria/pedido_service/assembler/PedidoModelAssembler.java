package com.ferreteria.pedido_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.pedido_service.controller.PedidoController;
import com.ferreteria.pedido_service.model.Pedido;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        return EntityModel.of(pedido,
                linkTo(methodOn(PedidoController.class).cancelarPedido(pedido.getId())).withRel("cancelar-pedido"),
                linkTo(methodOn(PedidoController.class).actualizarEstado(pedido.getId(), "NUEVO_ESTADO"))
                        .withRel("actualizar-estado"));
    }
}