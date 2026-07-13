package com.ferreteria.venta_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

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
import com.ferreteria.venta_service.assembler.VentaModelAssembler;
import com.ferreteria.venta_service.exception.GlobalExceptionHandler;
import com.ferreteria.venta_service.exception.BadRequestException;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.service.VentaService;

@ExtendWith(MockitoExtension.class)
public class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Venta ventaBase;
    private VentaRequestDto ventaRequestDto;

    @BeforeEach
    void setUp() {
        // Instanciamos el Assembler real, igual que en SoporteControllerTest
        VentaModelAssembler assembler = new VentaModelAssembler();

        VentaController controller = new VentaController(ventaService, assembler);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ventaBase = new Venta();
        ventaBase.setId(1L);
        ventaBase.setUsuarioId(10L);
        ventaBase.setTotal(15000);

        ventaRequestDto = new VentaRequestDto();
        ventaRequestDto.setUsuarioId(10L);
        ventaRequestDto.setCostoDespacho(3500);
        ventaRequestDto.setDireccion("Dir Test");

        DetalleVentaRequestDto detalleDto = new DetalleVentaRequestDto();
        detalleDto.setProductoId(100L);
        detalleDto.setCantidad(2);
        detalleDto.setPrecioUnitario(5000);

        ventaRequestDto.setDetalles(List.of(detalleDto));
    }

    @Test
    public void testObtenerTodas_Exito() throws Exception {
        // GIVEN
        when(ventaService.obtenerTodas()).thenReturn(List.of(ventaBase));

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.links[0].href").exists()); // Validamos que inyecte enlaces generales

        verify(ventaService, times(1)).obtenerTodas();
    }

    @Test
    public void testObtenerPorUsuario_Exito() throws Exception {
        // GIVEN
        when(ventaService.obtenerPorUsuario(10L)).thenReturn(List.of(ventaBase));

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.links[0].href").exists());

        verify(ventaService, times(1)).obtenerPorUsuario(10L);
    }

    @Test
    public void testObtenerPorEmail_Exito() throws Exception {
        // GIVEN
        when(ventaService.obtenerVentasPorEmailUsuario("test@correo.com")).thenReturn(List.of(ventaBase));

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas/cliente/email/test@correo.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.links[0].href").exists());

        verify(ventaService, times(1)).obtenerVentasPorEmailUsuario("test@correo.com");
    }

    @Test
    public void testObtenerPorRangoFechas_Exito() throws Exception {
        // GIVEN
        when(ventaService.obtenerPorRangoFechas(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(ventaBase));

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas/rango-fechas")
                .param("fechaInicio", "2024-01-01")
                .param("fechaFin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(ventaService, times(1)).obtenerPorRangoFechas(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testObtenerPorId_Exito() throws Exception {
        // GIVEN
        when(ventaService.obtenerPorId(1L)).thenReturn(ventaBase);

        // WHEN & THEN
        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.links[0].rel").exists()); // Verifica HATEOAS inyectado por el Assembler real

        verify(ventaService, times(1)).obtenerPorId(1L);
    }

    @Test
    public void testProcesarVenta_Exito() throws Exception {
        // GIVEN
        when(ventaService.procesarVenta(any(Venta.class), anyString(), eq(null))).thenReturn(ventaBase);

        // WHEN & THEN
        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaRequestDto)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(1L));

        verify(ventaService, times(1)).procesarVenta(any(Venta.class), anyString(), eq(null));
    }

    @Test
    public void testProcesarVenta_ServicioLanzaBadRequest_Retorna400() throws Exception {
        // GIVEN: Simulamos que el servicio falla al validar el inventario o la promo
        when(ventaService.procesarVenta(any(Venta.class), anyString(), eq(null)))
                .thenThrow(new BadRequestException("Error de inventario"));

        // WHEN & THEN
        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaRequestDto)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(jsonPath("$.message").value("Error de inventario"));
    }

    @Test
    public void testActualizarVenta_Exito() throws Exception {
        // GIVEN
        when(ventaService.actualizarVenta(eq(1L), any(Venta.class))).thenReturn(ventaBase);

        // WHEN & THEN
        mockMvc.perform(put("/api/ventas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaRequestDto)))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.id").value(1L));

        verify(ventaService, times(1)).actualizarVenta(eq(1L), any(Venta.class));
    }

    @Test
    public void testEliminarVenta_Exito() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent()); // HTTP 204

        verify(ventaService, times(1)).eliminarVenta(1L);
    }
}