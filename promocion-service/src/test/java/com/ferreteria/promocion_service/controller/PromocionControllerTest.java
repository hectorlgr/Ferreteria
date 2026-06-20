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
        PromocionController controller = new PromocionController(promocionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 1. Modelo de respuesta simulado
        promocionMock = new Promocion();
        promocionMock.setId(1L);
        promocionMock.setCodigo("CYBER2026");
        promocionMock.setPorcentajeDescuento(30.0);
        promocionMock.setEstado(true);

        // 2. DTO de entrada simulado
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
                .andExpect(status().isCreated()) // HTTP 201
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
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$._embedded.promocionList[0].codigo").value("CYBER2026"))
                // Validar HATEOAS
                .andExpect(jsonPath("$._embedded.promocionList[0]._links.validar-codigo.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
                
        verify(promocionService, times(1)).obtenerTodas();
    }

    @Test
    public void testValidarCodigo_Exito() throws Exception {
        // GIVEN
        when(promocionService.validarYObtenerDescuento("CYBER2026")).thenReturn(30.0);

        // WHEN & THEN
        mockMvc.perform(get("/api/promociones/validar/CYBER2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuento").value(30.0)) // Verifica que el Map devuelve la clave "descuento"
                // Validar HATEOAS
                .andExpect(jsonPath("$._links.todas-las-promociones.href").exists());
                
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