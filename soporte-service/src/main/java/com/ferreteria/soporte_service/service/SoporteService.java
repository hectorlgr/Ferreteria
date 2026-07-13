package com.ferreteria.soporte_service.service;

import com.ferreteria.soporte_service.model.Ticket;
import com.ferreteria.soporte_service.repository.TicketRepository;
import com.ferreteria.soporte_service.exception.ResourceNotFoundException;
import com.ferreteria.soporte_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoporteService {

    private static final Logger logger = LoggerFactory.getLogger(SoporteService.class);

    private final TicketRepository ticketRepository;
    private final WebClient.Builder webClientBuilder;

    public record PedidoDto(Long idUsuario) {
    }

    public Ticket crearTicket(Ticket ticket) {
        logger.info("Iniciando validaciones para crear ticket del Usuario ID: {} para el Pedido ID: {}",
                ticket.getUsuarioId(), ticket.getPedidoId());

        // Validar que el Usuario existe en usuario-service
        try {
            webClientBuilder.build().get()
                    .uri("http://usuario-service/api/usuarios/" + ticket.getUsuarioId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error: El usuario ID {} no existe.", ticket.getUsuarioId());
            throw new ResourceNotFoundException("El usuario con ID " + ticket.getUsuarioId() + " no existe.");
        }

        // Validar que el Pedido existe y pertenece al usuario
        logger.info("Verificando existencia y pertenencia del Pedido ID: {}...", ticket.getPedidoId());
        try {
            PedidoDto pedido = webClientBuilder.build().get()
                    .uri("http://pedido-service/api/pedidos/" + ticket.getPedidoId())
                    .retrieve()
                    .bodyToMono(PedidoDto.class)
                    .block();

            // El pedido debe pertenecer a quien hace el reclamo
            if (pedido != null && !pedido.idUsuario().equals(ticket.getUsuarioId())) {
                logger.warn("Ticket rechazado: El pedido {} no pertenece al usuario {}", ticket.getPedidoId(),
                        ticket.getUsuarioId());
                throw new BadRequestException("No puedes crear un ticket de soporte para un pedido que no realizaste.");
            }

        } catch (BadRequestException e) {
            // Se relanza la excepción de negocio para que el manejador global la capture
            // limpia
            throw e;
        } catch (Exception e) {
            logger.error("Error al contactar a pedido-service: El pedido ID {} no existe.", ticket.getPedidoId());
            throw new ResourceNotFoundException(
                    "El pedido con ID " + ticket.getPedidoId() + " no existe en el sistema.");
        }

        // Asignar los valores iniciales y guardar
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());

        logger.info("Validaciones superadas. Guardando ticket en la base de datos...");
        return ticketRepository.save(ticket);
    }

    public List<Ticket> obtenerTicketsPorUsuario(Long usuarioId) {
        logger.info("Consultando historial de tickets para el usuario ID: {}", usuarioId);
        return ticketRepository.findByUsuarioId(usuarioId);
    }

    public Ticket obtenerTicketPorId(Long id) {
        logger.info("Buscando ticket ID: {}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con el ID: " + id));
    }

    public Ticket actualizarEstado(Long id, String nuevoEstado) {
        logger.info("Agente de soporte actualizando Ticket ID: {} a nuevo estado: {}", id, nuevoEstado);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con el ID: " + id));

        ticket.setEstado(nuevoEstado);

        Ticket ticketActualizado = ticketRepository.save(ticket);
        logger.info("Estado del Ticket ID: {} actualizado exitosamente.", id);

        return ticketActualizado;
    }
}