package com.ferreteria.resena_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.resena_service.Dto.ResenaRequestDto;
import com.ferreteria.resena_service.model.Resena;
import com.ferreteria.resena_service.service.ResenaService;
import com.ferreteria.resena_service.assembler.ResenaModelAssembler;

@ExtendWith(MockitoExtension.class)
public class ResenaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResenaService resenaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Resena resenaMock;
    private ResenaRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        ResenaModelAssembler assembler = new ResenaModelAssembler();

        ResenaController controller = new ResenaController(resenaService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        resenaMock = new Resena();
        resenaMock.setId(1L);
        resenaMock.setIdProducto(100L);
        resenaMock.setIdUsuario(5L);
        resenaMock.setCalificacion(5);
        resenaMock.setComentario("Muy buen producto");

        dtoMock = new ResenaRequestDto();
        dtoMock.setIdProducto(100L);
        dtoMock.setIdUsuario(5L);
        dtoMock.setCalificacion(5);
        dtoMock.setComentario("Muy buen producto");
    }

    @Test
    public void testCrearResena() throws Exception {
        // GIVEN
        when(resenaService.crearResena(any(Resena.class))).thenReturn(resenaMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idProducto").value(100L))
                .andExpect(jsonPath("$.calificacion").value(5));

        verify(resenaService, times(1)).crearResena(any(Resena.class));
    }

    @Test
    public void testObtenerPorProducto() throws Exception {
        // GIVEN
        when(resenaService.obtenerResenasPorProducto(100L)).thenReturn(Arrays.asList(resenaMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/resenas/producto/100"))
                .andExpect(status().isOk()) // HTTP 200

                .andExpect(jsonPath("$.content[0].calificacion").value(5))

                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.links[1].href").exists());

        verify(resenaService, times(1)).obtenerResenasPorProducto(100L);
    }

    @Test
    public void testObtenerPromedioProducto() throws Exception {
        // GIVEN
        when(resenaService.calcularPromedioProducto(100L)).thenReturn(4.5);

        // WHEN & THEN
        mockMvc.perform(get("/api/resenas/producto/100/promedio"))
                .andExpect(status().isOk()) // Ya no será HTTP 500

                .andExpect(jsonPath("$.promedio").value(4.5))

                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.links[1].href").exists());

        verify(resenaService, times(1)).calcularPromedioProducto(100L);
    }
}