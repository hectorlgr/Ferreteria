package com.ferreteria.soporte_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.soporte_service.controller.SoporteController;
import com.ferreteria.soporte_service.model.Ticket;

@Component
public class TicketModelAssembler implements RepresentationModelAssembler<Ticket, EntityModel<Ticket>> {

    @Override
    public EntityModel<Ticket> toModel(Ticket ticket) {
        return EntityModel.of(ticket,
                linkTo(methodOn(SoporteController.class).obtenerPorId(ticket.getId())).withSelfRel(),
                linkTo(methodOn(SoporteController.class).obtenerPorUsuario(ticket.getUsuarioId()))
                        .withRel("ver-historial-usuario"));
    }
}