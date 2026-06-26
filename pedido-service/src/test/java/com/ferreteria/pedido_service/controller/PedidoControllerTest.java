package com.ferreteria.pedido_service.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.ferreteria.pedido_service.Dto.PedidoRequestDto;
import com.ferreteria.pedido_service.model.Pedido;
import com.ferreteria.pedido_service.service.PedidoService;

@ExtendWith(MockitoExtension.class)
public class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Pedido pedidoMock;
    private PedidoRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        PedidoController controller = new PedidoController(pedidoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        pedidoMock = new Pedido();
        pedidoMock.setId(10L);
        pedidoMock.setIdUsuario(5L);
        pedidoMock.setIdVenta(20L);
        pedidoMock.setEstado("CONFIRMADO");

        dtoMock = new PedidoRequestDto();
        dtoMock.setIdUsuario(5L);
        dtoMock.setIdVenta(20L);
        dtoMock.setDireccion("Av. Matta 100");
    }

    @Test
    public void testCrearPedido() throws Exception {
        // GIVEN
        when(pedidoService.crearPedido(anyLong(), anyLong(), anyString())).thenReturn(pedidoMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
                
        verify(pedidoService, times(1)).crearPedido(5L, 20L, "Av. Matta 100");
    }

    @Test
    public void testObtenerPorUsuario() throws Exception {
        // GIVEN
        when(pedidoService.obtenerPedidosPorUsuario(5L)).thenReturn(Arrays.asList(pedidoMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/pedidos/usuario/5"))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.content[0].estado").value("CONFIRMADO"))
                
                .andExpect(jsonPath("$.content[0].links[0].href").exists())
                .andExpect(jsonPath("$.content[0].links[1].href").exists())
                
                .andExpect(jsonPath("$.links[0].href").exists());
                
        verify(pedidoService, times(1)).obtenerPedidosPorUsuario(5L);
    }

    @Test
    public void testActualizarEstado() throws Exception {
        // GIVEN
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(10L);
        pedidoActualizado.setEstado("EN_RUTA");
        
        when(pedidoService.actualizarEstado(10L, "EN_RUTA")).thenReturn(pedidoActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/pedidos/10/estado")
                .param("nuevoEstado", "EN_RUTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_RUTA"));
                
        verify(pedidoService, times(1)).actualizarEstado(10L, "EN_RUTA");
    }

    @Test
    public void testCancelarPedido() throws Exception {
        // GIVEN
        Pedido pedidoCancelado = new Pedido();
        pedidoCancelado.setId(10L);
        pedidoCancelado.setEstado("CANCELADO");
        
        when(pedidoService.cancelarPedido(10L)).thenReturn(pedidoCancelado);

        // WHEN & THEN
        mockMvc.perform(put("/api/pedidos/10/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
                
        verify(pedidoService, times(1)).cancelarPedido(10L);
    }
}