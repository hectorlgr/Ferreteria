package com.ferreteria.promocion_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.ferreteria.promocion_service.Dto.PromocionRequestDto;
import com.ferreteria.promocion_service.model.Promocion;
import com.ferreteria.promocion_service.service.PromocionService;
import com.ferreteria.promocion_service.assembler.PromocionModelAssembler;
import com.ferreteria.promocion_service.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
public class PromocionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PromocionService promocionService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Promocion promocionMock;
    private PromocionRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        PromocionModelAssembler assembler = new PromocionModelAssembler();

        PromocionController controller = new PromocionController(promocionService, assembler);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        promocionMock = new Promocion();
        promocionMock.setId(1L);
        promocionMock.setCodigo("CYBER2026");
        promocionMock.setPorcentajeDescuento(30.0);
        promocionMock.setEstado(true);

        dtoMock = new PromocionRequestDto();
        dtoMock.setCodigo("cyber2026");
        dtoMock.setPorcentajeDescuento(30.0);
        dtoMock.setEstado(true);
    }

    @Test
    public void testCrearPromocion() throws Exception {
        // GIVEN
        when(promocionService.crearPromocion(any(Promocion.class))).thenReturn(promocionMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/promociones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("CYBER2026"))
                .andExpect(jsonPath("$.porcentajeDescuento").value(30.0));

        verify(promocionService, times(1)).crearPromocion(any(Promocion.class));
    }

    @Test
    public void testObtenerTodas() throws Exception {
        // GIVEN
        when(promocionService.obtenerTodas()).thenReturn(Arrays.asList(promocionMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/promociones"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content[0].codigo").value("CYBER2026"))

                .andExpect(jsonPath("$.content[0].links[0].href").exists())

                .andExpect(jsonPath("$.links[0].href").exists());

        verify(promocionService, times(1)).obtenerTodas();
    }

    @Test
    public void testValidarCodigo_Exito() throws Exception {
        // GIVEN
        when(promocionService.validarYObtenerDescuento("CYBER2026")).thenReturn(30.0);

        // WHEN & THEN
        mockMvc.perform(get("/api/promociones/validar/CYBER2026"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.descuento").value(30.0))

                .andExpect(jsonPath("$.links[1].href").exists());

        verify(promocionService, times(1)).validarYObtenerDescuento("CYBER2026");
    }

    @Test
    public void testActivarPromocion() throws Exception {
        // GIVEN
        when(promocionService.activarPromocion(1L)).thenReturn(promocionMock);

        // WHEN & THEN
        mockMvc.perform(put("/api/promociones/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(true));

        verify(promocionService, times(1)).activarPromocion(1L);
    }
}