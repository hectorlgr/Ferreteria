package com.ferreteria.promocion_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferreteria.promocion_service.model.Promocion;
import com.ferreteria.promocion_service.repository.PromocionRepository;

@ExtendWith(MockitoExtension.class)
public class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    // Helper para crear una promoción de prueba rápidamente
    private Promocion crearPromocionPrueba() {
        Promocion p = new Promocion();
        p.setId(1L);
        p.setCodigo("CYBERDAY");
        p.setPorcentajeDescuento(20.0);
        p.setEstado(true);
        return p;
    }

    @Test
    void testValidarYObtenerDescuento_Exito() {
        // GIVEN: El profesor quiere ver cómo aplicamos reglas de negocio.
        Promocion promo = crearPromocionPrueba();
        // Simulamos que el repositorio encuentra la promoción activa
        when(promocionRepository.findByCodigoAndEstadoTrue("CYBERDAY")).thenReturn(Optional.of(promo));

        // WHEN: El usuario ingresa el cupón en minúsculas (el service debe pasarlo a mayúsculas)
        Double descuento = promocionService.validarYObtenerDescuento("cyberday");

        // THEN: Verificamos que el cálculo de la regla de negocio es correcto
        assertNotNull(descuento);
        assertEquals(20.0, descuento);
        verify(promocionRepository, times(1)).findByCodigoAndEstadoTrue("CYBERDAY");
    }

    @Test
    void testValidarYObtenerDescuento_Invalido_LanzaExcepcion() {
        // GIVEN: Un código que no existe o está apagado
        when(promocionRepository.findByCodigoAndEstadoTrue("FALSO")).thenReturn(Optional.empty());

        // WHEN & THEN: Comprobamos que salte el error de negocio de Héctor
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            promocionService.validarYObtenerDescuento("falso");
        });

        assertEquals("El código de descuento no existe o no está activo en este momento.", excepcion.getMessage());
        verify(promocionRepository, times(1)).findByCodigoAndEstadoTrue("FALSO");
    }

    @Test
    void testCrearPromocion_ConvierteMayusculas() {
        // GIVEN
        Promocion promoNueva = new Promocion();
        promoNueva.setCodigo("navidad2026");
        
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promoNueva);

        // WHEN
        Promocion resultado = promocionService.crearPromocion(promoNueva);

        // THEN
        // Verificamos que el service modificó el objeto antes de guardarlo
        assertEquals("NAVIDAD2026", resultado.getCodigo());
        verify(promocionRepository, times(1)).save(promoNueva);
    }

    @Test
    void testActivarPromocion_Exito() {
        // GIVEN: Una promoción apagada
        Promocion promo = crearPromocionPrueba();
        promo.setEstado(false);
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promo);

        // WHEN
        Promocion resultado = promocionService.activarPromocion(1L);

        // THEN
        assertTrue(resultado.getEstado()); // Verificamos que ahora es true
        verify(promocionRepository, times(1)).findById(1L);
        verify(promocionRepository, times(1)).save(promo);
    }
}