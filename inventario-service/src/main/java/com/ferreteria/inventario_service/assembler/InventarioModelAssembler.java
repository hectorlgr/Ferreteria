package com.ferreteria.inventario_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ferreteria.inventario_service.controller.InventarioController;
import com.ferreteria.inventario_service.model.Inventario;

@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<Inventario, EntityModel<Inventario>> {

        @Override
        public EntityModel<Inventario> toModel(Inventario inventario) {
                return EntityModel.of(inventario,
                                linkTo(methodOn(InventarioController.class)
                                                .obtenerPorProductoId(inventario.getProductoId()))
                                                .withSelfRel(),
                                linkTo(methodOn(InventarioController.class).obtenerTodos())
                                                .withRel("todo-el-inventario"),
                                linkTo(methodOn(InventarioController.class).agregarStock(inventario.getProductoId(),
                                                null))
                                                .withRel("agregar-stock"),
                                linkTo(methodOn(InventarioController.class).descontarStock(inventario.getProductoId(),
                                                null))
                                                .withRel("descontar-stock"));
        }
}