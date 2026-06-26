package com.ferreteria.inventario_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.inventario_service.Dto.InventarioRequestDto;
import com.ferreteria.inventario_service.model.Inventario;
import com.ferreteria.inventario_service.service.InventarioService;

@ExtendWith(MockitoExtension.class)
public class InventarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventarioService inventarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Inventario inventarioMock;
    private InventarioRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        InventarioController controller = new InventarioController(inventarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        inventarioMock = new Inventario();
        inventarioMock.setId(1L);
        inventarioMock.setProductoId(10L);
        inventarioMock.setCantidad(50);

        dtoMock = new InventarioRequestDto();
        dtoMock.setProductoId(10L);
        dtoMock.setCantidad(50);
    }

    @Test
    public void testGuardarInventario() throws Exception {
        // GIVEN
        when(inventarioService.guardarInventario(any(Inventario.class))).thenReturn(inventarioMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(10L))
                .andExpect(jsonPath("$.cantidad").value(50));
                
        verify(inventarioService, times(1)).guardarInventario(any(Inventario.class));
    }

    @Test
    public void testObtenerPorProductoId() throws Exception {
        // GIVEN
        when(inventarioService.obtenerPorProductoId(10L)).thenReturn(inventarioMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/inventario/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(50))
                
                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.links[1].href").exists())
                .andExpect(jsonPath("$.links[2].href").exists())
                .andExpect(jsonPath("$.links[3].href").exists());
                
        verify(inventarioService, times(1)).obtenerPorProductoId(10L);
    }

    @Test
    public void testDescontarStock() throws Exception {
        // GIVEN
        Inventario inventarioActualizado = new Inventario();
        inventarioActualizado.setProductoId(10L);
        inventarioActualizado.setCantidad(45);

        when(inventarioService.actualizarStock(10L, 5)).thenReturn(inventarioActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/inventario/producto/10/descontar")
                .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(45));
                
        verify(inventarioService, times(1)).actualizarStock(10L, 5);
    }

    @Test
    public void testAgregarStock() throws Exception {
        // GIVEN
        Inventario inventarioActualizado = new Inventario();
        inventarioActualizado.setProductoId(10L);
        inventarioActualizado.setCantidad(70);

        when(inventarioService.agregarStock(10L, 20)).thenReturn(inventarioActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/inventario/producto/10/agregar")
                .param("cantidad", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(70));
                
        verify(inventarioService, times(1)).agregarStock(10L, 20);
    }

    @Test
    public void testEliminarInventario() throws Exception {
        // GIVEN
        doNothing().when(inventarioService).eliminarPorProductoId(10L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/inventario/producto/10"))
                .andExpect(status().isNoContent());
                
        verify(inventarioService, times(1)).eliminarPorProductoId(10L);
    }
}