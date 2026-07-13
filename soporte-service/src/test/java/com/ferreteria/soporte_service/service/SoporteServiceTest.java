package com.ferreteria.soporte_service.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferreteria.soporte_service.model.Ticket;
import com.ferreteria.soporte_service.repository.TicketRepository;
import com.ferreteria.soporte_service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SoporteServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private SoporteService soporteService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void testObtenerTicketsPorUsuario_Exito() {
        // GIVEN
        Ticket ticket1 = new Ticket();
        ticket1.setUsuarioId(1L);
        Ticket ticket2 = new Ticket();
        ticket2.setUsuarioId(1L);

        when(ticketRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(ticket1, ticket2));

        // WHEN
        List<Ticket> resultado = soporteService.obtenerTicketsPorUsuario(1L);

        // THEN
        assertEquals(2, resultado.size());
        verify(ticketRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void testObtenerTicketPorId_Exito() {
        // GIVEN
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        ticket.setAsunto("Consulta general");

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        // WHEN
        Ticket resultado = soporteService.obtenerTicketPorId(10L);

        // THEN
        assertNotNull(resultado);
        assertEquals("Consulta general", resultado.getAsunto());
        verify(ticketRepository, times(1)).findById(10L);
    }

    @Test
    void testObtenerTicketPorId_NoEncontrado() {
        // GIVEN
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> {
            soporteService.obtenerTicketPorId(99L);
        });
        verify(ticketRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarEstado_Exito() {
        // GIVEN
        Ticket ticketOriginal = new Ticket();
        ticketOriginal.setId(1L);
        ticketOriginal.setEstado("ABIERTO");

        Ticket ticketActualizado = new Ticket();
        ticketActualizado.setId(1L);
        ticketActualizado.setEstado("EN_REVISION");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketOriginal));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketActualizado);

        // WHEN
        Ticket resultado = soporteService.actualizarEstado(1L, "EN_REVISION");

        // THEN
        assertEquals("EN_REVISION", resultado.getEstado());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}