package com.ferreteria.soporte_service.config;

import com.ferreteria.soporte_service.model.Ticket;
import com.ferreteria.soporte_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SoporteDataLoader implements CommandLineRunner {

    private final TicketRepository ticketRepository;

    @Override
    public void run(String... args) throws Exception {

        if (ticketRepository.count() == 0) {
            log.info("Iniciando la carga de tickets de soporte por defecto...");

            // Escenario 1: Reclamo asociado a una compra
            Ticket ticket1 = new Ticket();
            ticket1.setUsuarioId(1L);
            ticket1.setPedidoId(1L);
            ticket1.setCategoria("RETRASO_DESPACHO");
            ticket1.setAsunto("Mi pedido dice entregado pero no lo tengo");
            ticket1.setMensaje(
                    "Revisé en conserjería y me indican que no ha llegado ningún paquete de la ferretería a mi nombre.");
            ticket1.setEstado("ABIERTO");
            ticket1.setFechaCreacion(LocalDateTime.now());

            // Escenario 2: Consulta general sin compra asociada
            Ticket ticket2 = new Ticket();
            ticket2.setUsuarioId(2L);
            ticket2.setPedidoId(2L);
            ticket2.setCategoria("CONSULTA_PRODUCTO");
            ticket2.setAsunto("Duda sobre garantía de herramientas");
            ticket2.setMensaje(
                    "Quisiera saber si los taladros percutores de marca Makita vienen con garantía extendida o debo comprarla aparte.");
            ticket2.setEstado("RESUELTO");
            ticket2.setFechaCreacion(LocalDateTime.now().minusDays(2)); // Creado hace 2 días

            ticketRepository.save(ticket1);
            ticketRepository.save(ticket2);

            log.info("¡Tickets de soporte cargados exitosamente!");
        } else {
            log.info("La base de datos ya contiene tickets. Omitiendo carga inicial.");
        }
    }
}