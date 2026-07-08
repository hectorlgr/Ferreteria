package com.ferreteria.resena_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.resena_service.controller.ResenaController;
import com.ferreteria.resena_service.model.Resena;

@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<Resena, EntityModel<Resena>> {

    @Override
    public EntityModel<Resena> toModel(Resena resena) {
        return EntityModel.of(resena,
                linkTo(methodOn(ResenaController.class).obtenerPromedioProducto(resena.getIdProducto()))
                        .withRel("ver-promedio"));
    }
}