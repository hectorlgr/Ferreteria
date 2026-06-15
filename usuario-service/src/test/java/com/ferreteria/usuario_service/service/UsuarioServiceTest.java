package com.ferreteria.usuario_service.service;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Le decimos a JUnit que vamos a usar Mockito para crear "clones" (Mocks)
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    // 1. Creamos un clon (Mock) de la base de datos. ¡No tocará la BD real!
    @Mock
    private UsuarioRepository usuarioRepository;

    // 2. Inyectamos ese clon dentro de nuestro servicio real
    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    // Esto se ejecuta ANTES de cada prueba para tener datos frescos
    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario(1L, "Paulo Catalan", "paulo.catalan@duocuc.cl");
    }

    @Test
    void testGuardarUsuario_Exito() {
        // GIVEN (Dado): Le enseñamos al clon cómo debe responder
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        // WHEN (Cuando): Ejecutamos el método real que hizo Héctor
        Usuario resultado = usuarioService.guardarUsuario(new Usuario(null, "Paulo Catalan", "paulo.catalan@duocuc.cl"));

        // THEN (Entonces): Verificamos que el código hizo lo correcto
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Paulo Catalan", resultado.getNombre());
        
        // Verificamos que el repositorio intentó guardar exactamente 1 vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testObtenerPorId_Exito() {
        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        // WHEN
        Usuario resultado = usuarioService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals("paulo.catalan@duocuc.cl", resultado.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        // GIVEN: Le decimos al clon que la base de datos está vacía para ese ID
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN: Verificamos que al no encontrarlo, el código explote con la RuntimeException de Héctor
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerPorId(99L);
        });

        assertEquals("Error: Usuario no encontrado con el ID 99", excepcion.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
    }
}