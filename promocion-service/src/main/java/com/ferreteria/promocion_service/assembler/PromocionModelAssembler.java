package com.ferreteria.promocion_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.promocion_service.controller.PromocionController;
import com.ferreteria.promocion_service.model.Promocion;

@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<Promocion, EntityModel<Promocion>> {

    @Override
    public EntityModel<Promocion> toModel(Promocion promocion) {
        return EntityModel.of(promocion,
                linkTo(methodOn(PromocionController.class).validarCodigo(promocion.getCodigo()))
                        .withRel("validar-codigo"));
    }

    public EntityModel<Map<String, Double>> toDescuentoModel(Map<String, Double> response, String codigo) {
        return EntityModel.of(response,
                linkTo(methodOn(PromocionController.class).validarCodigo(codigo)).withSelfRel(),
                linkTo(methodOn(PromocionController.class).obtenerTodas()).withRel("todas-las-promociones"));
    }
}