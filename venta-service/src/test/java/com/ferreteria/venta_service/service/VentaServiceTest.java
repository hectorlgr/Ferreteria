package com.ferreteria.venta_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ferreteria.venta_service.Dto.UsuarioDto;
import com.ferreteria.venta_service.exception.BadRequestException;
import com.ferreteria.venta_service.exception.ResourceNotFoundException;
import com.ferreteria.venta_service.model.DetalleVenta;
import com.ferreteria.venta_service.model.Venta;
import com.ferreteria.venta_service.repository.VentaRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private VentaService ventaService;

    private Venta ventaBase;

    @BeforeEach
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void setUp() {
        // Configuración básica para simular llamadas WebClient
        lenient().when(webClientBuilder.build()).thenReturn(webClient);

        // Mock para GET
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Mock para PUT
        lenient().when(webClient.put()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // Mock para POST
        lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);

        // Objeto de prueba
        ventaBase = new Venta();
        ventaBase.setId(1L);
        ventaBase.setUsuarioId(10L);
        ventaBase.setCostoDespacho(3500);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProductoId(100L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(5000); // Subtotal será 10000

        List<DetalleVenta> detalles = new ArrayList<>();
        detalles.add(detalle);
        ventaBase.setDetalles(detalles);
    }

    @Test
    void testObtenerTodas_RetornaListaVentas() {
        when(ventaRepository.findAll()).thenReturn(List.of(ventaBase));
        List<Venta> resultado = ventaService.obtenerTodas();
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorUsuario_RetornaListaVentas() {
        when(ventaRepository.findByUsuarioId(10L)).thenReturn(List.of(ventaBase));
        List<Venta> resultado = ventaService.obtenerPorUsuario(10L);
        assertEquals(1, resultado.size());
    }

    @Test
    void testObtenerPorId_Exito_RetornaVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaBase));
        Venta resultado = ventaService.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ventaService.obtenerPorId(99L));
    }

    @Test
    void testActualizarVenta_Exito() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaBase));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaBase);

        Venta actualizada = new Venta();
        actualizada.setUsuarioId(20L);
        actualizada.setTotal(15000);

        Venta resultado = ventaService.actualizarVenta(1L, actualizada);
        assertNotNull(resultado);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void testEliminarVenta_Exito() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaBase));
        ventaService.eliminarVenta(1L);
        verify(ventaRepository, times(1)).delete(ventaBase);
    }

    @Test
    void testObtenerPorRangoFechas_Exito() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);
        when(ventaRepository.findByFechaRango(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ventaBase));

        List<Venta> resultado = ventaService.obtenerPorRangoFechas(inicio, fin);
        assertEquals(1, resultado.size());
    }

    @Test
    void testObtenerPorRangoFechas_FalloFechasInvertidas_LanzaExcepcion() {
        LocalDate inicio = LocalDate.of(2024, 2, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);
        assertThrows(BadRequestException.class, () -> ventaService.obtenerPorRangoFechas(inicio, fin));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcesarVenta_CaminoFeliz_SinDescuento() {
        // GIVEN: Simular éxito en las 4 llamadas de red
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object())); // Usuario + Inventario
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty()); // Pedido
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Venta resultado = ventaService.procesarVenta(ventaBase, "Dir Test", null);

        // THEN
        assertNotNull(resultado);
        // Costo productos: 2 * 5000 = 10000.
        // IVA 19% = 1900. Total = 11900.
        // Despacho: 3500. Total Final = 15400.
        assertEquals(15400, resultado.getTotal());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcesarVenta_CaminoFeliz_ConDescuentoValido() {
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object())); // Usuario + Inventario
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty()); // Pedido

        // Simular validación de cupón exitosa
        Map<String, Double> mapDescuento = Map.of("descuento", 20.0);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(mapDescuento));

        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        Venta resultado = ventaService.procesarVenta(ventaBase, "Dir Test", "CUPON20");

        assertNotNull(resultado);
        // Costo inicial productos: 10000. Descuento 20% = -2000. Subtotal = 8000.
        // IVA 19% = 1520. Total = 9520.
        // Despacho: 3500. Total Final = 13020.
        assertEquals(13020, resultado.getTotal());
    }

    @Test
    void testProcesarVenta_UsuarioNoExiste_LanzaExcepcion() {
        // Fallo en la primera validación
        when(responseSpec.bodyToMono(Object.class))
                .thenThrow(WebClientResponseException.create(404, "Not Found", null, null, null));

        assertThrows(ResourceNotFoundException.class, () -> {
            ventaService.procesarVenta(ventaBase, "Dir Test", null);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcesarVenta_CuponInvalido_LanzaExcepcion() {
        // Usuario OK
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));
        // Cupón Falla
        when(responseSpec.bodyToMono(Map.class)).thenThrow(new RuntimeException("Error Promo"));

        assertThrows(BadRequestException.class, () -> {
            ventaService.procesarVenta(ventaBase, "Dir Test", "CUPON_FALSO");
        });
    }

    @Test
    void testProcesarVenta_InventarioFalla_LanzaExcepcion() {
        // Usuario OK
        when(responseSpec.bodyToMono(Object.class))
                .thenReturn(Mono.just(new Object())) // Para validación de usuario
                .thenThrow(new RuntimeException("Stock Insuficiente")); // Para inventario

        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        assertThrows(BadRequestException.class, () -> {
            ventaService.procesarVenta(ventaBase, "Dir Test", null);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcesarVenta_PedidoFalla_LanzaExcepcion() {
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        // Falla creación del pedido
        when(responseSpec.bodyToMono(Void.class)).thenThrow(new RuntimeException("Error Logística"));

        assertThrows(BadRequestException.class, () -> {
            ventaService.procesarVenta(ventaBase, "Dir Test", null);
        });
    }

    @Test
    void testObtenerVentasPorEmailUsuario_Exito() {
        UsuarioDto usuario = new UsuarioDto();
        usuario.setId(10L);
        usuario.setEmail("test@correo.com");

        when(responseSpec.bodyToMono(UsuarioDto.class)).thenReturn(Mono.just(usuario));
        when(ventaRepository.findByUsuarioId(10L)).thenReturn(List.of(ventaBase));

        List<Venta> resultado = ventaService.obtenerVentasPorEmailUsuario("test@correo.com");
        assertEquals(1, resultado.size());
    }

    @Test
    void testObtenerVentasPorEmailUsuario_Fallo_LanzaExcepcion() {
        when(responseSpec.bodyToMono(UsuarioDto.class)).thenThrow(new RuntimeException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> {
            ventaService.obtenerVentasPorEmailUsuario("falso@correo.com");
        });
    }
}