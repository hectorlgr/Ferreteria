package com.ferreteria.venta_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.ferreteria.venta_service.Dto.DetalleVentaRequestDto;
import com.ferreteria.venta_service.Dto.VentaRequestDto;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.service.VentaService;
import com.ferreteria.venta_service.assembler.VentaModelAssembler;

@ExtendWith(MockitoExtension.class)
public class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Venta ventaMock;
    private VentaRequestDto ventaDtoMock;

    @BeforeEach
    void setUp() {
        VentaModelAssembler assembler = new VentaModelAssembler();

        VentaController controller = new VentaController(ventaService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        ventaMock = new Venta();
        ventaMock.setId(10L);
        ventaMock.setUsuarioId(5L);
        ventaMock.setCostoDespacho(2500);
        ventaMock.setTotal(12500);

        ventaDtoMock = new VentaRequestDto();
        ventaDtoMock.setUsuarioId(5L);
        ventaDtoMock.setCostoDespacho(2500);
        ventaDtoMock.setDireccion("Av. Matta 100");

        DetalleVentaRequestDto detalleDto = new DetalleVentaRequestDto();
        detalleDto.setProductoId(1L);
        detalleDto.setCantidad(2);
        detalleDto.setPrecioUnitario(5000);
        ventaDtoMock.setDetalles(Arrays.asList(detalleDto));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        // GIVEN
        when(ventaService.obtenerPorId(10L)).thenReturn(ventaMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.usuarioId").value(5L))
                .andExpect(jsonPath("$.total").value(12500))

                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.links[1].href").exists())
                .andExpect(jsonPath("$.links[2].href").exists());

        verify(ventaService, times(1)).obtenerPorId(10L);
    }

    @Test
    public void testProcesarVenta() throws Exception {
        // GIVEN
        when(ventaService.procesarVenta(any(Venta.class), anyString(), isNull())).thenReturn(ventaMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaDtoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.usuarioId").value(5L));
    }
}