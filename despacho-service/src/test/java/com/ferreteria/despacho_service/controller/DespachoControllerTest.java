package com.ferreteria.despacho_service.controller;

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
import com.ferreteria.despacho_service.Dto.DespachoRequestDto;
import com.ferreteria.despacho_service.model.Despacho;
import com.ferreteria.despacho_service.service.DespachoService;

@ExtendWith(MockitoExtension.class)
public class DespachoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DespachoService despachoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Despacho despachoMock;
    private DespachoRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        DespachoController controller = new DespachoController(despachoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 1. Modelo simulado
        despachoMock = new Despacho();
        despachoMock.setId(1L);
        despachoMock.setPedidoId(1024L);
        despachoMock.setDireccion("Av. Las Condes 500");
        despachoMock.setEstado("RECIBIDO_EN_BODEGA");

        // 2. DTO de entrada simulado
        dtoMock = new DespachoRequestDto();
        dtoMock.setIdPedido(1024L);
        dtoMock.setIdUsuario(5L);
        dtoMock.setDireccion("Av. Las Condes 500");
    }

    @Test
    public void testCrearDespacho() throws Exception {
        // GIVEN
        when(despachoService.crearDespacho(any(Despacho.class))).thenReturn(despachoMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/despachos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("RECIBIDO_EN_BODEGA"))
                .andExpect(jsonPath("$.direccion").value("Av. Las Condes 500"));
                
        verify(despachoService, times(1)).crearDespacho(any(Despacho.class));
    }

    @Test
    public void testObtenerPorPedidoId() throws Exception {
        // GIVEN
        when(despachoService.obtenerPorPedidoId(1024L)).thenReturn(despachoMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/despachos/pedido/1024"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.estado").value("RECIBIDO_EN_BODEGA"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-los-despachos.href").exists())
                .andExpect(jsonPath("$._links.actualizar-estado.href").exists());
                
        verify(despachoService, times(1)).obtenerPorPedidoId(1024L);
    }

    @Test
    public void testActualizarEstado() throws Exception {
        // GIVEN
        Despacho despachoActualizado = new Despacho();
        despachoActualizado.setId(1L);
        despachoActualizado.setEstado("EN_RUTA");
        
        when(despachoService.actualizarEstado(1L, "EN_RUTA")).thenReturn(despachoActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/despachos/1/estado")
                .param("estado", "EN_RUTA")) // Pasamos el parámetro por URL (@RequestParam)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_RUTA"));
                
        verify(despachoService, times(1)).actualizarEstado(1L, "EN_RUTA");
    }

    @Test
    public void testObtenerTodos() throws Exception {
        // GIVEN
        when(despachoService.obtenerTodos()).thenReturn(Arrays.asList(despachoMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/despachos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.despachoList[0].pedidoId").value(1024L))
                .andExpect(jsonPath("$._embedded.despachoList[0]._links.actualizar-estado.href").exists());
                
        verify(despachoService, times(1)).obtenerTodos();
    }
}