package com.ferreteria.soporte_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import com.ferreteria.soporte_service.Dto.TicketRequestDto;
import com.ferreteria.soporte_service.model.Ticket;
import com.ferreteria.soporte_service.service.SoporteService;
import com.ferreteria.soporte_service.assembler.TicketModelAssembler;
import com.ferreteria.soporte_service.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
public class SoporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SoporteService soporteService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Ticket ticketMock;
    private TicketRequestDto dtoMock;

    @BeforeEach
    void setUp() {
        TicketModelAssembler assembler = new TicketModelAssembler();

        SoporteController controller = new SoporteController(soporteService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ticketMock = new Ticket();
        ticketMock.setId(1L);
        ticketMock.setUsuarioId(5L);
        ticketMock.setPedidoId(100L);
        ticketMock.setCategoria("RETRASO_ENVIO");
        ticketMock.setAsunto("Pedido no llega");
        ticketMock.setMensaje("Han pasado 5 días y nada.");
        ticketMock.setEstado("ABIERTO");
        ticketMock.setFechaCreacion(LocalDateTime.now());

        dtoMock = new TicketRequestDto();
        dtoMock.setUsuarioId(5L);
        dtoMock.setPedidoId(100L);
        dtoMock.setCategoria("RETRASO_ENVIO");
        dtoMock.setAsunto("Pedido no llega");
        dtoMock.setMensaje("Han pasado 5 días y nada.");
    }

    @Test
    public void testCrearTicket() throws Exception {
        // GIVEN
        when(soporteService.crearTicket(any(Ticket.class))).thenReturn(ticketMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/soporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(5L))
                .andExpect(jsonPath("$.categoria").value("RETRASO_ENVIO"))
                .andExpect(jsonPath("$.estado").value("ABIERTO"));

        verify(soporteService, times(1)).crearTicket(any(Ticket.class));
    }

    @Test
    public void testObtenerPorUsuario() throws Exception {
        // GIVEN
        when(soporteService.obtenerTicketsPorUsuario(5L)).thenReturn(Arrays.asList(ticketMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/soporte/usuario/5"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.content[0].asunto").value("Pedido no llega"))
                .andExpect(jsonPath("$.content[0].links[0].href").exists())
                .andExpect(jsonPath("$.links[0].href").exists());

        verify(soporteService, times(1)).obtenerTicketsPorUsuario(5L);
    }

    @Test
    public void testObtenerPorId() throws Exception {
        // GIVEN
        when(soporteService.obtenerTicketPorId(1L)).thenReturn(ticketMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/soporte/1"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.asunto").value("Pedido no llega"))
                // Validamos que el Assembler inyectó los enlaces HATEOAS
                .andExpect(jsonPath("$.links[0].rel").value("self"))
                .andExpect(jsonPath("$.links[1].rel").value("ver-historial-usuario"));

        verify(soporteService, times(1)).obtenerTicketPorId(1L);
    }

    @Test
    public void testActualizarEstado() throws Exception {
        // GIVEN
        Ticket ticketActualizado = new Ticket();
        ticketActualizado.setId(1L);
        ticketActualizado.setEstado("EN_REVISION");

        when(soporteService.actualizarEstado(eq(1L), eq("EN_REVISION"))).thenReturn(ticketActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/soporte/1/estado")
                .param("estado", "EN_REVISION"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.estado").value("EN_REVISION"));

        verify(soporteService, times(1)).actualizarEstado(1L, "EN_REVISION");
    }
}