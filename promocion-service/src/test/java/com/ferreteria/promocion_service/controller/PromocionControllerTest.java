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
                // .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk()) // HTTP 200
                
                // 1. Buscamos el código dentro del nodo 'content'
                .andExpect(jsonPath("$.content[0].codigo").value("CYBER2026"))
                
                // 2. Buscamos el link de la promoción en su arreglo (validar-codigo)
                .andExpect(jsonPath("$.content[0].links[0].href").exists())
                
                // 3. Buscamos el link 'self' general de la colección en la raíz
                .andExpect(jsonPath("$.links[0].href").exists());
                
        verify(promocionService, times(1)).obtenerTodas();
    }

    @Test
    public void testValidarCodigo_Exito() throws Exception {
        // GIVEN
        when(promocionService.validarYObtenerDescuento("CYBER2026")).thenReturn(30.0);

        // WHEN & THEN
        mockMvc.perform(get("/api/promociones/validar/CYBER2026"))
                // .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                
                // El descuento se mapea correctamente en la raíz
                .andExpect(jsonPath("$.descuento").value(30.0))
                
                // Reemplazamos '_links.todas-las-promociones' por la posición en el arreglo
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